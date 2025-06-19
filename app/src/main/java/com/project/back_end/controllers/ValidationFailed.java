package com.project.back_end.controllers;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for validation errors in the application.
 * Catches MethodArgumentNotValidException and formats the error messages.
 */
@RestControllerAdvice
public class ValidationFailed {

    /**
     * Handles MethodArgumentNotValidException and formats the error messages.
     *
     * @param ex the exception containing validation errors
     * @return ResponseEntity with a map of error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> response = new HashMap<>();

        String errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        response.put("message", errorMessages);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
