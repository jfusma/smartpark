package com.usm.park.service;


import com.usm.park.exception.IntegrationErrorCode;
import com.usm.park.exception.IntegrationException;
import com.usm.park.model.ParkingSpot;
import com.usm.park.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;

    public ParkingSpot getAvailableParkingSpot(boolean isElectric) throws RuntimeException {
        List<Integer> spotsId = parkingSpotRepository.findByOccupiedFalseAndHasCharger(isElectric);
        if (spotsId.isEmpty()) {
            throw new IntegrationException(IntegrationErrorCode.PARKING_SPOT_UNAVAILABLE);
        }
        return parkingSpotRepository.findById(spotsId.get(0)).orElse(null);
    }

    public ParkingSpot save(ParkingSpot parkingSpot) {
        return parkingSpotRepository.save(parkingSpot);
    }
}
