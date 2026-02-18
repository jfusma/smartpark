package com.usm.park.service;

import com.usm.park.model.Vehicle;
import com.usm.park.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public Vehicle findByLicensePlate(String licensePlate) {
        return vehicleRepository.findByPlate(licensePlate).orElse(null);
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }
}