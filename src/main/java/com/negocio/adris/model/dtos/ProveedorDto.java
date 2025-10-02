package com.negocio.adris.model.dtos;

import jakarta.validation.constraints.NotBlank;

public class ProveedorDto {
    @NotBlank (message= "El proveedor necesita un nombre")
    private String nombre;
    private String telefono;

    public ProveedorDto() {
    }

    public ProveedorDto(String nombre, String telefono) {
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
