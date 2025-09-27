package com.negocio.adris.viewmodel;

import com.google.inject.Inject;
import com.negocio.adris.model.dtos.DetalleVentaDto;
import com.negocio.adris.model.dtos.VentaDto;
import com.negocio.adris.model.enums.FormaDePago;
import com.negocio.adris.model.service.VentaService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

public class VentaViewModel {
    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<FormaDePago> formaDePago = new SimpleObjectProperty<>();
    private final ObservableList<DetalleVentaItem> detalleVentas = FXCollections.observableArrayList();
    private final ReadOnlyObjectWrapper<BigDecimal> total = new ReadOnlyObjectWrapper<>();

    private final VentaService ventaService;
    @Inject
    public VentaViewModel(VentaService ventaService){
        this.ventaService = ventaService;

        detalleVentas.addListener((ListChangeListener<DetalleVentaItem>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (DetalleVentaItem item : c.getAddedSubList()) {
                        item.subtotalProperty().addListener((obs, oldVal, newVal) -> recalcularTotal());
                    }
                }
            }
            recalcularTotal();
        });
    }

    public void agregarItem(DetalleVentaItem item) {
        detalleVentas.add(item);
    }

    public void recalcularTotal(){
        BigDecimal suma = detalleVentas.stream().
                map(DetalleVentaItem::subtotalProperty)
                .map(ReadOnlyObjectProperty::get)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (formaDePago.get().equals(FormaDePago.EFECTIVO) && suma.compareTo(BigDecimal.valueOf(20000)) > 0){
            suma = suma.multiply(BigDecimal.valueOf(0.9));
        }

        total.set(suma.setScale(2, RoundingMode.HALF_UP));
    }

    public void guardarVenta(){
        VentaDto dto = new VentaDto();
        List<DetalleVentaDto> detalleVentaDtos = detalleVentas.stream()
                .map(item -> new DetalleVentaDto(
                        item.productoProperty().get(),
                        item.cantidadProperty().get(),
                        item.descuentoProperty().get(),
                        item.precioProperty().get()
                )).toList();

        dto.setDetalleVentaDtos(detalleVentaDtos);
        dto.setFecha(LocalDateTime.now());
        dto.setFormaDePago(formaDePagoProperty().get());
        System.out.println("dto = " + dto.getFormaDePago() + dto.getDetalleVentaDtos().toString() + dto.getFecha());
        ventaService.crearVenta(dto);
        id.set(0);
        detalleVentas.clear();
    }

    public void borrarItem(DetalleVentaItem item) {
        detalleVentas.remove(item);
    }

    public void agregarCantidad(DetalleVentaItem item){
        if (item.cantidadProperty().get().compareTo(BigDecimal.valueOf(item.productoProperty().get().getCantidad())) < 0){
            item.cantidadProperty().set(item.cantidadProperty().get().add(BigDecimal.ONE));
        }
    }

    public void sacarCantidad(DetalleVentaItem item){
        if (item.cantidadProperty().get().compareTo(BigDecimal.ONE) > 0){
            item.cantidadProperty().set(item.cantidadProperty().get().subtract(BigDecimal.ONE));
        }
    }

    public void cancelar(){
        id.set(0);
        detalleVentas.clear();
    }

    public LongProperty idProperty() { return id; }
    public ObjectProperty<FormaDePago> formaDePagoProperty() { return formaDePago; }
    public ObservableList<DetalleVentaItem> getDetalleVentas() { return detalleVentas; }
    public ReadOnlyObjectProperty<BigDecimal> totalProperty() { return total.getReadOnlyProperty(); }
}
