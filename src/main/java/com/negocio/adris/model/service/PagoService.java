package com.negocio.adris.model.service;

import com.negocio.adris.model.dtos.PagoDto;
import com.negocio.adris.model.entities.Pago;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.PagoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface PagoService {
    Pago guardar(PagoDto dto) throws GastoNotFoundException, ProveedorNotFoundException;
    Pago modificar(long id, PagoDto dto) throws PagoNotFoundException, GastoNotFoundException, ProveedorNotFoundException;
    void eliminar(long id) throws PagoNotFoundException;
    Pago getPago(long id) throws PagoNotFoundException;
    List<Pago> getPagos();
    List<Pago> getAllPagosPorDia(LocalDateTime fecha);
    BigDecimal getTotalPagoPorDia(LocalDateTime fecha);
    List<Pago> getAllPagosPorMes(LocalDateTime fecha);
    BigDecimal getTotalPagoPorMes(LocalDateTime fecha);
    Map<LocalDate, BigDecimal> getEgresosMes(YearMonth yearMonth);
}
