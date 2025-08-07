package com.negocio.adris.model.dtos;

import com.negocio.adris.model.enums.TipoProducto;
import com.negocio.adris.model.enums.UnidadMedida;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductoDto {
    @NotBlank(message = "El nombre es necesario")
    private String nombre;
    @NotBlank(message = "La marca es necesaria")
    private String marca;
    @PositiveOrZero(message = "El peso del producto no puede ser negativo")
    private double peso;
    @PositiveOrZero(message = "El peso del producto no puede ser negativo")
    private double pesoActual;
    private UnidadMedida unidadMedida;
    @PositiveOrZero(message = "La cantidad de productos no puede ser negativa")
    private int cantidad;
    @PositiveOrZero(message = "El costo del producto no puede ser negativo")
    private BigDecimal costo;
    @PositiveOrZero(message = "La ganancia del producto no puede ser negativa")
    private BigDecimal ganancia;
    @PositiveOrZero(message = "El precio de un producto no puede ser negativo")
    private BigDecimal precio;
    private TipoProducto tipo;
    private boolean esDivisible;

    public ProductoDto(){};
    
    public ProductoDto(String nombre, String marca, double peso, double pesoActual, UnidadMedida unidadMedida, int cantidad, BigDecimal costo, BigDecimal ganancia, BigDecimal precio, TipoProducto tipo, boolean esDivisible) {
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

    public double getPesoActual() {
        return pesoActual;
    }

    public void setPesoActual(double pesoActual) {
        this.pesoActual = pesoActual;
    }


    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
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
        return "ProductoDto{" +
                "nombre='" + nombre + '\'' +
                ", marca='" + marca + '\'' +
                ", peso=" + peso +
                ", pesoActual=" + pesoActual +
                ", unidadMedida=" + unidadMedida +
                ", cantidad=" + cantidad +
                ", costo=" + costo +
                ", ganancia=" + ganancia +
                ", precio=" + precio +
                ", tipo=" + tipo +
                ", esDivisible=" + esDivisible +
                '}';
    }
}
