package com.negocio.adris.model.repositories;

import com.negocio.adris.model.entities.Cuenta;
import com.negocio.adris.model.exceptions.CuentaNotFoundException;

import java.util.List;

public interface CuentaRepository {
    void save(Cuenta c);
    void update(Cuenta c);
    void delete(long id);
    Cuenta findById(long id) throws CuentaNotFoundException;
    List<Cuenta> findAll() throws CuentaNotFoundException;
}