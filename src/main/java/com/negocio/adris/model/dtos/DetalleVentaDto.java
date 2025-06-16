package com.negocio.adris.model.dtos;

import com.negocio.adris.model.entities.Producto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class DetalleVentaDto {
    @NotNull(message = "DetalleVenta necesita un producto al cual hacer referencia")
    private Producto producto;
    @Positive(message = "La cantidad no puede ser negativa")
    private int cantidad;
    @Positive(message = "El descuento no puede ser negativo")
    private BigDecimal descuento;
    @Positive(message = "El total no puede ser negativo")
    private BigDecimal subtotal;

    public DetalleVentaDto() {
    }

    public DetalleVentaDto(Producto producto, int cantidad, BigDecimal descuento, BigDecimal subtotal) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.descuento = descuento;
        this.subtotal = subtotal;
    }

    public Producto getProducto(){
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }


    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}