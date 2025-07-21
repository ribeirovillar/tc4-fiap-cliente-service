package com.fiap.cliente.infra;

import com.fiap.cliente.exception.CpfAlreadyInRegisteredException;
import com.fiap.cliente.exception.CustomerNotFoundException;
import com.fiap.cliente.exception.InvalidCpfException;
import com.fiap.cliente.exception.InvalidZipCodeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({InvalidCpfException.class, InvalidZipCodeException.class})
    public ResponseEntity<ErrorResponse> handleInvalidException(Exception ex) {
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(CpfAlreadyInRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleCpfAlreadyInRegisteredException(CpfAlreadyInRegisteredException ex) {
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.CONFLICT.getReasonPhrase(), ex.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    public record ErrorResponse(String reason, String message) {
    }
}