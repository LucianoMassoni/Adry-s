package com.negocio.adris.viewmodel;

import com.google.inject.Inject;
import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.service.DetalleVentaService;
import com.negocio.adris.model.service.VentaService;
import javafx.beans.property.*;


import java.math.BigDecimal;

public class DetalleVentaViewModel {
    private final DetalleVentaService detalleVentaService;
    private final VentaService ventaService;

    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<Producto> producto = new SimpleObjectProperty<>();
    private final IntegerProperty cantidad = new SimpleIntegerProperty(1);
    private final ObjectProperty<BigDecimal> descuento = new SimpleObjectProperty<>();


    @Inject
    public DetalleVentaViewModel(DetalleVentaService detalleVentaService, VentaService ventaService){
        this.detalleVentaService = detalleVentaService;
        this.ventaService = ventaService;

    }

    public void limpiarFormulario(){
        producto.set(null);
        cantidad.set(1);
        descuento.set(BigDecimal.ZERO);
    }



    public DetalleVentaItem crearDtoActual(){
        if (producto.get() == null)
            throw new IllegalArgumentException("se necesita un producto para cargar");
        return new DetalleVentaItem(
                producto.get(),
                cantidad.get(),
                descuento.get()
        );
    }

    public LongProperty idProperty() { return id; }
    public ObjectProperty<Producto> productoProperty() { return producto; }
    public IntegerProperty cantidadProperty() { return cantidad; }
    public ObjectProperty<BigDecimal> descuentoProperty() { return descuento; }
}
