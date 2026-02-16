package com.negocio.adris.viewmodel;

import com.negocio.adris.model.entities.Producto;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DetalleVentaItem {
    private final ObjectProperty<Producto> producto = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> cantidad = new SimpleObjectProperty<>(BigDecimal.ONE);
    private final ObjectProperty<BigDecimal> descuento = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final ObjectProperty<BigDecimal> precio = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final ObjectProperty<BigDecimal> subtotal = new SimpleObjectProperty<>(BigDecimal.ZERO);

    public DetalleVentaItem() {
        producto.addListener((obs, o, n) -> recalcSubtotal());
        cantidad.addListener((obs, o, n) -> recalcSubtotal());
        descuento.addListener((obs, o, n) -> recalcSubtotal());
        precio.addListener((obs, o, n) -> recalcSubtotal());
    }

    private void recalcSubtotal() {
        if (producto.get() == null) {
            subtotal.set(BigDecimal.ZERO);
            return;
        }

        if (descuento.get() == null) {
            descuento.set(BigDecimal.ZERO);
        }

        if (producto.get().esDivisible()) {
            if (precio.get() == null) {
                subtotal.set(BigDecimal.ZERO);
                return;
            }

            BigDecimal mult = BigDecimal.ONE.subtract(
                    descuento.get().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
            );

            subtotal.set(
                    precio.get().multiply(mult).setScale(2, RoundingMode.HALF_UP)
            );

        } else {
            if (cantidad.get() == null) {
                subtotal.set(BigDecimal.ZERO);
                return;
            }

            BigDecimal mult = BigDecimal.ONE.subtract(
                    descuento.get().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
            );

            subtotal.set(
                    producto.get().getPrecio()
                            .multiply(cantidad.get())
                            .multiply(mult)
                            .setScale(2, RoundingMode.HALF_UP)
            );
        }
    }

    public DetalleVentaItem getItemActual(){
        if (producto.get() == null)
            throw new IllegalArgumentException("se necesita un producto para cargar");

        if (!producto.getValue().esDivisible()){
            if (cantidad.get().intValue() > producto.get().getCantidad())
                throw new IllegalArgumentException("No hay " + cantidad.get() + " de " + producto.get().getNombre() + ". hay: " + producto.get().getCantidad());
        }

        DetalleVentaItem item = new DetalleVentaItem();
        item.producto.set(producto.get());
        item.cantidad.set(cantidad.get());
        item.descuento.set(descuento.get());
        item.precio.set(precio.get());

        return item;
    }

    public void limpiarFormulario(){
        producto.set(null);
        cantidad.set(BigDecimal.ONE);
        descuento.set(BigDecimal.ZERO);
        precio.set(BigDecimal.ZERO);
        subtotal.set(BigDecimal.ZERO);
    }

    public ObjectProperty<Producto> productoProperty(){ return producto; }
    public ObjectProperty<BigDecimal> cantidadProperty() { return cantidad; }
    public ObjectProperty<BigDecimal> descuentoProperty() { return descuento; }
    public ObjectProperty<BigDecimal> precioProperty() { return precio; }
    public ObjectProperty<BigDecimal> subtotalProperty() { return subtotal; }
}
