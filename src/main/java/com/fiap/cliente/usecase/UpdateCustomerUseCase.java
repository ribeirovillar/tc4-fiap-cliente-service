package com.fiap.cliente.usecase;

import com.fiap.cliente.domain.Customer;
import com.fiap.cliente.exception.CustomerNotFoundException;
import com.fiap.cliente.gateway.CustomerGateway;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UpdateCustomerUseCase {

    CustomerGateway customerGateway;
    @Transactional
    public void execute(Customer updatedCustomer) {
        customerGateway.findById(updatedCustomer.getId())
                .ifPresentOrElse(
                        existingCustomer -> customerGateway.save(updatedCustomer),
                        () -> {
                            throw new CustomerNotFoundException(updatedCustomer.getId());
                        }
                );
    }

}
