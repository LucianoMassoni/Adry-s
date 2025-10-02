package com.negocio.adris.model.service;

import com.negocio.adris.model.dtos.PagoDto;
import com.negocio.adris.model.entities.Pago;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.PagoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;

import java.util.List;

public interface PagoService {
    void guardar(PagoDto dto) throws GastoNotFoundException, ProveedorNotFoundException;
    void modificar(long id, PagoDto dto) throws PagoNotFoundException, GastoNotFoundException, ProveedorNotFoundException;
    void eliminar(long id) throws PagoNotFoundException;
    Pago getPago(long id) throws PagoNotFoundException;
    List<Pago> getPagos();
}
