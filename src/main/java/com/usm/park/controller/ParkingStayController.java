package com.usm.park.controller;

import com.usm.park.model.dto.ParkingStayIncomingRequestDto;
import com.usm.park.model.dto.ParkingStayResumeDto;
import com.usm.park.model.incoming.AppResponse;
import com.usm.park.service.ParkingStayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/parking-stays")
public class ParkingStayController {

    private final ParkingStayService parkingStayService;

    @PostMapping("/vehicles/check-in")
    @ResponseStatus(HttpStatus.CREATED)
    public AppResponse vehicleParkingStayCheckIn(@Validated @RequestBody ParkingStayIncomingRequestDto inRequestDto) {
        final String result = parkingStayService.checkInVehicle(inRequestDto.getLicensePlate(), inRequestDto.isChargerRequired());
        return new AppResponse(result);
    }

    @PostMapping("/vehicles/check-out")
    @ResponseStatus(HttpStatus.OK)
    public ParkingStayResumeDto vehicleParkingStayCheckOut(@Validated @RequestBody ParkingStayIncomingRequestDto inRequestDto) {
        return parkingStayService.vehicleCheckOut(inRequestDto.getLicensePlate());
    }

    @PostMapping("/vehicles/move")
    @ResponseStatus(HttpStatus.OK)
    public AppResponse moveVehicle(@Validated @RequestBody ParkingStayIncomingRequestDto inRequestDto) {
        final String result = parkingStayService.moveVehicle(inRequestDto.getLicensePlate());
        return new AppResponse(result);
    }
}
