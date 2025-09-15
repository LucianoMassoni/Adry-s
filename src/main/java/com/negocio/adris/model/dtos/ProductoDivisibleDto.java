package com.negocio.adris.model.dtos;

public class ProductoDivisibleDto {
    private String nombre;
    private String marca;
    private boolean esDivisible;

    public ProductoDivisibleDto(){}

    public ProductoDivisibleDto(String nombre, String marca, boolean esDivisible){
        this.nombre = nombre;
        this.marca = marca;
        this.esDivisible = esDivisible;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public String getNombre(){
        return nombre;
    }

    public void setMarca(String marca){
        this.marca = marca;
    }

    public String getMarca(){
        return marca;
    }

    public boolean esDivisible() {
        return esDivisible;
    }

    public void setEsDivisible(boolean esDivisible) {
        this.esDivisible = esDivisible;
    }
}
