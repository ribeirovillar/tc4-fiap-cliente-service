package com.fiap.cliente.controller.json;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class CustomerResponseDTO implements Serializable {
    private UUID id;
    private String fullName;
    private String cpf;
    private LocalDate birthDate;
    private AddressDTO address;
}
