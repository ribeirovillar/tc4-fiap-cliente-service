package com.fiap.cliente.usecase;

import com.fiap.cliente.domain.Customer;
import com.fiap.cliente.exception.CustomerNotFoundException;
import com.fiap.cliente.gateway.CustomerGateway;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RetrieveCustomerByIdUseCase {

    CustomerGateway customerGateway;

    public Customer execute(UUID customerId) {
        return customerGateway.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

}
