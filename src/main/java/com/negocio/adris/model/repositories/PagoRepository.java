package com.negocio.adris.model.repositories;

import com.negocio.adris.model.entities.Pago;
import com.negocio.adris.model.exceptions.PagoNotFoundException;

import  java.util.List;

public interface PagoRepository {
    void save(Pago p);
    void update(Pago p);
    void delete(long id) throws PagoNotFoundException;
    Pago findById(long id) throws PagoNotFoundException;
    List<Pago> findAll();
}
