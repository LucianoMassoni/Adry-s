package com.negocio.adris.model.repositories;

import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;

import java.util.List;

public interface ProveedorRepository {
    void save(Proveedor p);
    void update(Proveedor p);
    void delete(long id);
    Proveedor findById(long id) throws ProveedorNotFoundException;
    List<Proveedor> findAll();
}
