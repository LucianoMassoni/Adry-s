package com.negocio.adris.viewmodel;

import com.negocio.adris.model.dtos.GastoDto;
import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.model.service.GastoService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GastoViewModel {
    private final GastoService gastoService;

    private final LongProperty id = new SimpleLongProperty();
    private final ObjectProperty<Proveedor> proveedor = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> fecha_vencimiento = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> monto = new SimpleObjectProperty<>();
    private final StringProperty nota = new SimpleStringProperty();
    private final BooleanProperty saldado = new SimpleBooleanProperty();

    private final ObservableList<Gasto> gastos = FXCollections.observableArrayList();

    public GastoViewModel(GastoService gastoService){
        this.gastoService = gastoService;
    }

    public void cargarGastos(){
        gastos.setAll(gastoService.getGastos());
    }

    public ObservableList<Gasto> getGastos(){
        return gastos;
    }

    public void limpiarFormulario(){
        id.set(0);
        proveedor.set(null);
        fecha_vencimiento.set(null);
        monto.set(null);
        nota.set(null);
        saldado.set(false);
    }

    public void cargarGasto(Gasto g){
        id.set(g.getId());
        proveedor.set(g.getProveedor());
        fecha_vencimiento.set(g.getFechaVencimiento());
        monto.set(g.getMonto());
        nota.set(g.getNota());
        saldado.set(g.isSaldado());
    }

    public void guardarGasto() throws ProveedorNotFoundException {
        GastoDto dto = new GastoDto(
                proveedor.get().getId(),
                fecha_vencimiento.get(),
                monto.get(),
                nota.get()
        );

        gastoService.crear(dto);
    }

    public void modificarGasto() throws GastoNotFoundException, ProveedorNotFoundException {
        GastoDto dto = new GastoDto(
                proveedor.get().getId(),
                fecha_vencimiento.get(),
                monto.get(),
                nota.get()
        );

        gastoService.modificar(id.get(), dto);
    }

    public void eliminarGasto() throws GastoNotFoundException {
        gastoService.eliminar(id.get());
    }


    public LongProperty idProperty() { return id; }
    public ObjectProperty<Proveedor> proveedorProperty() { return proveedor; }
    public ObjectProperty<LocalDateTime> fechaVencimientoProperty() { return fecha_vencimiento; }
    public ObjectProperty<BigDecimal> montoProperty() { return monto; }
    public StringProperty notaProperty() { return nota; }
    public BooleanProperty saldadoProperty() { return saldado; }
    public ObservableList<Gasto> gastosProperty() { return gastos; }
}
