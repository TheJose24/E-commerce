package com.slamracing.ecommerce.exception;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            AuthenticationFailedException.class,
            TokenProcessingException.class
    })
    public ResponseEntity<String> handleAuthException(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), getStatusFromException(ex));
    }

    @ExceptionHandler({
            UserAlreadyExistsException.class
    })
    public ResponseEntity<String> handleUserAlreadyExistsException(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), getStatusFromException(ex));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleJwtException(JwtException e) {
        log.error("Error de JWT: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Sesión expirada o inválida. Por favor, inicie sesión nuevamente.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex) {
        return new ResponseEntity<>("Error interno del servidor. Por favor, intente más tarde.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpStatus getStatusFromException(RuntimeException ex) {
        if (ex instanceof HttpStatusProvider) {
            return ((HttpStatusProvider) ex).getHttpStatus();
        }
        return HttpStatus.BAD_REQUEST;  // Default status if none provided
    }
}

