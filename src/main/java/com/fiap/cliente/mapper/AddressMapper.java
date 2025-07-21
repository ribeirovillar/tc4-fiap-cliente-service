package com.fiap.cliente.mapper;

import com.fiap.cliente.controller.json.AddressDTO;
import com.fiap.cliente.domain.Address;
import com.fiap.cliente.gateway.database.jpa.entity.AddressEmbeddable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressEmbeddable toEmbeddable(Address address);

    Address toAddress(AddressEmbeddable embeddable);

    Address toAddress(AddressDTO dto);

    AddressDTO toAddressDTO(Address address);

}
