package com.usm.park.repository;

import com.usm.park.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {
    @Query("SELECT v FROM Vehicle v WHERE v.plate = :licensePlate")
    Optional<Vehicle> findByPlate(String licensePlate);
}
