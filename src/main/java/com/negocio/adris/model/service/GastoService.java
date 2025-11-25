package com.negocio.adris.model.service;

import com.negocio.adris.model.dtos.GastoDto;
import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.entities.Pago;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;

import java.util.List;

public interface GastoService {
    void crear(GastoDto dto) throws ProveedorNotFoundException;
    void modificar(long id, GastoDto dto) throws GastoNotFoundException, ProveedorNotFoundException;
    void eliminar(long id) throws GastoNotFoundException;
    Gasto getGastoById(long id) throws GastoNotFoundException, ProveedorNotFoundException;
    List<Gasto> getGastos();

    void agregarPago(Gasto g, Pago p);
}
