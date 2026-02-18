package com.usm.park.service;


import com.usm.park.model.ParkingRate;
import com.usm.park.model.ParkingSpot;
import com.usm.park.model.ParkingStay;
import com.usm.park.model.Vehicle;
import com.usm.park.repository.ParkingStayRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingStayServiceTest {

    private static final String licensePlate = "ABC123";

    @Mock
    private ParkingStayRepository parkingStayRepository;

    @Mock
    private VehicleService vehicleService;

    @Mock
    private ParkingSpotService parkingSpotService;

    @Mock
    private ParkingRate parkingRate;

    @InjectMocks
    private ParkingStayService parkingStayService;

    private Vehicle vehicleResponse;

    private ParkingSpot parkingSpotResponse;

    private ParkingStay parkingStayResponse;

    @BeforeEach
    void setUp() {
        vehicleResponse = createVehicleMock();
        parkingSpotResponse = createParkingSpotMock(1);
        parkingStayResponse = buildStay(vehicleResponse, parkingSpotResponse);
        Mockito.clearInvocations(parkingStayRepository, vehicleService, parkingSpotService);
    }

    @Test
    void checkInVehicle_whenAllParametersAreValid_thenCheckInSuccessful() {
        // Given
        boolean isElectric = false;
        vehicleResponse = null;
        parkingSpotResponse = createParkingSpotMock(1);
        parkingStayResponse = createParkingStayMock();
        when(vehicleService.findByLicensePlate(anyString())).thenAnswer((Answer<Vehicle>) invocation -> vehicleResponse);
        when(parkingSpotService.getAvailableParkingSpot(anyBoolean())).thenAnswer((Answer<ParkingSpot>) invocation -> parkingSpotResponse);
        // When
        String result = parkingStayService.checkInVehicle(licensePlate, isElectric);
        // Then
        Assertions.assertEquals(ParkingStayService.CHECK_IN_SUCCESSFUL, result, "Expected check-in to be successful");
        Mockito.verify(vehicleService).findByLicensePlate(licensePlate);
        Mockito.verify(vehicleService).saveVehicle(any());
        Mockito.verify(parkingStayRepository).save(any());
    }

    @Test
    void checkInVehicle_whenVehicleExistPreviouslyAreValid_thenCheckInSuccessful() {
        // Given
        boolean isElectric = false;
        vehicleResponse = createVehicleMock();
        parkingSpotResponse = createParkingSpotMock(1);
        parkingStayResponse = createParkingStayMock();
        when(vehicleService.findByLicensePlate(anyString())).thenAnswer((Answer<Vehicle>) invocation -> vehicleResponse);
        when(parkingSpotService.getAvailableParkingSpot(anyBoolean())).thenAnswer((Answer<ParkingSpot>) invocation -> parkingSpotResponse);
        // When
        String result = parkingStayService.checkInVehicle(licensePlate, isElectric);
        // Then
        Assertions.assertEquals(ParkingStayService.CHECK_IN_SUCCESSFUL, result, "Expected check-in to be successful");
        Mockito.verify(vehicleService).findByLicensePlate(licensePlate);
        Mockito.verify(vehicleService).saveVehicle(any());
        Mockito.verify(parkingStayRepository).save(any());
    }

    @Test
    void checkInVehicle_whenVehicleExistPreviouslyIsParked_thenCheckInFailed() {
        // Given
        boolean isElectric = false;
        vehicleResponse = createVehicleMock();
        vehicleResponse.setParkingStatus(true);
        parkingSpotResponse = createParkingSpotMock(1);
        parkingStayResponse = createParkingStayMock();
        when(vehicleService.findByLicensePlate(anyString())).thenAnswer((Answer<Vehicle>) invocation -> vehicleResponse);
        // When
        IntegrationException exception = assertThrows(IntegrationException.class,
            () -> parkingStayService.checkInVehicle(licensePlate, isElectric),
            "Expected check-in to fail due to vehicle already parked");
        //then
        Assertions.assertEquals(
            IntegrationErrorCode.VEHICLE_ALREADY_PARKED.getCode(),
            exception.getError().getCode(),
            "Expected error code to indicate vehicle already parked");
        Mockito.verify(vehicleService).findByLicensePlate(licensePlate);
        Mockito.verify(vehicleService, Mockito.never()).saveVehicle(any());
        Mockito.verify(parkingStayRepository, Mockito.never()).save(any());
    }

    @Test
    void vehicleCheckOut_whenVehicleIsParked_thenCheckOutSuccessful() {
        // Given
        vehicleResponse = createVehicleMock();
        vehicleResponse.setParkingStatus(true);
        parkingSpotResponse = createParkingSpotMock(1);
        parkingStayResponse = createParkingStayMock();
        ParkingSpot oldParkingSpot = createParkingSpotMock(1);
        StayMovement movement = new StayMovement();
        movement.setId(1L);
        movement.setParkingSpot(oldParkingSpot);
        LocalDateTime startTime = LocalDateTime.now().minusHours(25);
        movement.setStartTime(startTime);
        movement.setLiquidated(false);
        parkingStayResponse.setMovements(List.of(movement));
        parkingStayResponse.setEntryTime(startTime);
        parkingStayResponse.setExitTime(LocalDateTime.now());
        long totalHours = 5L;
        when(vehicleService.findByLicensePlate(anyString())).thenAnswer((Answer<Vehicle>) invocation -> vehicleResponse);
        when(parkingStayRepository.findByVehiclePlateAndLiquidatedFalse(licensePlate)).thenAnswer((Answer<ParkingStay>) invocation -> parkingStayResponse);
        when(parkingRate.getTotalHours(isA(LocalDateTime.class), isA(LocalDateTime.class))).thenAnswer(invocation -> totalHours);
        when(parkingRate.calculateTotalCost(totalHours, false)).thenAnswer(invocation -> BigDecimal.valueOf(10));
        // When
        ParkingStayResumeDto result = parkingStayService.vehicleCheckOut(licensePlate);
        // Then
        Assertions.assertNotNull(result, "Expected check-out result to be not null");
        Mockito.verify(vehicleService).findByLicensePlate(licensePlate);
        Mockito.verify(vehicleService).saveVehicle(any());
        Mockito.verify(parkingStayRepository).save(any());
    }

    @Test
    void vehicleCheckOut_whenVehicleIsNotParked_thenCheckOutFailed() {
        // Given
        vehicleResponse = createVehicleMock();
        vehicleResponse.setParkingStatus(false);
        when(vehicleService.findByLicensePlate(anyString())).thenAnswer((Answer<Vehicle>) invocation -> vehicleResponse);
        // When
        IntegrationException exception = assertThrows(IntegrationException.class,
            () -> parkingStayService.vehicleCheckOut(licensePlate),
            "Expected check-out to fail due to vehicle not parked");
        // Then
        Assertions.assertEquals(
            IntegrationErrorCode.VEHICLE_DOES_NOT_EXIST.getCode(),
            exception.getError().getCode(),
            "Expected error code to indicate vehicle not parked");
        Mockito.verify(vehicleService).findByLicensePlate(licensePlate);
        Mockito.verify(vehicleService, Mockito.never()).saveVehicle(any());
        Mockito.verify(parkingStayRepository, Mockito.never()).save(any());
    }

    @Test
    void moveVehicle_whenVehicleIsParked_thenMoveSuccessful() {
        // Given
        vehicleResponse = createVehicleMock();
        vehicleResponse.setParkingStatus(true);
        parkingStayResponse = createParkingStayMock();
        ParkingSpot oldParkingSpot = createParkingSpotMock(1);
        StayMovement movement = new StayMovement();
        movement.setId(1L);
        movement.setParkingSpot(oldParkingSpot);
        LocalDateTime startTime = LocalDateTime.now().minusHours(25);
        movement.setStartTime(startTime);
        movement.setLiquidated(false);
        ParkingSpot newParkingSpot = createParkingSpotMock(2);
        parkingSpotResponse = newParkingSpot;
        List<StayMovement> movementList = new ArrayList<>();
        movementList.add(movement);
        parkingStayResponse.setMovements(movementList);
        parkingStayResponse.setEntryTime(startTime);
        when(parkingStayRepository.findByVehiclePlateAndLiquidatedFalse(licensePlate)).thenAnswer((Answer<ParkingStay>) invocation -> parkingStayResponse);
        when(parkingSpotService.getAvailableParkingSpot(anyBoolean())).thenAnswer((Answer<ParkingSpot>) invocation -> parkingSpotResponse);
        when(vehicleService.findByLicensePlate(anyString())).thenAnswer((Answer<Vehicle>) invocation -> vehicleResponse);
        // When
        String result = parkingStayService.moveVehicle(licensePlate);
        // Then
        Assertions.assertEquals(ParkingStayService.MOVE_SUCCESSFUL, result, "Expected move to be successful");
        Mockito.verify(vehicleService).findByLicensePlate(licensePlate);
        Mockito.verify(parkingStayRepository).findByVehiclePlateAndLiquidatedFalse(licensePlate);
    }

    @Test
    void moveVehicle_whenVehicleIsNotParked_thenMoveFailed() {
        // Given
        vehicleResponse = createVehicleMock();
        vehicleResponse.setParkingStatus(false);
        when(vehicleService.findByLicensePlate(anyString())).thenAnswer((Answer<Vehicle>) invocation -> vehicleResponse);
        // When
        IntegrationException exception = assertThrows(IntegrationException.class,
            () -> parkingStayService.moveVehicle(licensePlate),
            "Expected move to fail due to vehicle not parked");
        // Then
        Assertions.assertEquals(
            IntegrationErrorCode.VEHICLE_DOES_NOT_EXIST.getCode(),
            exception.getError().getCode(),
            "Expected error code to indicate vehicle not parked");
        Mockito.verify(vehicleService).findByLicensePlate(licensePlate);
        Mockito.verify(parkingStayRepository, Mockito.never()).findByVehiclePlateAndLiquidatedFalse(anyString());
    }

    @Test
    void moveVehicle_whenNoAvailableSpot_thenMoveFailed() {
        // Given
        vehicleResponse = createVehicleMock();
        vehicleResponse.setParkingStatus(true);
        parkingStayResponse = createParkingStayMock();
        ParkingSpot oldParkingSpot = createParkingSpotMock(1);
        StayMovement movement = new StayMovement();
        movement.setId(1L);
        movement.setParkingSpot(oldParkingSpot);
        LocalDateTime startTime = LocalDateTime.now().minusHours(25);
        movement.setStartTime(startTime);
        movement.setLiquidated(false);
        List<StayMovement> movementList = new ArrayList<>();
        movementList.add(movement);
        parkingStayResponse.setMovements(movementList);
        parkingStayResponse.setEntryTime(startTime);
        when(parkingStayRepository.findByVehiclePlateAndLiquidatedFalse(licensePlate)).thenAnswer((Answer<ParkingStay>) invocation -> parkingStayResponse);
        when(parkingSpotService.getAvailableParkingSpot(anyBoolean())).thenAnswer((Answer<ParkingSpot>) invocation -> null);
        when(vehicleService.findByLicensePlate(anyString())).thenAnswer((Answer<Vehicle>) invocation -> vehicleResponse);
        // When
        IntegrationException exception = assertThrows(IntegrationException.class,
            () -> parkingStayService.moveVehicle(licensePlate),
            "Expected move to fail due to no available parking spot");
        // Then
        Assertions.assertEquals(
            IntegrationErrorCode.PARKING_SPOT_UNAVAILABLE.getCode(),
            exception.getError().getCode(),
            "Expected error code to indicate no available parking spot");
        Mockito.verify(vehicleService).findByLicensePlate(licensePlate);
        Mockito.verify(parkingStayRepository).findByVehiclePlateAndLiquidatedFalse(licensePlate);
    }

    private Vehicle createVehicleMock() {
        Vehicle vehicle = new Vehicle();
        vehicle.setPlate(licensePlate);
        vehicle.setParkingStatus(false);
        return vehicle;
    }

    private ParkingSpot createParkingSpotMock(Integer id) {
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setId(id != null ? id : 1);
        parkingSpot.setHasCharger(false);
        parkingSpot.setOccupied(false);
        return parkingSpot;
    }

    private ParkingStay createParkingStayMock() {
        ParkingStay parkingStay = new ParkingStay();
        parkingStay.setId(1L);
        parkingStay.setTotalCost(BigDecimal.ZERO);
        return parkingStay;
    }

    private ParkingStay buildStay(Vehicle vehicle, ParkingSpot spot) {
        ParkingStay ps = new ParkingStay();
        ps.setVehicle(vehicle);
        ps.setEntryTime(LocalDateTime.now().minusHours(2));
        ps.setLiquidated(false);
        StayMovement movement = new StayMovement();
        movement.setParkingStay(ps);
        movement.setParkingSpot(spot);
        movement.setStartTime(ps.getEntryTime());
        movement.setLiquidated(false);
        ps.setMovements(List.of(movement));
        return ps;
    }
}
