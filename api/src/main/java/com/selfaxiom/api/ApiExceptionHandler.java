package com.selfaxiom.api;

import com.selfaxiom.api.auth.AuthExceptions.DuplicateUserException;
import com.selfaxiom.api.auth.AuthExceptions.InvalidCredentialsException;
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
  public ResponseEntity<Map<String, Object>> handleDuplicateUser(DuplicateUserException ex,
      HttpServletRequest request) {
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

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
        "timestamp", Instant.now().toString(),
        "status", HttpStatus.UNAUTHORIZED.value(),
        "error", "Unauthorized",
        "message", ex.getMessage(),
        "path", request.getRequestURI()));
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
        "timestamp", Instant.now().toString(),
        "status", HttpStatus.NOT_FOUND.value(),
        "error", "Not Found",
        "message", ex.getMessage(),
        "path", request.getRequestURI()));
  }
}
