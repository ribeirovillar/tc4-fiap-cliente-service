package com.fiap.cliente.gateway;

import com.fiap.cliente.domain.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerGateway {
    Customer save(Customer customer);
    Optional<Customer> findByCpf(String cpf);
    Optional<Customer> findById(UUID customerId);
    List<Customer> findAll();

    void delete(Customer customer);
}
