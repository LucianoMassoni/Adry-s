package com.negocio.adris.model.service;

import com.negocio.adris.model.dtos.AsientoDto;
import com.negocio.adris.model.dtos.CuentaDto;
import com.negocio.adris.model.entities.Cuenta;
import com.negocio.adris.model.enums.TipoCuenta;
import com.negocio.adris.model.exceptions.CuentaNotFoundException;
import com.negocio.adris.model.repositories.CuentaRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CuentaServiceImplTest {
    @Mock
    private CuentaRepository repo;
    @InjectMocks
    private CuentaServiceImpl cuentaService;

    @BeforeEach
    void setUp() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        cuentaService = new CuentaServiceImpl(validator, repo);
    }

    @Test
    void crearCuenta_dtoValido() {
        CuentaDto dto = new CuentaDto(TipoCuenta.ACTIVO_POSITIVO, "a caja", BigDecimal.ZERO, new BigDecimal(20), LocalDate.now());

        assertDoesNotThrow(() -> cuentaService.crearCuenta(dto));
    }

    @Test
    void creaCuenta_tipoCuentaNull() {
        CuentaDto dto = new CuentaDto(null, "a caja", BigDecimal.ZERO, new BigDecimal(20), LocalDate.now());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> cuentaService.crearCuenta(dto));

        assertTrue(ex.getMessage().contains("La cuenta necesita un tipo"));
        verify(repo, never()).save(any());
    }

    @Test
    void crearCuenta_detalleInvalido() {
        CuentaDto dto = new CuentaDto(TipoCuenta.ACTIVO_POSITIVO, "", BigDecimal.ZERO, new BigDecimal(20), LocalDate.now());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> cuentaService.crearCuenta(dto));

        assertTrue(ex.getMessage().contains("detalle"));
        verify(repo, never()).save(any());
    }

    @Test
    void crearCuenta_debeNegativo() {
        CuentaDto dto = new CuentaDto(TipoCuenta.ACTIVO_POSITIVO, "a caja", new BigDecimal(-1), new BigDecimal(20), LocalDate.now());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> cuentaService.crearCuenta(dto));

        assertTrue(ex.getMessage().contains("debe"));
        verify(repo, never()).save(any());
    }

    @Test
    void crearCuenta_haberNegativo() {
        CuentaDto dto = new CuentaDto(TipoCuenta.ACTIVO_POSITIVO, "a caja", new BigDecimal(0), new BigDecimal(-20), LocalDate.now());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> cuentaService.crearCuenta(dto));

        assertTrue(ex.getMessage().contains("haber"));
        verify(repo, never()).save(any());
    }

    @Test
    void crearCuenta_fechaNull() {
        CuentaDto dto = new CuentaDto(TipoCuenta.ACTIVO_POSITIVO, "a caja", new BigDecimal(0), new BigDecimal(20), null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> cuentaService.crearCuenta(dto));

        assertTrue(ex.getMessage().contains("null"));
        verify(repo, never()).save(any());
    }

    @Test
    void modificarCuenta_correcto() throws CuentaNotFoundException {
        Cuenta c = new Cuenta(1, TipoCuenta.ACTIVO_POSITIVO, "a caja", new BigDecimal(0), new BigDecimal(20), LocalDate.now());

        CuentaDto dto = new CuentaDto(TipoCuenta.ACTIVO_POSITIVO, "mercaderia", new BigDecimal(0), new BigDecimal(20), LocalDate.now());

        when(repo.findById(1L)).thenReturn(c);

        cuentaService.modificarCuenta(dto, 1L);

        verify(repo).update(c);
        assertEquals(c.getDetalle(), dto.getDetalle());
    }

    // las verifications para modificar son las mismas que para crear

    @Test
    void eliminarCuenta_cuentaExistente() throws CuentaNotFoundException {
        Cuenta c = new Cuenta(1, TipoCuenta.ACTIVO_POSITIVO, "a caja", new BigDecimal(0), new BigDecimal(20), LocalDate.now());

        when(repo.findById(1L)).thenReturn(c);

        assertDoesNotThrow(() -> cuentaService.eliminarCuenta(1L));

        verify(repo).delete(1L);
    }

    @Test
    void eliminarCuenta_cuentaInexistente() throws CuentaNotFoundException {
        when(repo.findById(anyLong())).thenThrow(new CuentaNotFoundException("cuenta no encontrada"));

        Exception ex = assertThrows(CuentaNotFoundException.class, () -> cuentaService.eliminarCuenta(1L));

        assertEquals("cuenta no encontrada", ex.getMessage());
    }

    @Test
    void obtenerCuenta_existente() throws CuentaNotFoundException {
        Cuenta mock = new Cuenta(1, TipoCuenta.ACTIVO_POSITIVO, "a caja", new BigDecimal(0), new BigDecimal(20), LocalDate.now());

        when(repo.findById(1L)).thenReturn(mock);

        Cuenta c = cuentaService.obtenerCuenta(1L);

        assertEquals(c, mock);
    }

    @Test
    void obtenerCuenta_inexistente() throws CuentaNotFoundException {
        when(repo.findById(anyLong())).thenThrow(new CuentaNotFoundException("cuenta no encontrada"));

        Exception ex = assertThrows(CuentaNotFoundException.class, () -> cuentaService.obtenerCuenta(1L));

        assertEquals("cuenta no encontrada", ex.getMessage());
    }

    @Test
    void obtenerTodasLasCuentas_existente() throws CuentaNotFoundException {
        List<Cuenta> listaMock = new ArrayList<>();
        Cuenta mock = new Cuenta(1, TipoCuenta.ACTIVO_POSITIVO, "a caja", new BigDecimal(0), new BigDecimal(20), LocalDate.now());
        Cuenta mock2 = new Cuenta(2, TipoCuenta.GANANCIA_POSITIVO, "venta", new BigDecimal(200), BigDecimal.ZERO, LocalDate.now());

        listaMock.add(mock);
        listaMock.add(mock2);

        when(repo.findAll()).thenReturn(listaMock);

        List<Cuenta> lista = cuentaService.obtenerTodasCuentas();

        assertFalse(lista.isEmpty());
        assertEquals(2, lista.size());
        assertEquals(listaMock.get(0), lista.get(0));
        assertEquals(listaMock.get(1), lista.get(1));
    }

    @Test
    void validarAsiento_balanceado_noLanzaExcepcion() {
        List<CuentaDto> cuentas = List.of(
                new CuentaDto(TipoCuenta.ACTIVO_POSITIVO, "Caja", new BigDecimal("0"), new BigDecimal("100"), LocalDate.now()),
                new CuentaDto(TipoCuenta.GANANCIA_POSITIVO, "Venta", new BigDecimal("100"), new BigDecimal("0"), LocalDate.now())
        );

        assertDoesNotThrow(() -> cuentaService.validarAsiento(cuentas));
    }

    @Test
    void validarAsiento_noBalanceado_lanzaExcepcion() {
        List<CuentaDto> cuentas = List.of(
                new CuentaDto(TipoCuenta.ACTIVO_POSITIVO, "Caja", new BigDecimal("0"), new BigDecimal("100"), LocalDate.now()),
                new CuentaDto(TipoCuenta.GANANCIA_POSITIVO, "Venta", new BigDecimal("90"), new BigDecimal("0"), LocalDate.now())
        );

        Exception ex = assertThrows(IllegalArgumentException.class, () -> cuentaService.validarAsiento(cuentas));
        assertEquals("El asiento no está balanceado: debe ≠ haber", ex.getMessage());
    }

    @Test
    void registrarAsiento_asientoValido_guardaTodo() {
        List<CuentaDto> cuentas = List.of(
                new CuentaDto(TipoCuenta.ACTIVO_POSITIVO, "Caja", BigDecimal.ZERO, new BigDecimal("100"), LocalDate.now()),
                new CuentaDto(TipoCuenta.GANANCIA_POSITIVO, "Venta", new BigDecimal("100"), BigDecimal.ZERO, LocalDate.now())
        );
        AsientoDto asiento = new AsientoDto(LocalDate.now(), cuentas);

        cuentaService.registrarAsiento(asiento);

        verify(repo, times(2)).save(any(Cuenta.class));
    }

    @Test
    void registrarAsiento_asientoNoBalanceado_lanzaExcepcionYNoGuarda() {
        List<CuentaDto> cuentas = List.of(
                new CuentaDto(TipoCuenta.ACTIVO_POSITIVO, "Caja", BigDecimal.ZERO, new BigDecimal("100"), LocalDate.now()),
                new CuentaDto(TipoCuenta.GANANCIA_POSITIVO, "Venta", new BigDecimal("90"), BigDecimal.ZERO, LocalDate.now())
        );
        AsientoDto asiento = new AsientoDto(LocalDate.now(), cuentas);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> cuentaService.registrarAsiento(asiento));
        assertEquals("El asiento no está balanceado: debe ≠ haber", ex.getMessage());

        verify(repo, never()).save(any());
    }

    @Test
    void registrarAsiento_cuentaInvalida_lanzaExcepcionYNoGuarda() {
        List<CuentaDto> cuentas = List.of(
                new CuentaDto(null, "Caja", BigDecimal.ZERO, new BigDecimal("100"), LocalDate.now()),
                new CuentaDto(TipoCuenta.GANANCIA_POSITIVO, "Venta", new BigDecimal("100"), BigDecimal.ZERO, LocalDate.now())
        );
        AsientoDto asiento = new AsientoDto(LocalDate.now(), cuentas);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> cuentaService.registrarAsiento(asiento));
        assertTrue(ex.getMessage().contains("tipo"));

        verify(repo, never()).save(any());
    }
}