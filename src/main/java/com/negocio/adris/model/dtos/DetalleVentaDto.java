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
    private BigDecimal precio;


    public DetalleVentaDto() {
    }

    public DetalleVentaDto(Producto producto, BigDecimal cantidad, BigDecimal descuento, BigDecimal precio) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.descuento = descuento;
        this.precio = precio;
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

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}