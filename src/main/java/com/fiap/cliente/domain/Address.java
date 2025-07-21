package com.fiap.cliente.domain;

import com.fiap.cliente.exception.InvalidZipCodeException;
import lombok.Getter;

@Getter
public class Address {

    private String street;
    private String number;
    private String city;
    private String state;
    private String zipCode;

    public Address(String street, String number, String city, String state, String zipCode) {
        validateZipCode(zipCode);
        this.street = street;
        this.number = number;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    private static void validateZipCode(String zipCode) {
        if (zipCode == null || zipCode.length() < 5) {
            throw new InvalidZipCodeException(zipCode);
        }
    }

}
