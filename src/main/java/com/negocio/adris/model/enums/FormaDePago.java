package com.negocio.adris.model.enums;

public enum FormaDePago {
    EFECTIVO,
    TARJETA,
    TRANSFERENCIA;

    @Override
    public String toString(){
        return switch (this) {
            case EFECTIVO -> "Efectivo";
            case TARJETA -> "Tarjeta";
            case TRANSFERENCIA -> "Transferencia";
        };
    }
}
