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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UpdateCustomerUseCaseTest {

    @Mock
    CustomerGateway customerGateway;

    UpdateCustomerUseCase updateCustomerUseCase;

    UUID existingCustomerId;
    Customer existingCustomer;
    Customer updatedCustomer;

    @BeforeEach
    void setUp() {
        updateCustomerUseCase = new UpdateCustomerUseCase(customerGateway);
        existingCustomerId = UUID.randomUUID();
        existingCustomer = new Customer(existingCustomerId, "Joao Silva", "12345678901",
                LocalDate.of(1990, 5, 15), null);
        updatedCustomer = new Customer(existingCustomerId, "Joao Silva Updated", "12345678901",
                LocalDate.of(1990, 5, 15),
                new Address("Rua Nova", "456", "Rio de Janeiro", "RJ", "21000123"));
    }

    @Test
    void updateExistingCustomerSuccessfully() {
        when(customerGateway.findById(existingCustomerId)).thenReturn(Optional.of(existingCustomer));
        when(customerGateway.save(updatedCustomer)).thenReturn(updatedCustomer);

        updateCustomerUseCase.execute(updatedCustomer);

        verify(customerGateway).findById(existingCustomerId);
        verify(customerGateway).save(updatedCustomer);
    }

    @Test
    void throwExceptionWhenCustomerNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        Customer nonExistentCustomer = new Customer(nonExistentId, "Non Existent", "98765432100",
                LocalDate.of(1995, 10, 20), null);

        when(customerGateway.findById(nonExistentId)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> updateCustomerUseCase.execute(nonExistentCustomer));

        assertTrue(exception.getMessage().endsWith(nonExistentId.toString()));
        verify(customerGateway).findById(nonExistentId);
        verify(customerGateway, never()).save(any());
    }

    @Test
    void updateCustomerWithChangedName() {
        Customer nameUpdatedCustomer = new Customer(existingCustomerId, "New Name",
                existingCustomer.getCpf(), existingCustomer.getBirthDate(), existingCustomer.getAddress());

        when(customerGateway.findById(existingCustomerId)).thenReturn(Optional.of(existingCustomer));
        when(customerGateway.save(nameUpdatedCustomer)).thenReturn(nameUpdatedCustomer);

        updateCustomerUseCase.execute(nameUpdatedCustomer);

        verify(customerGateway).save(nameUpdatedCustomer);
    }

    @Test
    void updateCustomerWithNewAddress() {
        when(customerGateway.findById(existingCustomerId)).thenReturn(Optional.of(existingCustomer));
        when(customerGateway.save(updatedCustomer)).thenReturn(updatedCustomer);

        updateCustomerUseCase.execute(updatedCustomer);

        verify(customerGateway).save(updatedCustomer);
    }

    @Test
    void propagateExceptionWhenSaveFails() {
        RuntimeException expectedError = new RuntimeException("Database error");
        when(customerGateway.findById(existingCustomerId)).thenReturn(Optional.of(existingCustomer));
        when(customerGateway.save(updatedCustomer)).thenThrow(expectedError);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> updateCustomerUseCase.execute(updatedCustomer));

        assertEquals(expectedError, exception);
        verify(customerGateway).findById(existingCustomerId);
        verify(customerGateway).save(updatedCustomer);
    }
}