package com.negocio.adris.viewmodel;

import com.google.inject.Inject;
import com.negocio.adris.model.entities.BalanceDiario;
import com.negocio.adris.model.exporter.BalanceExcelExporter;
import com.negocio.adris.model.service.BalanceService;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;


public class BalanceVeiwModel {
    private final BalanceService balanceService;
    private final BalanceExcelExporter excelExporter;

    @Inject
    public BalanceVeiwModel(BalanceService balanceService, BalanceExcelExporter excelExporter){
        this.balanceService = balanceService;
        this.excelExporter = excelExporter;
    }

    public void exportarDatos(YearMonth yearMonth){
        Map<YearMonth, List<BalanceDiario>> balancesMap = balanceService.generarHasta(yearMonth);

        excelExporter.exportar(balancesMap);
    }


}
