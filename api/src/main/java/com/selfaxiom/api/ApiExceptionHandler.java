package com.selfaxiom.api;

import com.selfaxiom.api.auth.DuplicateUserException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(DuplicateUserException.class)
  public ResponseEntity<Map<String, Object>> handleDuplicateUser(DuplicateUserException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
        "timestamp", Instant.now().toString(),
        "status", HttpStatus.CONFLICT.value(),
        "error", "Conflict",
        "message", ex.getMessage(),
        "path", request.getRequestURI()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex,
      HttpServletRequest request) {
    Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (first, second) -> first));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
        "timestamp", Instant.now().toString(),
        "status", HttpStatus.BAD_REQUEST.value(),
        "error", "Bad Request",
        "message", "Validation failed",
        "fieldErrors", fieldErrors,
        "path", request.getRequestURI()));
  }
}
