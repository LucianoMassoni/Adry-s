package com.negocio.adris.model.service;

import com.negocio.adris.model.dtos.AsientoDto;
import com.negocio.adris.model.dtos.CuentaDto;
import com.negocio.adris.model.entities.Cuenta;
import com.negocio.adris.model.exceptions.CuentaNotFoundException;

import java.util.List;

public interface CuentaService {
    void validarAsiento(List<CuentaDto> cuentas);
    void registrarAsiento(AsientoDto asientoDto);
    void crearCuenta(CuentaDto dto);
    void modificarCuenta(CuentaDto dto, long id) throws CuentaNotFoundException;
    void eliminarCuenta(long id) throws CuentaNotFoundException;
    Cuenta obtenerCuenta(long id) throws CuentaNotFoundException;
    List<Cuenta> obtenerTodasCuentas() throws CuentaNotFoundException;
}
