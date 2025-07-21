package com.fiap.cliente.controller;

import com.fiap.cliente.controller.json.CustomerRequestDTO;
import com.fiap.cliente.controller.json.CustomerResponseDTO;
import com.fiap.cliente.domain.Customer;
import com.fiap.cliente.mapper.CustomerMapper;
import com.fiap.cliente.usecase.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerController {

    CreateCustomerUseCase createCustomerUseCase;
    RetrieveCustomerByIdUseCase retrieveCustomerByIdUseCase;
    RetrieveAllCustomersUseCase retrieveAllCustomersUseCase;
    DeleteCustomerByIdUseCase deleteCustomerByIdUseCase;
    UpdateCustomerUseCase updateCustomerUseCase;
    CustomerMapper customerMapper;

    @Operation(summary = "Create a new customer", description = "Creates a new customer and returns the created customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@RequestBody CustomerRequestDTO dto) {
        Customer customer = customerMapper.toDomain(dto);
        Customer saved = createCustomerUseCase.execute(customer);
        CustomerResponseDTO response = customerMapper.toResponse(saved);
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(response);
    }

    @Operation(summary = "Get customer by ID", description = "Retrieves a customer by their unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getById(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable UUID id) {
        Customer customer = retrieveCustomerByIdUseCase.execute(id);
        CustomerResponseDTO response = customerMapper.toResponse(customer);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all customers", description = "Retrieves a list of all customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of customers retrieved")
    })
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAll() {
        List<Customer> customers = retrieveAllCustomersUseCase.execute();
        List<CustomerResponseDTO> response = customers.stream()
                .map(customerMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a customer", description = "Deletes a customer by their unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable UUID id) {
        deleteCustomerByIdUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a customer", description = "Updates an existing customer's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> update(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable UUID id,
            @RequestBody CustomerRequestDTO dto) {
        Customer customer = customerMapper.toDomain(id, dto);
        updateCustomerUseCase.execute(customer);
        CustomerResponseDTO response = customerMapper.toResponse(customer);
        return ResponseEntity.ok(response);
    }
}