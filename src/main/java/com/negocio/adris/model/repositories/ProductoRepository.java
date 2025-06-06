package com.negocio.adris.model.repositories;

import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.exceptions.ProductoNotFoundException;

import java.util.List;

public interface ProductoRepository {
    void save(Producto p);
    void update(Producto p);
    void delete(long id);
    Producto findById(long id) throws ProductoNotFoundException;
    List<Producto> findAll() throws ProductoNotFoundException;
}
