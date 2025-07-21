package com.fiap.cliente.domain;

import com.fiap.cliente.exception.InvalidCpfException;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class Customer {

    private UUID id;
    private String fullName;
    private String cpf;
    private LocalDate birthDate;
    private Address address;

    public Customer(UUID id, String fullName, String cpf, LocalDate birthDate, Address address) {
        validateCpf(cpf);
        this.id = id;
        this.fullName = fullName;
        this.cpf = cpf;
        this.birthDate = birthDate;
        this.address = address;
    }

    private void validateCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            throw new InvalidCpfException(cpf);
        }
    }

}
