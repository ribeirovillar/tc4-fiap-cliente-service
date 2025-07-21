package com.fiap.cliente.gateway.database.jpa.repository;

import com.fiap.cliente.gateway.database.jpa.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    Optional<CustomerEntity> findByCpf(String cpf);
}
