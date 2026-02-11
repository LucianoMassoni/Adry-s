package com.negocio.adris.model.repositories;

import com.negocio.adris.model.entities.Pago;
import com.negocio.adris.model.exceptions.PagoNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import  java.util.List;
import java.util.Map;

public interface PagoRepository {
    void save(Pago p);
    void update(Pago p);
    void delete(long id) throws PagoNotFoundException;
    Pago findById(long id) throws PagoNotFoundException;
    List<Pago> findAll();
    List<Pago> getAllPagosPorFecha(String fecha);
    Map<LocalDate, BigDecimal> getEgresosMes(YearMonth yearMonth);

}
