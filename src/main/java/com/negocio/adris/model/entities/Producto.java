package com.negocio.adris.model.entities;

import com.negocio.adris.model.enums.TipoProducto;
import com.negocio.adris.model.enums.UnidadMedida;

import java.math.BigDecimal;

public class Producto {
    private long id;
    private String nombre;
    private String marca;
    private double peso;
    private double pesoActual;
    private int cantidad;
    private UnidadMedida unidadMedida;
    private BigDecimal costo;
    private BigDecimal ganancia;
    private BigDecimal precio;
    private TipoProducto tipo;
    private boolean esDivisible;

    public Producto() {}

    public Producto(long id, String nombre, String marca, boolean esDivisible) {
        this.id = id;
        this.nombre = nombre;
        this.marca = marca;
        this.esDivisible = esDivisible;
    }

    public Producto(long id, String nombre, String marca, double peso, double pesoActual, UnidadMedida unidadMedida, int cantidad, BigDecimal costo, BigDecimal ganancia, BigDecimal precio, TipoProducto tipo, boolean esDivisible) {
        this.id = id;
        this.nombre = nombre;
        this.marca = marca;
        this.peso = peso;
        this.pesoActual = pesoActual;
        this.unidadMedida = unidadMedida;
        this.cantidad = cantidad;
        this.costo = costo;
        this.ganancia = ganancia;
        this.precio = precio;
        this.tipo = tipo;
        this.esDivisible = esDivisible;
    }

    public Producto(long id, String nombre, String marca, TipoProducto tipo, double peso, UnidadMedida unidadMedida) {
        this.id = id;
        this.nombre = nombre;
        ;this.marca = marca;
        this.tipo = tipo;
        this.peso = peso;
        this.unidadMedida = unidadMedida;
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

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getPesoActual() {
        return pesoActual;
    }

    public void setPesoActual(double pesoActual) {
        this.pesoActual = pesoActual;
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

    public boolean esDivisible() {
        return esDivisible;
    }

    public void setEsDivisible(boolean esDivisible) {
        this.esDivisible = esDivisible;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", marca='" + marca + '\'' +
                ", peso=" + peso +
                ", pesoActual=" + pesoActual +
                ", medida=" + (unidadMedida == null ? "" : unidadMedida.getSimbolo()) +
                ", cantidad=" + cantidad +
                ", costo=" + costo +
                ", ganancia=" + ganancia +
                ", precio=" + precio +
                ", tipo=" + tipo +
                ", esDivisible=" + esDivisible +
                '}';
    }
}

