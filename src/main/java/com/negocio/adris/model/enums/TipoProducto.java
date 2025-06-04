package com.negocio.adris.model.enums;

import java.util.List;

public enum TipoProducto {
    FIAMBRES(List.of(UnidadMedida.KILOS, UnidadMedida.GRAMOS)),
    EMBUTIDOS(List.of(UnidadMedida.UNIDAD)),
    QUESOS_Y_LACTEOS(List.of(UnidadMedida.KILOS, UnidadMedida.GRAMOS)),
    BEBIDAS_SIN_ALCOHOL(List.of(UnidadMedida.LITROS, UnidadMedida.MILILITROS)),
    BEBIDAS_ALCOHOLICA(List.of(UnidadMedida.LITROS, UnidadMedida.MILILITROS)),
    PANIFICADOS(List.of(UnidadMedida.UNIDAD)),
    CONSERVAS_Y_ESCABECHES(List.of(UnidadMedida.UNIDAD)),
    SNACKS_Y_SUELTOS(List.of(UnidadMedida.UNIDAD)),
    CONGELADOS(List.of(UnidadMedida.UNIDAD)),
    VINOS(List.of(UnidadMedida.LITROS, UnidadMedida.MILILITROS)),
    OTROS(List.of(UnidadMedida.UNIDAD));

    private final List<UnidadMedida> unidadesValidas;

    TipoProducto(List<UnidadMedida> unidadesValidas){
        this.unidadesValidas = unidadesValidas;
    }

    public List<UnidadMedida> getUnidadesValidas(){
        return unidadesValidas;
    }

    public boolean admiteUnidad(UnidadMedida unidad) {
        return unidadesValidas.contains(unidad);
    }

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
