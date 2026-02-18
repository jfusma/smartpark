package com.usm.park.exception;

public enum IntegrationErrorCode {

    VEHICLE_DOES_NOT_EXIST("2001", "Vehicle does not exist in the system."),
    VEHICLE_ALREADY_PARKED("2002", "Vehicle is already parked."),
    VEHICLE_NOT_PARKED("2003", "Vehicle is not currently parked."),
    PARKING_SPOT_UNAVAILABLE("2003", "No available parking spot for the vehicle.");

    private final String code;

    private final String description;

    IntegrationErrorCode(final String codeParameter, final String descriptionParameter) {
        this.code = codeParameter;
        this.description = descriptionParameter;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
