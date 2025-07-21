package com.fiap.cliente.gateway.database.jpa;


import com.fiap.cliente.domain.Customer;
import com.fiap.cliente.gateway.CustomerGateway;
import com.fiap.cliente.gateway.database.jpa.entity.CustomerEntity;
import com.fiap.cliente.gateway.database.jpa.repository.CustomerRepository;
import com.fiap.cliente.mapper.CustomerMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerJpaGateway implements CustomerGateway {

    CustomerRepository customerRepository;
    CustomerMapper customerMapper;

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = customerMapper.toEntity(customer);
        return customerMapper.toDomain(customerRepository.save(entity));
    }

    @Override
    public Optional<Customer> findByCpf(String cpf) {
        return customerRepository.findByCpf(cpf)
                .map(customerMapper::toDomain);
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        return customerRepository.findById(customerId)
                .map(customerMapper::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll()
                .stream().map(customerMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Customer customer) {
        customerRepository.delete(customerMapper.toEntity(customer));
    }
}
