package com.fiap.cliente.usecase;

import com.fiap.cliente.domain.Address;
import com.fiap.cliente.domain.Customer;
import com.fiap.cliente.gateway.CustomerGateway;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class RetrieveAllCustomersUseCaseTest {

    @Mock
    CustomerGateway customerGateway;

    RetrieveAllCustomersUseCase retrieveAllCustomersUseCase;

    @BeforeEach
    void setUp() {
        retrieveAllCustomersUseCase = new RetrieveAllCustomersUseCase(customerGateway);
    }

    @Test
    void retrieveAllCustomersSuccessfully() {
        List<Customer> customers = List.of(
                new Customer(UUID.randomUUID(), "Joao Silva", "12345678901", LocalDate.of(1990, 5, 15), null),
                new Customer(UUID.randomUUID(), "Eduardo Ramos", "98765432109", LocalDate.of(1985, 3, 20), null)
        );

        when(customerGateway.findAll()).thenReturn(customers);

        List<Customer> result = retrieveAllCustomersUseCase.execute();

        assertEquals(2, result.size());
        assertEquals(customers, result);
        verify(customerGateway).findAll();
    }

    @Test
    void retrieveEmptyListWhenNoCustomers() {
        when(customerGateway.findAll()).thenReturn(Collections.emptyList());

        List<Customer> result = retrieveAllCustomersUseCase.execute();

        assertTrue(result.isEmpty());
        verify(customerGateway).findAll();
    }

    @Test
    void retrieveCustomersWithCompleteInformation() {
        Address address = new Address("Rua Principal", "123", "São Paulo", "SP", "01234567");
        List<Customer> customers = List.of(
                new Customer(UUID.randomUUID(), "Maria Silva", "12345678901", LocalDate.of(1990, 5, 15), address)
        );

        when(customerGateway.findAll()).thenReturn(customers);

        List<Customer> result = retrieveAllCustomersUseCase.execute();

        assertEquals(1, result.size());
        assertNotNull(result.getFirst().getAddress());
        assertEquals("Rua Principal", result.getFirst().getAddress().getStreet());
        assertEquals("São Paulo", result.getFirst().getAddress().getCity());
    }
}