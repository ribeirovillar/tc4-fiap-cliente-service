package com.fiap.cliente.mapper;

import com.fiap.cliente.controller.json.CustomerRequestDTO;
import com.fiap.cliente.controller.json.CustomerResponseDTO;
import com.fiap.cliente.domain.Customer;
import com.fiap.cliente.gateway.database.jpa.entity.CustomerEntity;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface CustomerMapper {

    CustomerEntity toEntity(Customer customer);

    Customer toDomain(CustomerEntity entity);

    Customer toDomain(CustomerRequestDTO dto);

    Customer toDomain(UUID id, CustomerRequestDTO dto);

    CustomerResponseDTO toResponse(Customer customer);

}

