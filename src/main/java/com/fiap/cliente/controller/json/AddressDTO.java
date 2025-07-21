package com.fiap.cliente.controller.json;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDTO {
    private String street;
    private String number;
    private String city;
    private String state;
    private String zipCode;
}
