package com.negocio.adris.model.dtos;

import com.negocio.adris.model.entities.TipoProducto;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class ProductoDto {
    @NotBlank(message = "El nombre es necesario")
    private String nombre;
    @NotBlank(message = "La marca es necesaria")
    private String marca;
    @Positive(message = "El peso del producto no puede ser negativo")
    private double peso;
    @Positive(message = "La cantidad de productos no puede ser negativa")
    private int cantidad;
    @Positive(message = "El costo del producto no puede ser negativo")
    private double costo;
    @Positive(message = "La ganancia del producto no puede ser negativa")
    private double ganancia;
    @Positive(message = "El precio de un producto no puede ser negativo")
    private double precio;
    private TipoProducto tipo;
    @Future(message = "La fecha de vencimiento tiene que ser a futuro")
    private LocalDate fechaVencimiento;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public double getGanancia() {
        return ganancia;
    }

    public void setGanancia(double ganancia) {
        this.ganancia = ganancia;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public TipoProducto getTipo() {
        return tipo;
    }

    public void setTipo(TipoProducto tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
}
