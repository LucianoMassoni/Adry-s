package com.negocio.adris.model.exceptions;

public class CuentaNotFoundException extends Exception {
    public CuentaNotFoundException(String message) {
        super(message);
    }
}