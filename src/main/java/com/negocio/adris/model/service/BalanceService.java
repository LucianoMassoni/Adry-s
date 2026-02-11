package com.negocio.adris.model.service;

import com.negocio.adris.model.entities.BalanceDiario;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface BalanceService {
    List<BalanceDiario> generarBalanceMensual(YearMonth yearMonth);
    Map<YearMonth, List<BalanceDiario>> generarHasta(YearMonth yearMonth);
}
