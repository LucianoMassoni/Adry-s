package com.negocio.adris.model.service;

import com.negocio.adris.model.entities.BalanceDiario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {
    @Mock
    private VentaService ventaService;

    @Mock
    private PagoService pagoService;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private YearMonth yearMonth;

    @BeforeEach
    void setUp() {
        yearMonth = YearMonth.of(2024, 3); // Marzo 2024 (31 días)
    }

    @Test
    void generarBalanceMensual_debeGenerarTodosLosDiasDelMes() {
        when(ventaService.getFacturacionMes(yearMonth)).thenReturn(new HashMap<>());
        when(pagoService.getEgresosMes(yearMonth)).thenReturn(new HashMap<>());

        List<BalanceDiario> resultado = balanceService.generarBalanceMensual(yearMonth);

        assertEquals(31, resultado.size());
        assertEquals(LocalDate.of(2024,3,1), resultado.get(0).getFecha());
        assertEquals(LocalDate.of(2024,3,31), resultado.get(30).getFecha());
    }

    @Test
    void generarBalanceMensual_debeCompletarConCerosSiNoHayDatos() {
        when(ventaService.getFacturacionMes(yearMonth)).thenReturn(new HashMap<>());
        when(pagoService.getEgresosMes(yearMonth)).thenReturn(new HashMap<>());

        List<BalanceDiario> resultado = balanceService.generarBalanceMensual(yearMonth);

        resultado.forEach(b -> {
            assertEquals(BigDecimal.ZERO, b.getFacturacion());
            assertEquals(BigDecimal.ZERO, b.getEgresos());
        });
    }

    @Test
    void generarBalanceMensual_debeAsignarFacturacionYEgresosCorrectamente() {
        Map<LocalDate, BigDecimal> facturacion = new HashMap<>();
        Map<LocalDate, BigDecimal> egresos = new HashMap<>();

        LocalDate fecha = LocalDate.of(2024,3,10);

        facturacion.put(fecha, BigDecimal.valueOf(1000));
        egresos.put(fecha, BigDecimal.valueOf(300));

        when(ventaService.getFacturacionMes(yearMonth)).thenReturn(facturacion);
        when(pagoService.getEgresosMes(yearMonth)).thenReturn(egresos);

        List<BalanceDiario> resultado = balanceService.generarBalanceMensual(yearMonth);

        BalanceDiario dia10 = resultado.stream()
                .filter(b -> b.getFecha().equals(fecha))
                .findFirst()
                .orElseThrow();

        assertEquals(BigDecimal.valueOf(1000), dia10.getFacturacion());
        assertEquals(BigDecimal.valueOf(300), dia10.getEgresos());
    }

    @Test
    void generarHasta_debeGenerarDesdeEneroHastaMesSeleccionado() {
        YearMonth seleccionado = YearMonth.of(2024, 3);

        when(ventaService.getFacturacionMes(any())).thenReturn(new HashMap<>());
        when(pagoService.getEgresosMes(any())).thenReturn(new HashMap<>());

        Map<YearMonth, List<BalanceDiario>> resultado = balanceService.generarHasta(seleccionado);

        assertEquals(3, resultado.size());
        assertTrue(resultado.containsKey(YearMonth.of(2024,1)));
        assertTrue(resultado.containsKey(YearMonth.of(2024,2)));
        assertTrue(resultado.containsKey(YearMonth.of(2024,3)));
    }

    @Test
    void generarHasta_siEsEneroSoloDebeGenerarUnMes() {
        YearMonth enero = YearMonth.of(2024, 1);

        when(ventaService.getFacturacionMes(any())).thenReturn(new HashMap<>());
        when(pagoService.getEgresosMes(any())).thenReturn(new HashMap<>());

        Map<YearMonth, List<BalanceDiario>> resultado = balanceService.generarHasta(enero);

        assertEquals(1, resultado.size());
        assertTrue(resultado.containsKey(enero));
    }

    @Test
    void generarHasta_debeMantenerOrdenCronologico() {
        YearMonth seleccionado = YearMonth.of(2024, 4);

        when(ventaService.getFacturacionMes(any())).thenReturn(new HashMap<>());
        when(pagoService.getEgresosMes(any())).thenReturn(new HashMap<>());

        Map<YearMonth, List<BalanceDiario>> resultado = balanceService.generarHasta(seleccionado);

        List<YearMonth> claves = new ArrayList<>(resultado.keySet());

        assertEquals(YearMonth.of(2024,1), claves.get(0));
        assertEquals(YearMonth.of(2024,2), claves.get(1));
        assertEquals(YearMonth.of(2024,3), claves.get(2));
        assertEquals(YearMonth.of(2024,4), claves.get(3));
    }
}
