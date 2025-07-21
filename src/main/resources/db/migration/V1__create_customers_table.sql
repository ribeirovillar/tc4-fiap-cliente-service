-- V1__create_customers_table.sql
CREATE TABLE customers (
    id UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    birth_date DATE NOT NULL,

    street VARCHAR(255),
    number VARCHAR(20),
    city VARCHAR(100),
    state VARCHAR(50),
    zip_code VARCHAR(20),

    CONSTRAINT uk_customer_cpf UNIQUE (cpf)
);