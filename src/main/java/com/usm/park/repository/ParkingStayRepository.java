package com.usm.park.repository;

import com.usm.park.model.ParkingStay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ParkingStayRepository extends JpaRepository<ParkingStay, Long>, JpaSpecificationExecutor<ParkingStay> {

    ParkingStay findByVehiclePlateAndLiquidatedFalse(String licensePlate);
}
