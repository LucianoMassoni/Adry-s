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
    private final ObjectProperty<UnidadMedida> unidadMedida = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> descuento = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> subtotal = new SimpleObjectProperty<>();


    @Inject
    public DetalleVentaViewModel(DetalleVentaService detalleVentaService, VentaService ventaService){
        this.detalleVentaService = detalleVentaService;
        this.ventaService = ventaService;

    }

    public void limpiarFormulario(){
        producto.set(null);
        cantidad.set(BigDecimal.ONE);
        unidadMedida.set(null);
        descuento.set(BigDecimal.ZERO);
        subtotal.set(BigDecimal.ZERO);
    }

    public DetalleVentaItem crearDtoActual(){
        if (producto.get() == null)
            throw new IllegalArgumentException("se necesita un producto para cargar");

        if (!producto.getValue().esDivisible()){
            if (cantidad.get().intValue() > producto.get().getCantidad())
                throw new IllegalArgumentException("No hay " + cantidad.get() + " de " + producto.get().getNombre() + ". hay: " + producto.get().getCantidad());
        }

        return new DetalleVentaItem(
                producto.get(),
                cantidad.get(),
                unidadMedida.get(),
                descuento.get()
        );
    }

    public LongProperty idProperty() { return id; }
    public ObjectProperty<Producto> productoProperty() { return producto; }
    public ObjectProperty<BigDecimal> cantidadProperty() { return cantidad; }
    public ObjectProperty<UnidadMedida> unidadMedidaProperty() { return unidadMedida; }
    public ObjectProperty<BigDecimal> descuentoProperty() { return descuento; }
    public ObjectProperty<BigDecimal> subtotalProperty() { return subtotal; }
}
