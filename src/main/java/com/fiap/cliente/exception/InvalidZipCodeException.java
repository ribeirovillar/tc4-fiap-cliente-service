package com.fiap.cliente.exception;

public class InvalidZipCodeException extends RuntimeException {
    public InvalidZipCodeException(String zipCode) {
        super("Invalid zip code: " + zipCode);
    }
}
