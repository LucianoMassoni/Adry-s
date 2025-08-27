package com.negocio.adris.model.dtos;

import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.enums.UnidadMedida;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.Optional;

public class DetalleVentaDto {
    @NotNull(message = "DetalleVenta necesita un producto al cual hacer referencia")
    private Producto producto;
    private BigDecimal cantidad;
    @PositiveOrZero(message = "El descuento no puede ser negativo")
    private BigDecimal descuento;
    private Optional<Double> peso;
    private Optional<BigDecimal> precioPorPeso;
    private UnidadMedida unidadMedida;


    public DetalleVentaDto() {
    }

    public DetalleVentaDto(Producto producto, BigDecimal cantidad, UnidadMedida unidadMedida, BigDecimal descuento) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.unidadMedida = unidadMedida;
        this.descuento = descuento;
    }

    public Producto getProducto(){
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public Optional<Double> getPeso() {
        return peso;
    }

    public void setPeso(Optional<Double> peso) {
        this.peso = peso;
    }

    public Optional<BigDecimal> getPrecioPorPeso() {
        return precioPorPeso;
    }

    public void setPrecioPorPeso(Optional<BigDecimal> precioPorPeso) {
        this.precioPorPeso = precioPorPeso;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
}