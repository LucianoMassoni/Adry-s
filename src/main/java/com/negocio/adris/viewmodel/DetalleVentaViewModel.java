package com.negocio.adris.viewmodel;

import com.google.inject.Inject;
import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.enums.UnidadMedida;
import com.negocio.adris.model.service.DetalleVentaService;
import com.negocio.adris.model.service.VentaService;
import javafx.beans.property.*;


import java.math.BigDecimal;

public class DetalleVentaViewModel {
    private final DetalleVentaService detalleVentaService;
    private final VentaService ventaService;

    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<Producto> producto = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> cantidad = new SimpleObjectProperty<>(BigDecimal.ONE);
    private final ObjectProperty<BigDecimal> descuento = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> precio = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> subtotal = new SimpleObjectProperty<>();


    @Inject
    public DetalleVentaViewModel(DetalleVentaService detalleVentaService, VentaService ventaService){
        this.detalleVentaService = detalleVentaService;
        this.ventaService = ventaService;

    }

    public void limpiarFormulario(){
        producto.set(null);
        cantidad.set(BigDecimal.ONE);
        descuento.set(BigDecimal.ZERO);
        precio.set(null);
        subtotal.set(BigDecimal.ZERO);
    }

    public DetalleVentaItem crearDtoActual(){
        if (producto.get() == null)
            throw new IllegalArgumentException("se necesita un producto para cargar");

        if (!producto.getValue().esDivisible()){
            if (cantidad.get().intValue() > producto.get().getCantidad())
                throw new IllegalArgumentException("No hay " + cantidad.get() + " de " + producto.get().getNombre() + ". hay: " + producto.get().getCantidad());
        }

        DetalleVentaItem item = new DetalleVentaItem(
                producto.get(),
                cantidad.get(),
                descuento.get(),
                precio.get()
        );

        return item;
    }

    public LongProperty idProperty() { return id; }
    public ObjectProperty<Producto> productoProperty() { return producto; }
    public ObjectProperty<BigDecimal> cantidadProperty() { return cantidad; }
    public ObjectProperty<BigDecimal> descuentoProperty() { return descuento; }
    public ObjectProperty<BigDecimal> precioProperty() { return precio; }
    public ObjectProperty<BigDecimal> subtotalProperty() { return subtotal; }
}
