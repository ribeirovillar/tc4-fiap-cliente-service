package com.fiap.cliente.usecase;

import com.fiap.cliente.exception.CustomerNotFoundException;
import com.fiap.cliente.gateway.CustomerGateway;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteCustomerByIdUseCase {

    CustomerGateway customerGateway;
    @Transactional
    public void execute(UUID customerId) {
        customerGateway.findById(customerId)
                .ifPresentOrElse(
                        customerGateway::delete,
                        () -> {
                            throw new CustomerNotFoundException(customerId);
                        }
                );
    }

}
