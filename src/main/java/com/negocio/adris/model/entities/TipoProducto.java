package com.negocio.adris.model.entities;

public enum TipoProducto {
    FIAMBRES,
    EMBUTIDOS,
    QUESOS_Y_LACTEOS,
    BEBIDAS_SIN_ALCOHOL,
    BEBIDAS_ALCOHOLICA,
    PANIFICADOS,
    CONSERVAS_Y_ESCABECHES,
    SNACKS_Y_SUELTOS,
    CONGELADOS,
    VINOS,
    OTROS;

    @Override
    public String toString(){
        return switch (this){
            case FIAMBRES -> "Fiambres";
            case EMBUTIDOS -> "embutidos";
            case QUESOS_Y_LACTEOS -> "Quesos y lácteos";
            case BEBIDAS_SIN_ALCOHOL -> "Bebidas sin alcohol";
            case BEBIDAS_ALCOHOLICA -> "Bebidas alcohólicas";
            case PANIFICADOS -> "Panificados";
            case CONSERVAS_Y_ESCABECHES -> "Conservas y escabeches";
            case SNACKS_Y_SUELTOS -> "Snacks y sueltos";
            case CONGELADOS -> "Congelados";
            case VINOS -> "Vinos";
            case OTROS -> "Otros";
        };
    }
}
