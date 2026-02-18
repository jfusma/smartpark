package com.usm.park.util;


import com.usm.park.exception.IntegrationException;
import com.usm.park.model.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
        MethodArgumentNotValidException ex
    ) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .collect(Collectors.toList());
        ApiErrorResponse apiError = new ApiErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ApiErrorResponse apiError = new ApiErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid request body",
            Collections.singletonList("Request body is missing or invalid JSON")
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        ApiErrorResponse apiError = new ApiErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal server error",
            Collections.singletonList(ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<ApiErrorResponse> integrationException(final HttpServletRequest req, final IntegrationException error) {
        ApiErrorResponse apiError = new ApiErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            error.getError().getDescription(),
            Collections.singletonList(error.getError().getCode())
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }
}
