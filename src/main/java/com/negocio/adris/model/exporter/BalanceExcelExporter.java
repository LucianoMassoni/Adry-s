package com.negocio.adris.model.exporter;

import com.negocio.adris.model.entities.BalanceDiario;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface BalanceExcelExporter {
    void exportar(Map<YearMonth, List<BalanceDiario>> datos);
}
