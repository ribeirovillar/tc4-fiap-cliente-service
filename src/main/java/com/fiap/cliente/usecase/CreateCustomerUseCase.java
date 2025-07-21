package com.fiap.cliente.usecase;

import com.fiap.cliente.domain.Customer;
import com.fiap.cliente.exception.CpfAlreadyInRegisteredException;
import com.fiap.cliente.gateway.CustomerGateway;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCustomerUseCase {

    private final CustomerGateway customerGateway;
    @Transactional
    public Customer execute(Customer customer) {
        customerGateway.findByCpf(customer.getCpf())
                .ifPresent(customerFound -> {
                    throw new CpfAlreadyInRegisteredException(customerFound.getCpf());
                });
        return customerGateway.save(customer);
    }
}
