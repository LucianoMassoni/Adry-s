package com.negocio.adris.viewmodel;

import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.enums.UnidadMedida;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DetalleVentaItem {
    private final ObjectProperty<Producto> producto = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> cantidad = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> descuento = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> subtotal = new SimpleObjectProperty<>();

    public DetalleVentaItem(Producto producto, BigDecimal cantidad, BigDecimal descuento){
        this.producto.set(producto);
        this.cantidad.set(cantidad);
        this.descuento.set(descuento);
        recalcSubtotal();

        this.cantidad.addListener((obs, oldv, newv) -> recalcSubtotal());
        this.descuento.addListener((obs, oldv, newv) -> recalcSubtotal());}

    private void recalcSubtotal(){
        BigDecimal precio = producto.get().getPrecio();
        BigDecimal mult = BigDecimal.ONE.subtract(descuento.get().divide(BigDecimal.valueOf(100)));
        subtotal.set(precio.multiply(cantidad.get()).multiply(mult)
                .setScale(2, RoundingMode.HALF_UP));
    }

    public ObjectProperty<Producto> productoProperty(){ return producto; }
    public ObjectProperty<BigDecimal> cantidadProperty() { return cantidad; }
    public ObjectProperty<BigDecimal> descuentoProperty() { return descuento; }
    public ObjectProperty<BigDecimal> subtotalProperty() { return subtotal; }
}
