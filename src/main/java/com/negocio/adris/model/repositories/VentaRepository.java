package com.negocio.adris.model.repositories;

import com.negocio.adris.model.entities.Venta;
import com.negocio.adris.model.exceptions.VentaNotFoundException;

import java.util.List;

public interface VentaRepository {
    void save(Venta v);
    void update(Venta v);
    void delete(long id);
    Venta findById(long id) throws VentaNotFoundException;
    List<Venta> findAll() throws VentaNotFoundException;
}
