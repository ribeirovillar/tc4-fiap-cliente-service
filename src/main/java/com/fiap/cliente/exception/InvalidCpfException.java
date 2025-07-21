package com.fiap.cliente.exception;

public class InvalidCpfException extends RuntimeException {
    public InvalidCpfException(String cpf) {
        super("Invalid CPF: " + cpf);
    }
}
