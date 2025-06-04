package com.negocio.adris.model.enums;

public enum Litraje {
    LITROS("L"),
    MILILITROS("ml");

    private final String simbolo;

    Litraje(String simbolo){
        this.simbolo = simbolo;
    }

    public String getSimbolo(){
        return simbolo;
    }
}
