package com.negocio.adris.model.repositories;

import com.negocio.adris.model.entities.DetalleVenta;
import com.negocio.adris.model.exceptions.DetalleVentaNotFoundException;

import java.util.List;

public interface DetalleVentaRepository {
    void save(DetalleVenta detalleVenta);
    void update(DetalleVenta detalleVenta);
    void delete(long id);
    DetalleVenta findById(long id) throws DetalleVentaNotFoundException;
    List<DetalleVenta> findAll() throws DetalleVentaNotFoundException;
    List<DetalleVenta> findAllByVentaId(long ventaId) throws DetalleVentaNotFoundException;
}
