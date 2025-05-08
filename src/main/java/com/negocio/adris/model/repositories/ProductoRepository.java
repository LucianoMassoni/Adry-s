package com.negocio.adris.model.repositories;

import com.negocio.adris.model.entities.Producto;

import java.util.List;

public interface ProductoRepository {
    void save(Producto p);
    void update(Producto p); //TODO: me faltaría un throws ProductoNotFoundException
    void delete(int id);
    Producto findById(int id);
    List<Producto> findAll();
}
