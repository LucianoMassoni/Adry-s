package com.negocio.adris.model.repositories;

import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.exceptions.GastoNotFoundException;

import java.util.List;

public interface GastoRepository {
    void save(Gasto g);
    void update(Gasto g);
    void delete(long id) throws GastoNotFoundException;
    Gasto findById(long id) throws GastoNotFoundException;
    List<Gasto> findAll();
}
