package com.fiap.cliente.usecase;

import com.fiap.cliente.domain.Customer;
import com.fiap.cliente.gateway.CustomerGateway;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RetrieveAllCustomersUseCase {

    CustomerGateway customerGateway;

    public List<Customer> execute() {
        return customerGateway.findAll();
    }

}
