package com.negocio.adris.model.dtos;

import com.negocio.adris.model.entities.Producto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class DetalleVentaDto {
    @NotNull(message = "DetalleVenta necesita un producto al cual hacer referencia")
    private Producto producto;
    @Positive(message = "La cantidad no puede ser negativa")
    private int cantidad;
    @PositiveOrZero(message = "El descuento no puede ser negativo")
    private BigDecimal descuento;


    public DetalleVentaDto() {
    }

    public DetalleVentaDto(Producto producto, int cantidad, BigDecimal descuento) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.descuento = descuento;
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

    public BigDecimal getSubtotal(){
        BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
        return subtotal.subtract(subtotal.multiply(descuento.divide(BigDecimal.valueOf(100))));
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }
}