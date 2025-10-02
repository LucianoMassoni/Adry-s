package com.negocio.adris.model.service;

import com.negocio.adris.model.dtos.ProveedorDto;
import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;

import java.util.List;

public interface ProveedorService {
    void guardar(ProveedorDto dto);
    void modificar(long id, ProveedorDto dto) throws ProveedorNotFoundException;
    void eliminar(long id) throws ProveedorNotFoundException;
    Proveedor getProveedor(long id) throws ProveedorNotFoundException;
    List<Proveedor> getProveedores();
}
