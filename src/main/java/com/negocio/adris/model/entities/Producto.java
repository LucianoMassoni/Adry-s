package com.negocio.adris.model.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Producto {
    private long id;
    private String nombre;
    private String marca;
    private double peso;
    private int cantidad;
    private BigDecimal costo;
    private BigDecimal ganancia;
    private BigDecimal precio;
    private TipoProducto tipo;
    private LocalDate fechaVencimiento;

    public Producto() {}

    public Producto(long id, String nombre, String marca, double peso, int cantidad, BigDecimal costo, BigDecimal ganancia, BigDecimal precio, TipoProducto tipo, LocalDate fechaVencimiento) {
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

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public BigDecimal getGanancia() {
        return ganancia;
    }

    public void setGanancia(BigDecimal ganancia) {
        this.ganancia = ganancia;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
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

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", marca='" + marca + '\'' +
                ", peso=" + peso +
                ", cantidad=" + cantidad +
                ", costo=" + costo +
                ", ganancia=" + ganancia +
                ", precio=" + precio +
                ", tipo=" + tipo +
                ", fechaVencimiento=" + fechaVencimiento +
                '}';
    }
}

