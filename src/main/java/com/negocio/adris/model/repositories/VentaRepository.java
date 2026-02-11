package com.negocio.adris.model.repositories;

import com.negocio.adris.model.entities.Venta;
import com.negocio.adris.model.exceptions.VentaNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface VentaRepository {
    void save(Venta v);
    void delete(long id);
    Venta findById(long id) throws VentaNotFoundException;
    List<Venta> findAll() throws VentaNotFoundException;
    List<Venta> getAllVentasByFecha(String fecha) throws VentaNotFoundException;
    Map<LocalDate, BigDecimal> getFacturacionMes(YearMonth yearMonth);
}
