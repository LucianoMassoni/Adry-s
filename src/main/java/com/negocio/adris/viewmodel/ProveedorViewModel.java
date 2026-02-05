package com.negocio.adris.viewmodel;

import com.google.inject.Inject;
import com.negocio.adris.model.dtos.ProveedorDto;
import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.model.service.ProveedorService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProveedorViewModel {
    private final ProveedorService proveedorService;

    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty telefono = new SimpleStringProperty();

    private final ObservableList<Proveedor> proveedores = FXCollections.observableArrayList();

    @Inject
    public ProveedorViewModel(ProveedorService proveedorService){
        this.proveedorService = proveedorService;

        cargarProveedores();
    }

    public void limpiarFormulario(){
        id.set(0);
        nombre.set(null);
        telefono.set(null);
    }

    public void cargarProveedores() {
        proveedores.setAll(proveedorService.getProveedores());
    }

    public Proveedor getProveedor(long id) throws ProveedorNotFoundException {
        return proveedorService.getProveedor(id);
    }

    public ObservableList<Proveedor> getProveedores(){
        cargarProveedores();
        return proveedores;
    }

    public void cargarProveedor(Proveedor p){
        id.set(p.getId());
        nombre.set(p.getNombre());
        telefono.set(p.getTelefono());
    }

    public void crearProveedor(){
        ProveedorDto dto = new ProveedorDto(
                nombre.get(),
                telefono.get()
        );

        proveedorService.guardar(dto);

        cargarProveedores();
    }

    public void modificarProveedor() throws ProveedorNotFoundException {
        ProveedorDto dto = new ProveedorDto(
                nombre.get(),
                telefono.get()
        );

        proveedorService.modificar(id.get(), dto);
    }

    public void eliminarProveedor() throws ProveedorNotFoundException {
        proveedorService.eliminar(id.get());
    }


    public LongProperty idProperty() {return id; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty telefonoProperty() { return telefono; }
    public ObservableList<Proveedor> proveedoresProperty() { return proveedores; }
}
