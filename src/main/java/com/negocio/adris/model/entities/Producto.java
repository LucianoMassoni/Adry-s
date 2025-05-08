package com.negocio.adris.model.entities;

import java.time.LocalDate;
import java.util.Date;

public class Producto {
    private long id;
    private String nombre;
    private String marca;
    private double peso;
    private int cantidad;
    private double costo;
    private double ganancia;
    private double precio;
    private TipoProducto tipo;
    private LocalDate fechaVencimiento;

    public Producto() {}

    public Producto(long id, String nombre, String marca, double peso, int cantidad, double costo, double ganancia, double precio, TipoProducto tipo, LocalDate fechaVencimiento) {
        this.id = id;
        this.nombre = nombre;
        this.marca = marca;
        this.peso = peso;
        this.cantidad = cantidad;
        this.costo = costo;
        this.ganancia = ganancia;
        this.precio = precio;
        this.tipo = tipo;
        this.fechaVencimiento = fechaVencimiento;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

