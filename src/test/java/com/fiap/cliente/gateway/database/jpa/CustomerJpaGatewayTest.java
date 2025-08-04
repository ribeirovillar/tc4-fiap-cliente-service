package com.fiap.cliente.gateway.database.jpa;

import com.fiap.cliente.domain.Address;
import com.fiap.cliente.domain.Customer;
import com.fiap.cliente.gateway.database.jpa.entity.CustomerEntity;
import com.fiap.cliente.gateway.database.jpa.repository.CustomerRepository;
import com.fiap.cliente.mapper.CustomerMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class CustomerJpaGatewayTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    CustomerMapper customerMapper;

    CustomerJpaGateway customerJpaGateway;

    Customer customer;
    CustomerEntity customerEntity;

    @BeforeEach
    void setUp() {
        customerJpaGateway = new CustomerJpaGateway(customerRepository, customerMapper);

        Address address = new Address("Rua A", "123", "São Paulo", "SP", "01234567");
        customer = new Customer(UUID.randomUUID(), "Maria Silva", "12345678901",
                               LocalDate.of(1990, 5, 15), address);
        customerEntity = new CustomerEntity();
    }

    @Test
    void save() {
        when(customerMapper.toEntity(customer)).thenReturn(customerEntity);
        when(customerRepository.save(customerEntity)).thenReturn(customerEntity);
        when(customerMapper.toDomain(customerEntity)).thenReturn(customer);

        Customer result = customerJpaGateway.save(customer);

        assertEquals(customer, result);
        verify(customerMapper).toEntity(customer);
        verify(customerRepository).save(customerEntity);
        verify(customerMapper).toDomain(customerEntity);
    }

    @Test
    void findByCpfWhenExists() {
        String cpf = "12345678901";
        when(customerRepository.findByCpf(cpf)).thenReturn(Optional.of(customerEntity));
        when(customerMapper.toDomain(customerEntity)).thenReturn(customer);

        Optional<Customer> result = customerJpaGateway.findByCpf(cpf);

        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
        verify(customerRepository).findByCpf(cpf);
        verify(customerMapper).toDomain(customerEntity);
    }

    @Test
    void findByCpfWhenNotExists() {
        String cpf = "12345678901";
        when(customerRepository.findByCpf(cpf)).thenReturn(Optional.empty());

        Optional<Customer> result = customerJpaGateway.findByCpf(cpf);

        assertFalse(result.isPresent());
        verify(customerRepository).findByCpf(cpf);
        verify(customerMapper, never()).toDomain(any(CustomerEntity.class));
    }

    @Test
    void findByIdWhenExists() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));
        when(customerMapper.toDomain(customerEntity)).thenReturn(customer);

        Optional<Customer> result = customerJpaGateway.findById(customerId);

        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
        verify(customerRepository).findById(customerId);
        verify(customerMapper).toDomain(customerEntity);
    }

    @Test
    void findByIdWhenNotExists() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        Optional<Customer> result = customerJpaGateway.findById(customerId);

        assertFalse(result.isPresent());
        verify(customerRepository).findById(customerId);
        verify(customerMapper, never()).toDomain(any(CustomerEntity.class));
    }

    @Test
    void findAll() {
        List<CustomerEntity> entities = List.of(customerEntity);
        when(customerRepository.findAll()).thenReturn(entities);
        when(customerMapper.toDomain(customerEntity)).thenReturn(customer);

        List<Customer> result = customerJpaGateway.findAll();

        assertEquals(1, result.size());
        assertEquals(customer, result.get(0));
        verify(customerRepository).findAll();
        verify(customerMapper).toDomain(customerEntity);
    }

    @Test
    void delete() {
        when(customerMapper.toEntity(customer)).thenReturn(customerEntity);

        customerJpaGateway.delete(customer);

        verify(customerMapper).toEntity(customer);
        verify(customerRepository).delete(customerEntity);
    }
}
