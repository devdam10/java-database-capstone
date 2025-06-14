//package com.project.back_end.controllers;
//
//public class ValidationFailed {
//
//// 1. Set Up the Global Exception Handler:
////    - Annotate the class with `@RestControllerAdvice` to apply it globally across all controllers.
////    - This class is responsible for handling exceptions and customizing error responses uniformly.
//
//
//// 2. Define the `handleValidationException` Method:
////    - Annotate with `@ExceptionHandler(MethodArgumentNotValidException.class)` to intercept validation exceptions thrown when a request body fails `@Valid` checks.
////    - Iterates through all field validation errors from the exception.
////    - Extracts and collects default error messages (e.g., "Email is required", "Invalid phone number").
////    - Constructs a response map containing the error message under the `"message"` key.
////    - Returns a `ResponseEntity` with HTTP 400 Bad Request status and the error message in the body.
//
//
//}

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

@RestControllerAdvice
public class ValidationFailed {

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
