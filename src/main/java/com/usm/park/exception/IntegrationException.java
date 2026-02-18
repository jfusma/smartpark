package com.usm.park.exception;


public class IntegrationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final IntegrationErrorCode error;

    private final String field;

    private final String rejectedValue;

    public IntegrationException(IntegrationErrorCode error, String field, String rejectedValue) {
        this.error = error;
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    public IntegrationException(IntegrationErrorCode error) {
        super(error.getDescription());
        this.error = error;
        this.field = null;
        this.rejectedValue = null;
    }

    public IntegrationErrorCode getError() {
        return error;
    }

    public String getField() {
        return field;
    }

    public String getRejectedValue() {
        return rejectedValue;
    }
}
