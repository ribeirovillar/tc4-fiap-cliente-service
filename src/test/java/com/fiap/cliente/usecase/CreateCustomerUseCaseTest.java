package com.fiap.cliente.usecase;

import com.fiap.cliente.domain.Address;
import com.fiap.cliente.domain.Customer;
import com.fiap.cliente.exception.CpfAlreadyInRegisteredException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class CreateCustomerUseCaseTest {

    @Mock
    CustomerGateway customerGateway;

    CreateCustomerUseCase createCustomerUseCase;

    Customer newCustomer;

    Customer existingCustomer;

    @BeforeEach
    void setUp() {
        createCustomerUseCase = new CreateCustomerUseCase(customerGateway);
        Address address = new Address("Rua A", "123", "São Paulo", "SP", "01234567");
        newCustomer = new Customer(null, "Maria Silva", "12345678901", LocalDate.of(1990, 5, 15), address);
        existingCustomer = new Customer(UUID.randomUUID(), newCustomer.getFullName(),
                newCustomer.getCpf(), newCustomer.getBirthDate(), newCustomer.getAddress());
    }

    @Test
    void registerNewCustomerSuccessfully() {
        when(customerGateway.findByCpf(newCustomer.getCpf())).thenReturn(Optional.empty());
        when(customerGateway.save(newCustomer)).thenReturn(existingCustomer);

        Customer savedCustomer = createCustomerUseCase.execute(newCustomer);

        assertNotNull(savedCustomer);
        assertNotNull(savedCustomer.getId());
        assertEquals(newCustomer.getFullName(), savedCustomer.getFullName());
        assertEquals(newCustomer.getCpf(), savedCustomer.getCpf());
        assertNotNull(savedCustomer.getAddress());
        assertEquals(newCustomer.getAddress().getStreet(), savedCustomer.getAddress().getStreet());

        verify(customerGateway).findByCpf(newCustomer.getCpf());
        verify(customerGateway).save(newCustomer);
    }

    @Test
    void throwExceptionWhenCpfAlreadyExists() {
        when(customerGateway.findByCpf(newCustomer.getCpf())).thenReturn(Optional.of(existingCustomer));

        CpfAlreadyInRegisteredException exception = assertThrows(CpfAlreadyInRegisteredException.class,
                () -> createCustomerUseCase.execute(newCustomer));
        assertTrue(exception.getMessage().endsWith(existingCustomer.getCpf()));
        verify(customerGateway).findByCpf(newCustomer.getCpf());
        verify(customerGateway, never()).save(any());
    }

}