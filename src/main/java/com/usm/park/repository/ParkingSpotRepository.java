package com.usm.park.repository;

import com.usm.park.model.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Integer>, JpaSpecificationExecutor<ParkingSpot> {

    @Query("SELECT p.id FROM ParkingSpot p WHERE p.occupied = false AND p.hasCharger = :hasChargerParam")
    List<Integer> findByOccupiedFalseAndHasCharger(boolean hasChargerParam);
}
