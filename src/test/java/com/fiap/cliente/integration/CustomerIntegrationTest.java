package com.fiap.cliente.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.cliente.controller.json.AddressDTO;
import com.fiap.cliente.controller.json.CustomerRequestDTO;
import com.fiap.cliente.controller.json.CustomerResponseDTO;
import com.fiap.cliente.gateway.database.jpa.repository.CustomerRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@FieldDefaults(level = AccessLevel.PRIVATE)
class CustomerIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    CustomerRepository customerRepository;

    String baseUrl;
    HttpHeaders headers;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/customers";
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
    }

    @Test
    void completeCustomerLifecycle() {
        AddressDTO addressDTO = new AddressDTO("Rua A", "123", "São Paulo", "SP", "01234567");
        CustomerRequestDTO createRequest = new CustomerRequestDTO(
            "Maria Silva",
            "12345678901",
            LocalDate.of(1990, 5, 15),
            addressDTO
        );

        HttpEntity<CustomerRequestDTO> createEntity = new HttpEntity<>(createRequest, headers);
        ResponseEntity<CustomerResponseDTO> createResponse = restTemplate.postForEntity(
            baseUrl, createEntity, CustomerResponseDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertEquals("Maria Silva", createResponse.getBody().getFullName());
        assertEquals("12345678901", createResponse.getBody().getCpf());

        UUID customerId = createResponse.getBody().getId();
        assertNotNull(customerId);

        ResponseEntity<CustomerResponseDTO> getResponse = restTemplate.getForEntity(
            baseUrl + "/" + customerId, CustomerResponseDTO.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Maria Silva", getResponse.getBody().getFullName());

        ResponseEntity<CustomerResponseDTO[]> getAllResponse = restTemplate.getForEntity(
            baseUrl, CustomerResponseDTO[].class);
        assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());
        assertEquals(1, getAllResponse.getBody().length);

        AddressDTO updatedAddressDTO = new AddressDTO("Rua B", "456", "Rio de Janeiro", "RJ", "87654321");
        CustomerRequestDTO updateRequest = new CustomerRequestDTO(
            "Maria Santos Silva",
            "12345678901",
            LocalDate.of(1990, 5, 15),
            updatedAddressDTO
        );

        HttpEntity<CustomerRequestDTO> updateEntity = new HttpEntity<>(updateRequest, headers);
        ResponseEntity<CustomerResponseDTO> updateResponse = restTemplate.exchange(
            baseUrl + "/" + customerId, HttpMethod.PUT, updateEntity, CustomerResponseDTO.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals("Maria Santos Silva", updateResponse.getBody().getFullName());
        assertEquals("Rua B", updateResponse.getBody().getAddress().getStreet());

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
            baseUrl + "/" + customerId, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        ResponseEntity<String> getAfterDeleteResponse = restTemplate.getForEntity(
            baseUrl + "/" + customerId, String.class);
        assertEquals(HttpStatus.NOT_FOUND, getAfterDeleteResponse.getStatusCode());

        assertEquals(0, customerRepository.count());
    }

    @Test
    void createCustomerWithInvalidCpf() {
        AddressDTO addressDTO = new AddressDTO("Rua A", "123", "São Paulo", "SP", "01234567");
        CustomerRequestDTO request = new CustomerRequestDTO(
            "Maria Silva",
            "123456789",
            LocalDate.of(1990, 5, 15),
            addressDTO
        );

        HttpEntity<CustomerRequestDTO> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl, entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createCustomerWithInvalidZipCode() {
        AddressDTO addressDTO = new AddressDTO("Rua A", "123", "São Paulo", "SP", "123");
        CustomerRequestDTO request = new CustomerRequestDTO(
            "Maria Silva",
            "12345678901",
            LocalDate.of(1990, 5, 15),
            addressDTO
        );

        HttpEntity<CustomerRequestDTO> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl, entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createDuplicateCustomer() {
        AddressDTO addressDTO = new AddressDTO("Rua A", "123", "São Paulo", "SP", "01234567");
        CustomerRequestDTO request = new CustomerRequestDTO(
            "Maria Silva",
            "12345678901",
            LocalDate.of(1990, 5, 15),
            addressDTO
        );

        HttpEntity<CustomerRequestDTO> entity = new HttpEntity<>(request, headers);

        ResponseEntity<CustomerResponseDTO> firstResponse = restTemplate.postForEntity(
            baseUrl, entity, CustomerResponseDTO.class);
        assertEquals(HttpStatus.CREATED, firstResponse.getStatusCode());

        ResponseEntity<String> secondResponse = restTemplate.postForEntity(
            baseUrl, entity, String.class);
        assertEquals(HttpStatus.CONFLICT, secondResponse.getStatusCode());
    }

    @Test
    void retrieveNonExistentCustomer() {
        UUID nonExistentId = UUID.randomUUID();

        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/" + nonExistentId, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateNonExistentCustomer() {
        UUID nonExistentId = UUID.randomUUID();
        AddressDTO addressDTO = new AddressDTO("Rua A", "123", "São Paulo", "SP", "01234567");
        CustomerRequestDTO request = new CustomerRequestDTO(
            "Maria Silva",
            "12345678901",
            LocalDate.of(1990, 5, 15),
            addressDTO
        );

        HttpEntity<CustomerRequestDTO> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/" + nonExistentId, HttpMethod.PUT, entity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteNonExistentCustomer() {
        UUID nonExistentId = UUID.randomUUID();

        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/" + nonExistentId, HttpMethod.DELETE, null, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createCustomerWithValidationErrors() {
        AddressDTO addressDTO = new AddressDTO("", "", "", "", "");
        CustomerRequestDTO request = new CustomerRequestDTO(
            "",
            "",
            null,
            addressDTO
        );

        HttpEntity<CustomerRequestDTO> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl, entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
