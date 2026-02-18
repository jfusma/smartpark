package com.usm.park.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParkingStayIncomingRequestDto {

    @NotBlank(message = "License plate must not be blank")
    @Size(max = 10, message = "License plate must not exceed 10 characters")
    private String licensePlate;

    private boolean chargerRequired;
}
