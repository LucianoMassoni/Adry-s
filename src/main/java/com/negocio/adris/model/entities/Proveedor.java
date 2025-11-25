package com.negocio.adris.model.entities;

public class Proveedor {
    private Long id;
    private String nombre;
    private String telefono;

    public Proveedor(){

    }

    public Proveedor(Long id, String nombre, String telefono){
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public long getId(){
        return this.id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getNombre(){
        return this.nombre;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public String getTelefono(){
        return this.telefono;
    }

    public void setTelefono(String telefono){
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return nombre + " | " + telefono;
    }
}
