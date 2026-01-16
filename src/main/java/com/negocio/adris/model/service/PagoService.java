package com.negocio.adris.model.service;

import com.negocio.adris.model.dtos.PagoDto;
import com.negocio.adris.model.entities.Pago;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.PagoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PagoService {
    void guardar(PagoDto dto) throws GastoNotFoundException, ProveedorNotFoundException;
    void modificar(long id, PagoDto dto) throws PagoNotFoundException, GastoNotFoundException, ProveedorNotFoundException;
    void eliminar(long id) throws PagoNotFoundException;
    Pago getPago(long id) throws PagoNotFoundException;
    List<Pago> getPagos();
    List<Pago> getAllPagosPorDia(LocalDateTime fecha);
    BigDecimal getTotalPagoPorDia(LocalDateTime fecha);
    List<Pago> getAllPagosPorMes(LocalDateTime fecha);
    BigDecimal getTotalPagoPorMes(LocalDateTime fecha);
}
