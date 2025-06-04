package com.negocio.adris.model.enums;

public enum Kilaje {
    KILOS("kg"),
    GRAMOS("g");

    private final String simbolo;

    Kilaje(String simbolo){
        this.simbolo = simbolo;
    }

    public String getSimbolo(){
        return simbolo;
    }
}
