package com.negocio.adris.model.service;

import com.negocio.adris.model.dtos.PagoDto;
import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.entities.Pago;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.PagoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.model.repositories.PagoRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceImplTest {
    @Mock
    private PagoRepository repo;

    @Mock
    private Validator validator;

    @Mock
    private GastoService gastoService;

    @InjectMocks
    private PagoServiceImpl service;

    // ---------------- GUARDAR
    @Test
    void guardar_pagoValidoParcial_deberiaGuardar() throws GastoNotFoundException, ProveedorNotFoundException {
        PagoDto dto = mock(PagoDto.class);

        when(validator.validate(dto)).thenReturn(Collections.emptySet());

        when(dto.getGastoId()).thenReturn(1L);
        when(dto.getMontoPagado()).thenReturn(BigDecimal.valueOf(100));
        when(dto.getFechaDePago()).thenReturn(LocalDate.now());

        Gasto gasto = mock(Gasto.class);
        when(gasto.getMontoRestante()).thenReturn(BigDecimal.valueOf(500));

        when(gastoService.getGastoById(1L)).thenReturn(gasto);

        ArgumentCaptor<Pago> captor = ArgumentCaptor.forClass(Pago.class);

        Pago resultado = service.guardar(dto);

        verify(repo).save(captor.capture());

        Pago guardado = captor.getValue();

        assertEquals(BigDecimal.valueOf(100), guardado.getMontoPagado());
        assertEquals(gasto, guardado.getGasto());
        assertEquals(dto.getFechaDePago().atStartOfDay(), guardado.getFechaPago());

        verify(gastoService, never()).saldarDeuda(any());
        assertEquals(guardado, resultado);
    }

    @Test
    void guardar_pagoExacto_deberiaSaldarDeuda() throws GastoNotFoundException, ProveedorNotFoundException {
        PagoDto dto = mock(PagoDto.class);

        when(validator.validate(dto)).thenReturn(Collections.emptySet());
        when(dto.getGastoId()).thenReturn(1L);
        when(dto.getMontoPagado()).thenReturn(BigDecimal.valueOf(500));
        when(dto.getFechaDePago()).thenReturn(LocalDate.now());

        Gasto gasto = mock(Gasto.class);
        when(gasto.getMontoRestante()).thenReturn(BigDecimal.valueOf(500));

        when(gastoService.getGastoById(1L)).thenReturn(gasto);

        service.guardar(dto);

        verify(gastoService).saldarDeuda(gasto);
        verify(repo).save(any(Pago.class));
    }

    @Test
    void guardar_pagoMayorAlRestante_deberiaLanzarException() throws GastoNotFoundException, ProveedorNotFoundException {
        PagoDto dto = mock(PagoDto.class);

        when(validator.validate(dto)).thenReturn(Collections.emptySet());
        when(dto.getGastoId()).thenReturn(1L);
        when(dto.getMontoPagado()).thenReturn(BigDecimal.valueOf(600));

        Gasto gasto = mock(Gasto.class);
        when(gasto.getMontoRestante()).thenReturn(BigDecimal.valueOf(500));

        when(gastoService.getGastoById(1L)).thenReturn(gasto);

        assertThrows(IllegalArgumentException.class, () -> service.guardar(dto));

        verify(repo, never()).save(any());
    }

    @Test
    void guardar_conErroresDeValidacion_deberiaLanzarException() {
        PagoDto dto = mock(PagoDto.class);

        ConstraintViolation<PagoDto> violation = mock(ConstraintViolation.class);
        when(validator.validate(dto)).thenReturn(Set.of(violation));

        assertThrows(IllegalArgumentException.class, () -> service.guardar(dto));

        verify(repo, never()).save(any());
    }

    @Test
    void guardar_pagoExacto_deberiaSaldarDeudaYGuardar() throws GastoNotFoundException, ProveedorNotFoundException {
        PagoDto dto = mock(PagoDto.class);

        when(validator.validate(dto)).thenReturn(Collections.emptySet());
        when(dto.getGastoId()).thenReturn(1L);
        when(dto.getMontoPagado()).thenReturn(BigDecimal.valueOf(500));
        when(dto.getFechaDePago()).thenReturn(LocalDate.now());

        Gasto gasto = mock(Gasto.class);
        when(gasto.getMontoRestante()).thenReturn(BigDecimal.valueOf(500));

        when(gastoService.getGastoById(1L)).thenReturn(gasto);

        Pago resultado = service.guardar(dto);

        assertNotNull(resultado);

        verify(gastoService).saldarDeuda(gasto);
        verify(repo).save(any(Pago.class));
    }

    // ---------------- MODIFICAR
    @Test
    void modificar_pagoValido_deberiaActualizar() throws PagoNotFoundException, GastoNotFoundException, ProveedorNotFoundException {
        Pago existente = mock(Pago.class);

        when(repo.findById(1L)).thenReturn(existente);

        PagoDto dto = mock(PagoDto.class);
        when(dto.getGastoId()).thenReturn(2L);
        when(dto.getMontoPagado()).thenReturn(BigDecimal.valueOf(300));

        Gasto gasto = mock(Gasto.class);
        when(gastoService.getGastoById(2L)).thenReturn(gasto);

        Pago resultado = service.modificar(1L, dto);

        verify(existente).setGasto(gasto);
        verify(existente).setMontoPagado(BigDecimal.valueOf(300));
        verify(repo).update(existente);

        assertEquals(existente, resultado);
    }

    @Test
    void modificar_pagoExistente_deberiaActualizar() throws PagoNotFoundException, GastoNotFoundException, ProveedorNotFoundException {
        PagoDto dto = mock(PagoDto.class);

        when(dto.getGastoId()).thenReturn(2L);
        when(dto.getMontoPagado()).thenReturn(BigDecimal.valueOf(300));

        Pago pagoExistente = mock(Pago.class);
        Gasto gasto = mock(Gasto.class);

        when(repo.findById(1L)).thenReturn(pagoExistente);
        when(gastoService.getGastoById(2L)).thenReturn(gasto);

        Pago resultado = service.modificar(1L, dto);

        verify(pagoExistente).setGasto(gasto);
        verify(pagoExistente).setMontoPagado(BigDecimal.valueOf(300));
        verify(repo).update(pagoExistente);

        assertEquals(pagoExistente, resultado);
    }

    // ---------------- ELIMINAR
    @Test
    void eliminar_deberiaLlamarAlRepo() throws PagoNotFoundException {
        Pago pago = mock(Pago.class);
        when(repo.findById(1L)).thenReturn(pago);

        service.eliminar(1L);

        verify(repo).delete(1L);
    }

    // ---------------- GET
    @Test
    void getPago_deberiaRetornarPago() throws PagoNotFoundException {
        Pago pago = mock(Pago.class);
        when(repo.findById(1L)).thenReturn(pago);

        Pago resultado = service.getPago(1L);

        assertEquals(pago, resultado);
    }

    @Test
    void getPagos_deberiaRetornarLista() {
        List<Pago> lista = List.of(mock(Pago.class));
        when(repo.findAll()).thenReturn(lista);

        List<Pago> resultado = service.getPagos();

        assertEquals(lista, resultado);
    }

    // ---------------- TOTALES DIA
    @Test
    void getTotalPagoPorDia_deberiaSumarCorrectamente() {
        LocalDateTime fecha = LocalDateTime.now();

        Pago p1 = mock(Pago.class);
        Pago p2 = mock(Pago.class);

        when(p1.getMontoPagado()).thenReturn(BigDecimal.valueOf(100));
        when(p2.getMontoPagado()).thenReturn(BigDecimal.valueOf(200));

        when(repo.getAllPagosPorFecha(fecha.format(DateTimeFormatter.ISO_LOCAL_DATE))).thenReturn(List.of(p1, p2));

        BigDecimal total = service.getTotalPagoPorDia(fecha);

        assertEquals(BigDecimal.valueOf(300).setScale(2, RoundingMode.HALF_UP), total);
    }

    @Test
    void getTotalPagoPorDia_deberiaSumarCorrectamenteConScale() {
        Pago p1 = mock(Pago.class);
        Pago p2 = mock(Pago.class);

        when(p1.getMontoPagado()).thenReturn(BigDecimal.valueOf(100.555));
        when(p2.getMontoPagado()).thenReturn(BigDecimal.valueOf(200.333));

        when(repo.getAllPagosPorFecha(anyString())).thenReturn(List.of(p1, p2));

        BigDecimal total = service.getTotalPagoPorDia(LocalDateTime.now());

        assertEquals(new BigDecimal("300.89"), total);
    }

    // ---------------- TOTALES MES
    @Test
    void getTotalPagoPorMes_deberiaSumarCorrectamente() {
        LocalDateTime fecha = LocalDateTime.now();

        Pago p1 = mock(Pago.class);
        Pago p2 = mock(Pago.class);

        when(p1.getMontoPagado()).thenReturn(BigDecimal.valueOf(150));
        when(p2.getMontoPagado()).thenReturn(BigDecimal.valueOf(350));

        when(repo.getAllPagosPorFecha(anyString())).thenReturn(List.of(p1, p2));

        BigDecimal total = service.getTotalPagoPorMes(fecha);

        assertEquals(BigDecimal.valueOf(500).setScale(2, RoundingMode.HALF_UP), total);
    }

    // ---------------- EGRESOS MES
    @Test
    void getEgresosMes_mesFuturo_deberiaLanzarException() {
        YearMonth futuro = YearMonth.now().plusMonths(1);

        assertThrows(IllegalArgumentException.class, () -> service.getEgresosMes(futuro));
    }

    @Test
    void getEgresosMes_mesValido_deberiaDelegarAlRepo() {
        YearMonth actual = YearMonth.now();

        Map<LocalDate, BigDecimal> mapa = Map.of(LocalDate.now(), BigDecimal.TEN);

        when(repo.getEgresosMes(actual)).thenReturn(mapa);

        Map<LocalDate, BigDecimal> resultado = service.getEgresosMes(actual);

        assertEquals(mapa, resultado);
    }
}
