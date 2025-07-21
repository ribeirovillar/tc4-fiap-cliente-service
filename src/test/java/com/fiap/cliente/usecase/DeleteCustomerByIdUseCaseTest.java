package com.fiap.cliente.usecase;

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
class DeleteCustomerByIdUseCaseTest {

    @Mock
    CustomerGateway customerGateway;

    DeleteCustomerByIdUseCase deleteCustomerByIdUseCase;

    UUID existingCustomerId;
    Customer existingCustomer;

    @BeforeEach
    void setUp() {
        deleteCustomerByIdUseCase = new DeleteCustomerByIdUseCase(customerGateway);
        existingCustomerId = UUID.randomUUID();
        existingCustomer = new Customer(existingCustomerId, "Joao Silva", "12345678901", LocalDate.of(1990, 5, 15), null);
    }

    @Test
    void deleteExistingCustomerSuccessfully() {
        when(customerGateway.findById(existingCustomerId)).thenReturn(Optional.of(existingCustomer));

        deleteCustomerByIdUseCase.execute(existingCustomerId);

        verify(customerGateway).delete(existingCustomer);
    }

    @Test
    void throwExceptionWhenCustomerNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(customerGateway.findById(nonExistentId)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> deleteCustomerByIdUseCase.execute(nonExistentId));

        assertTrue(exception.getMessage().endsWith(nonExistentId.toString()));
        verify(customerGateway, never()).delete(any());
    }

}