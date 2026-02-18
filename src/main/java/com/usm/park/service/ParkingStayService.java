package com.usm.park.service;

import com.usm.park.exception.IntegrationErrorCode;
import com.usm.park.exception.IntegrationException;
import com.usm.park.model.ParkingRate;
import com.usm.park.model.ParkingSpot;
import com.usm.park.model.ParkingStay;
import com.usm.park.model.StayMovement;
import com.usm.park.model.Vehicle;
import com.usm.park.model.dto.ParkingStayResumeDto;
import com.usm.park.repository.ParkingStayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;

@Slf4j
@RequiredArgsConstructor
@Service
public class ParkingStayService {

    public static final String CHECK_IN_SUCCESSFUL = "Check-in successful";

    public static final String MOVE_SUCCESSFUL = "Vehicle moved successfully";

    private final ParkingStayRepository parkingStayRepository;

    private final VehicleService vehicleService;

    private final ParkingSpotService parkingSpotService;

    private final ParkingRate parkingRate;

    private static boolean isUsedCharger(ParkingStay parkingStay) {
        return parkingStay.getMovements().stream().anyMatch(movement -> movement.getParkingSpot().isHasCharger());
    }

    private static ParkingSpot getSpotToFree(ParkingStay parkingStay) {
        return parkingStay.getMovements().stream()
            .max(Comparator.comparing(StayMovement::getStartTime))
            .map(StayMovement::getParkingSpot)
            .orElseThrow();
    }

    public String checkInVehicle(String licensePlate, boolean isElectric) {
        var vehicle = vehicleService.findByLicensePlate(licensePlate);
        if (vehicle == null) {
            vehicle = new Vehicle(licensePlate, true);
        } else {
            if (vehicle.isParkingStatus()) {
                log.warn("Vehicle with license plate {} is already registered and active", licensePlate);
                throw new IntegrationException(IntegrationErrorCode.VEHICLE_ALREADY_PARKED, "licensePlate", licensePlate);
            } else {
                vehicle.setParkingStatus(true);
            }
        }
        vehicleService.saveVehicle(vehicle);
        allocateParkingSpot(vehicle, isElectric);
        log.info("Vehicle with license plate {} checked in successfully", licensePlate);
        return CHECK_IN_SUCCESSFUL;
    }

    private void allocateParkingSpot(Vehicle vehicle, boolean isElectric) {
        ParkingSpot parkingSpot = findAndOccupyParkingSpot(isElectric);
        ParkingStay parkingStay = new ParkingStay();
        parkingStay.setVehicle(vehicle);
        parkingStay.setEntryTime(LocalDateTime.now());
        parkingStay.setTotalCost(BigDecimal.ZERO);
        StayMovement newMovement = new StayMovement();
        newMovement.setParkingStay(parkingStay);
        newMovement.setParkingSpot(parkingSpot);
        newMovement.setStartTime(parkingStay.getEntryTime());
        newMovement.setLiquidated(false);
        parkingStay.addMovement(newMovement);
        parkingStayRepository.save(parkingStay);
    }

    private ParkingSpot findAndOccupyParkingSpot(boolean isElectric) {
        ParkingSpot parkingSpot = parkingSpotService.getAvailableParkingSpot(isElectric);
        parkingSpot.setOccupied(true);
        parkingSpot = parkingSpotService.save(parkingSpot);
        return parkingSpot;
    }

    public ParkingStayResumeDto vehicleCheckOut(String vehiclePlate) {
        Vehicle vehicle = getAValidVehicle(vehiclePlate);
        LocalDateTime finalTime = LocalDateTime.now();
        ParkingStay parkingStay = parkingStayRepository.findByVehiclePlateAndLiquidatedFalse(vehiclePlate);
        parkingStay.getMovements().forEach(movement -> {
            movement.setEndTime(finalTime);
            movement.setLiquidated(true);
        });
        vehicle.setParkingStatus(false);
        ParkingSpot spotToFree = getSpotToFree(parkingStay);
        spotToFree.setOccupied(false);
        boolean usedCharger = isUsedCharger(parkingStay);
        parkingStay.setExitTime(finalTime);
        parkingStay.setLiquidated(true);
        long totalTime = parkingRate.getTotalHours(parkingStay.getEntryTime(), parkingStay.getExitTime());
        BigDecimal totalCost = parkingRate.calculateTotalCost(totalTime, usedCharger);
        parkingStay.setTotalCost(totalCost);
        parkingSpotService.save(spotToFree);
        parkingStayRepository.save(parkingStay);
        vehicleService.saveVehicle(vehicle);
        log.info("Vehicle with license plate {} checked out successfully. Total cost: {}", vehiclePlate, totalCost);
        return new ParkingStayResumeDto(vehiclePlate, parkingStay.getEntryTime(), parkingStay.getExitTime(), totalCost, totalTime);
    }

    public String moveVehicle(String vehiclePlate) {
        getAValidVehicle(vehiclePlate);
        ParkingStay parkingStay = parkingStayRepository.findByVehiclePlateAndLiquidatedFalse(vehiclePlate);
        if (parkingStay == null) {
            throw new IntegrationException(IntegrationErrorCode.VEHICLE_NOT_PARKED, "licensePlate", vehiclePlate);
        }
        StayMovement lastMovement = parkingStay.getMovements().stream()
            .max(Comparator.comparing(StayMovement::getStartTime))
            .orElseThrow();
        ParkingSpot currentSpot = lastMovement.getParkingSpot();
        ParkingSpot newSpot = parkingSpotService.getAvailableParkingSpot(currentSpot.isHasCharger());
        if (newSpot == null) {
            throw new IntegrationException(IntegrationErrorCode.PARKING_SPOT_UNAVAILABLE, "licensePlate", vehiclePlate);
        }
        newSpot.setOccupied(true);
        parkingSpotService.save(newSpot);
        lastMovement.setEndTime(LocalDateTime.now());
        lastMovement.setLiquidated(true);
        currentSpot.setOccupied(false);
        parkingSpotService.save(currentSpot);
        StayMovement newMovement = new StayMovement();
        newMovement.setParkingStay(parkingStay);
        newMovement.setParkingSpot(newSpot);
        newMovement.setStartTime(LocalDateTime.now());
        newMovement.setLiquidated(false);
        parkingStay.addMovement(newMovement);
        parkingStayRepository.save(parkingStay);
        log.info("Vehicle with license plate " + vehiclePlate + " moved successfully to new parking spot " + newSpot.getId());
        return MOVE_SUCCESSFUL;
    }

    private Vehicle getAValidVehicle(String vehiclePlate) {
        Vehicle vehicle = vehicleService.findByLicensePlate(vehiclePlate);
        if (vehicle == null || !vehicle.isParkingStatus()) {
            log.warn("Vehicle with license plate {} is not currently parked", vehiclePlate);
            throw new IntegrationException(IntegrationErrorCode.VEHICLE_DOES_NOT_EXIST, "licensePlate", vehiclePlate);
        }
        return vehicle;
    }
}