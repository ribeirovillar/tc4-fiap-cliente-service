package com.fiap.cliente.usecase;

import com.fiap.cliente.domain.Address;
import com.fiap.cliente.domain.Customer;
import com.fiap.cliente.exception.CustomerNotFoundException;
import com.fiap.cliente.gateway.CustomerGateway;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class RetrieveCustomerByIdUseCaseTest {

    @Mock
    CustomerGateway customerGateway;

    RetrieveCustomerByIdUseCase retrieveCustomerByIdUseCase;

    UUID existingCustomerId;
    Customer existingCustomer;

    @BeforeEach
    void setUp() {
        retrieveCustomerByIdUseCase = new RetrieveCustomerByIdUseCase(customerGateway);
        existingCustomerId = UUID.randomUUID();
        existingCustomer = new Customer(existingCustomerId, "Maria Silva", "12345678901",
                LocalDate.of(1990, 5, 15), null);
    }

    @Test
    void retrieveExistingCustomerSuccessfully() {
        when(customerGateway.findById(existingCustomerId)).thenReturn(Optional.of(existingCustomer));

        Customer retrievedCustomer = retrieveCustomerByIdUseCase.execute(existingCustomerId);

        assertNotNull(retrievedCustomer);
        assertEquals(existingCustomerId, retrievedCustomer.getId());
        assertEquals(existingCustomer.getFullName(), retrievedCustomer.getFullName());
        assertEquals(existingCustomer.getCpf(), retrievedCustomer.getCpf());
        verify(customerGateway).findById(existingCustomerId);
    }

    @Test
    void throwExceptionWhenCustomerNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(customerGateway.findById(nonExistentId)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> retrieveCustomerByIdUseCase.execute(nonExistentId));

        assertTrue(exception.getMessage().endsWith(nonExistentId.toString()));
        verify(customerGateway).findById(nonExistentId);
    }

    @Test
    void retrieveCustomerWithCompleteAddressSuccessfully() {
        Customer customerWithAddress = new Customer(existingCustomerId, "Ana Torres", "98765432100",
                LocalDate.of(1995, 3, 10),
                new Address("Rua Principal", "123", "São Paulo", "SP", "01234567"));

        when(customerGateway.findById(existingCustomerId)).thenReturn(Optional.of(customerWithAddress));

        Customer retrievedCustomer = retrieveCustomerByIdUseCase.execute(existingCustomerId);

        assertNotNull(retrievedCustomer);
        assertNotNull(retrievedCustomer.getAddress());
        assertEquals("Rua Principal", retrievedCustomer.getAddress().getStreet());
        assertEquals("São Paulo", retrievedCustomer.getAddress().getCity());
        assertEquals("SP", retrievedCustomer.getAddress().getState());
        verify(customerGateway).findById(existingCustomerId);
    }
}