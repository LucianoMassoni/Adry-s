package com.negocio.adris.model.enums;

public enum UnidadMedida {
    KILOS(Kilaje.KILOS),
    GRAMOS(Kilaje.GRAMOS),
    LITROS(Litraje.LITROS),
    MILILITROS(Litraje.MILILITROS),
    UNIDAD("Unidad");

    private final Object unidad;

    UnidadMedida(Object unidad){
        this.unidad = unidad;
    }

    public String getSimbolo(){
        if(unidad instanceof Kilaje){
            // Lo tengo que castear como Kilaje, Litraje o String porque es un Object.
            return ((Kilaje) unidad).getSimbolo();
        } else if (unidad instanceof Litraje) {
            return ((Litraje) unidad).getSimbolo();
        } else {
            return (String) unidad;
        }
    }

}
