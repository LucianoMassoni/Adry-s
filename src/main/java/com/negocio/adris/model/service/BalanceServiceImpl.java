package com.negocio.adris.model.service;

import com.google.inject.Inject;
import com.negocio.adris.model.entities.BalanceDiario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class BalanceServiceImpl implements BalanceService{
    private final VentaService ventaService;
    private final PagoService pagoService;

    @Inject
    public BalanceServiceImpl(VentaService ventaService, PagoService pagoService){
        this.ventaService = ventaService;
        this.pagoService = pagoService;
    }

    @Override
    public List<BalanceDiario> generarBalanceMensual(YearMonth yearMonth){
        List<BalanceDiario> lista = new ArrayList<>(yearMonth.lengthOfMonth());

        Map<LocalDate, BigDecimal> facturacion = ventaService.getFacturacionMes(yearMonth);
        Map<LocalDate, BigDecimal> egresos = pagoService.getEgresosMes(yearMonth);

        for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
            LocalDate fecha = yearMonth.atDay(i);

            BigDecimal fact = facturacion.getOrDefault(fecha, BigDecimal.ZERO);
            BigDecimal egr = egresos.getOrDefault(fecha, BigDecimal.ZERO);

            lista.add(new BalanceDiario(fecha, fact, egr));
        }
        return lista;
    }

    @Override
    public Map<YearMonth, List<BalanceDiario>> generarHasta(YearMonth seleccionado) {
        Map<YearMonth, List<BalanceDiario>> map = new LinkedHashMap<>();

        YearMonth actual = YearMonth.of(seleccionado.getYear(), 1);

        while (!actual.isAfter(seleccionado)){
            List<BalanceDiario> balances = generarBalanceMensual(actual);
            map.put(actual, balances);
            actual = actual.plusMonths(1);
        }

        return map;
    }
}
