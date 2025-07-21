package com.fiap.cliente.gateway.database.jpa.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AddressEmbeddable {

    private String street;
    private String number;
    private String city;
    private String state;
    private String zipCode;
}
