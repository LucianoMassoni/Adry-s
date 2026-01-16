package com.negocio.adris.viewmodel;

import com.google.inject.Inject;
import com.negocio.adris.model.dtos.PagoDto;
import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.entities.Pago;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.PagoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.model.service.PagoService;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoViewModel {
    private final PagoService pagoService;

    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<Gasto> gasto = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> fechaPago = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> montoPagado = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> montoPagadoDiario = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> montoPagadoMensual = new SimpleObjectProperty<>();

    private final ObservableList<Pago> pagos = FXCollections.observableArrayList();

    @Inject
    public PagoViewModel(PagoService pagoService){
        this.pagoService = pagoService;

        actualizarMontoPagadoPorDia();
        actualizarMontoPagadoMensual();
    }

    public void cargarPagos(){
        pagos.setAll(pagoService.getPagos());
    }

    public ObservableList<Pago> getPagos(){
        return pagos;
    }

    public void limpiarFormulario(){
        id.set(0);
        gasto.set(null);
        fechaPago.set(null);
        montoPagado.set(null);
    }

    public void cargarPago(Pago p){
        id.set(p.getId());
        gasto.set(p.getGasto());
        fechaPago.set(p.getFechaPago());
        montoPagado.set(p.getMontoPagado());
    }

    public void crearPago() throws GastoNotFoundException, ProveedorNotFoundException {
        PagoDto dto = new PagoDto(
                gasto.get().getId(),
                montoPagado.get()
        );

        pagoService.guardar(dto);
        actualizarMontoPagadoPorDia();
    }

    public void modificarPago() throws PagoNotFoundException, GastoNotFoundException, ProveedorNotFoundException {
        PagoDto dto = new PagoDto(
                gasto.get().getId(),
                montoPagado.get()
        );

        pagoService.modificar(id.get(), dto);
        actualizarMontoPagadoPorDia();
    }

    public void eliminarPago() throws PagoNotFoundException {
        pagoService.eliminar(id.get());
        actualizarMontoPagadoPorDia();
    }

    private void actualizarMontoPagadoPorDia(){
        montoPagadoDiario.set(pagoService.getTotalPagoPorDia(LocalDateTime.now()));
    }

    private void actualizarMontoPagadoMensual(){
        montoPagadoMensual.set(pagoService.getTotalPagoPorMes(LocalDateTime.now()));
    }

    public LongProperty idProperty() { return id; }
    public ObjectProperty<Gasto> gastoProperty() { return gasto; }
    public ObjectProperty<LocalDateTime> fechaPagoProperty() { return fechaPago; }
    public ObjectProperty<BigDecimal> montoPagadoProperty() { return montoPagado; }
    public ObjectProperty<BigDecimal> montoPagadoDiarioProperty() { return montoPagadoDiario; }
    public ObjectProperty<BigDecimal> montoPagadoMensualProperty() { return montoPagadoMensual; }
}
