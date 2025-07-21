package com.fiap.cliente.controller;

import com.fiap.cliente.controller.json.AddressDTO;
import com.fiap.cliente.controller.json.CustomerRequestDTO;
import com.fiap.cliente.controller.json.CustomerResponseDTO;
import com.fiap.cliente.domain.Customer;
import com.fiap.cliente.exception.InvalidCpfException;
import com.fiap.cliente.exception.InvalidZipCodeException;
import com.fiap.cliente.mapper.AddressMapper;
import com.fiap.cliente.mapper.AddressMapperImpl;
import com.fiap.cliente.mapper.CustomerMapper;
import com.fiap.cliente.mapper.CustomerMapperImpl;
import com.fiap.cliente.usecase.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class CustomerControllerTest {

    @Mock
    CreateCustomerUseCase createCustomerUseCase;

    @Mock
    RetrieveCustomerByIdUseCase retrieveCustomerByIdUseCase;

    @Mock
    RetrieveAllCustomersUseCase retrieveAllCustomersUseCase;

    @Mock
    DeleteCustomerByIdUseCase deleteCustomerByIdUseCase;

    @Mock
    UpdateCustomerUseCase updateCustomerUseCase;

    CustomerMapper customerMapper;
    CustomerController customerController;

    final UUID CUSTOMER_ID = UUID.randomUUID();
    Customer customerWithId;
    CustomerRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        AddressMapper addressMapper = new AddressMapperImpl();
        customerMapper = new CustomerMapperImpl();

        setFieldValue(customerMapper, "addressMapper", addressMapper);

        customerController = new CustomerController(
                createCustomerUseCase,
                retrieveCustomerByIdUseCase,
                retrieveAllCustomersUseCase,
                deleteCustomerByIdUseCase,
                updateCustomerUseCase,
                customerMapper);

        AddressDTO addressDTO = new AddressDTO("Rua Artur", "3", "Recife", "Pernambuco", "25611-123");
        requestDTO = new CustomerRequestDTO("Joao Paulo Rodrigues", "12345678901", LocalDate.of(1990, 1, 1), addressDTO);

        customerWithId = customerMapper.toDomain(CUSTOMER_ID, requestDTO);
    }

    private void setFieldValue(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createCustomerSuccessfully() {
        Customer customerSaved = customerMapper.toDomain(requestDTO);
        setFieldValue(customerSaved, "id", CUSTOMER_ID);
        when(createCustomerUseCase.execute(any(Customer.class))).thenReturn(customerSaved);

        ResponseEntity<CustomerResponseDTO> response = customerController.create(requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        CustomerResponseDTO responseDTO = response.getBody();
        assertEquals(CUSTOMER_ID, responseDTO.getId());
        assertEquals(requestDTO.getFullName(), responseDTO.getFullName());
        assertEquals(requestDTO.getCpf(), responseDTO.getCpf());
        assertEquals(requestDTO.getBirthDate(), responseDTO.getBirthDate());

        assertEquals(requestDTO.getAddress().getStreet(), responseDTO.getAddress().getStreet());
        assertEquals(requestDTO.getAddress().getNumber(), responseDTO.getAddress().getNumber());
        assertEquals(requestDTO.getAddress().getZipCode(), responseDTO.getAddress().getZipCode());
    }

    @Test
    void getCustomerByIdSuccessfully() {
        when(retrieveCustomerByIdUseCase.execute(CUSTOMER_ID)).thenReturn(customerWithId);

        ResponseEntity<CustomerResponseDTO> response = customerController.getById(CUSTOMER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(CUSTOMER_ID, response.getBody().getId());
        assertEquals(requestDTO.getFullName(), response.getBody().getFullName());
    }

    @Test
    void getAllCustomersSuccessfully() {
        List<Customer> customers = List.of(customerWithId);
        when(retrieveAllCustomersUseCase.execute()).thenReturn(customers);

        ResponseEntity<List<CustomerResponseDTO>> response = customerController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        CustomerResponseDTO responseDTO = response.getBody().getFirst();
        assertEquals(CUSTOMER_ID, responseDTO.getId());
        assertEquals(requestDTO.getFullName(), responseDTO.getFullName());
    }

    @Test
    void getAllCustomersReturnsEmptyList() {
        when(retrieveAllCustomersUseCase.execute()).thenReturn(Collections.emptyList());

        ResponseEntity<List<CustomerResponseDTO>> response = customerController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void deleteCustomerByIdSuccessfully() {
        doNothing().when(deleteCustomerByIdUseCase).execute(CUSTOMER_ID);

        ResponseEntity<Void> response = customerController.deleteById(CUSTOMER_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(deleteCustomerByIdUseCase).execute(CUSTOMER_ID);
    }

    @Test
    void updateCustomerSuccessfully() {
        setFieldValue(requestDTO, "fullName", "Joao Villar");
        Customer customerUpdated = customerMapper.toDomain(requestDTO);
        setFieldValue(customerUpdated, "id", CUSTOMER_ID);
        doNothing().when(updateCustomerUseCase).execute(any(Customer.class));

        ResponseEntity<CustomerResponseDTO> response = customerController.update(CUSTOMER_ID, requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        CustomerResponseDTO responseDTO = response.getBody();
        assertEquals(CUSTOMER_ID, responseDTO.getId());
        assertEquals(requestDTO.getFullName(), responseDTO.getFullName());
        assertEquals(requestDTO.getCpf(), responseDTO.getCpf());

        verify(updateCustomerUseCase).execute(any(Customer.class));
    }

    @Test
    void failedToCreateCustomerDueToInvalidCpf() {
        setFieldValue(requestDTO, "cpf", "2345678901");
        assertThrows(InvalidCpfException.class, () ->
                customerController.create(requestDTO)
        );
    }

    @Test
    void failedToCreateCustomerDueToNullCpf() {
        setFieldValue(requestDTO, "cpf", null);
        assertThrows(InvalidCpfException.class, () ->
                customerController.create(requestDTO)
        );
    }

    @Test
    void failToCreateCustomerDueToInvalidZipCode() {
        AddressDTO addressDTO = new AddressDTO("Rua Artur", "3", "Recife", "Pernambuco", "123");
        setFieldValue(requestDTO, "address", addressDTO);
        assertThrows(InvalidZipCodeException.class, () ->
                customerController.create(requestDTO)
        );
    }

    @Test
    void failToCreateCustomerDueToNullZipCode() {
        AddressDTO addressDTO = new AddressDTO("Rua Artur", "3", "Recife", "Pernambuco", null);
        setFieldValue(requestDTO, "address", addressDTO);
        assertThrows(InvalidZipCodeException.class, () ->
                customerController.create(requestDTO)
        );
    }

}