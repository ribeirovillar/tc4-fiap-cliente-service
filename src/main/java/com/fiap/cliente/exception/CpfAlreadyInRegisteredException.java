package com.fiap.cliente.exception;

public class CpfAlreadyInRegisteredException extends RuntimeException {
    public CpfAlreadyInRegisteredException(String cpf) {
        super("CPF already registered: " + cpf);
    }
}
