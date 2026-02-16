package com.negocio.adris.model.service;

import com.negocio.adris.model.dtos.DetalleVentaDto;
import com.negocio.adris.model.dtos.VentaDto;
import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.entities.Venta;
import com.negocio.adris.model.enums.FormaDePago;
import com.negocio.adris.model.exceptions.VentaNotFoundException;
import com.negocio.adris.model.repositories.VentaRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {
    @Mock
    private Validator validator;

    @Mock
    private VentaRepository repo;

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private VentaServiceImpl service;


    // CREAR VENTA
    @Test
    void crearVenta_valida_deberiaGuardarYActualizarStock() {
        VentaDto dto = mock(VentaDto.class);
        DetalleVentaDto detalleDto = mock(DetalleVentaDto.class);
        Producto producto = mock(Producto.class);

        when(validator.validate(dto)).thenReturn(Collections.emptySet());
        when(validator.validate(detalleDto)).thenReturn(Collections.emptySet());

        when(dto.getDetalleVentaDtos()).thenReturn(List.of(detalleDto));
        when(dto.getFormaDePago()).thenReturn(FormaDePago.EFECTIVO);
        when(dto.getFecha()).thenReturn(LocalDateTime.now());

        when(detalleDto.getProducto()).thenReturn(producto);
        when(detalleDto.getCantidad()).thenReturn(new BigDecimal("2"));
        when(detalleDto.getDescuento()).thenReturn(BigDecimal.ZERO);

        when(producto.esDivisible()).thenReturn(false);
        when(producto.getPrecio()).thenReturn(new BigDecimal("100"));

        service.crearVenta(dto);

        verify(productoService).comprarProducto(producto, new BigDecimal("2"));
        verify(repo).save(any(Venta.class));
    }

    @Test
    void crearVenta_dtoInvalido_deberiaLanzarException() {
        VentaDto dto = mock(VentaDto.class);

        ConstraintViolation<VentaDto> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Error venta");

        when(validator.validate(dto)).thenReturn(Set.of(violation));

        assertThrows(IllegalArgumentException.class, () -> service.crearVenta(dto));

        verify(repo, never()).save(any());
    }

    @Test
    void crearVenta_detalleInvalido_deberiaLanzarException() {
        VentaDto dto = mock(VentaDto.class);
        DetalleVentaDto detalleDto = mock(DetalleVentaDto.class);

        when(validator.validate(dto)).thenReturn(Collections.emptySet());

        ConstraintViolation<DetalleVentaDto> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Error detalle");

        when(validator.validate(detalleDto)).thenReturn(Set.of(violation));
        when(dto.getDetalleVentaDtos()).thenReturn(List.of(detalleDto));

        assertThrows(IllegalArgumentException.class, () -> service.crearVenta(dto));

        verify(repo, never()).save(any());
    }

    // ELIMINAR
    @Test
    void eliminarVenta_valida_deberiaEliminar() throws VentaNotFoundException {
        Venta venta = mock(Venta.class);

        when(repo.findById(1L)).thenReturn(venta);

        service.eliminarVenta(1L);

        verify(repo).delete(1L);
    }

    @Test
    void eliminarVenta_noExiste_deberiaLanzarException() throws VentaNotFoundException {
        when(repo.findById(1L)).thenThrow(new VentaNotFoundException("No existe"));

        assertThrows(VentaNotFoundException.class, () -> service.eliminarVenta(1L));

        verify(repo, never()).delete(anyLong());
    }

    // GANANCIA POR DIA
    @Test
    void obtenerGananciaPorDia_deberiaSumarCorrectamente() throws VentaNotFoundException {
        Venta v1 = mock(Venta.class);
        Venta v2 = mock(Venta.class);

        when(v1.getTotal()).thenReturn(new BigDecimal("100.555"));
        when(v2.getTotal()).thenReturn(new BigDecimal("200.333"));

        when(repo.getAllVentasByFecha(anyString())).thenReturn(List.of(v1, v2));

        BigDecimal total = service.obtenerGananciaPorDia(LocalDateTime.now());

        assertEquals(new BigDecimal("300.89"), total);
    }

    // FACTURACIÓN MES
    @Test
    void getFacturacionMes_mesFuturo_deberiaLanzarException() {
        YearMonth futuro = YearMonth.now().plusMonths(1);

        assertThrows(IllegalArgumentException.class, () -> service.getFacturacionMes(futuro));

        verify(repo, never()).getFacturacionMes(any());
    }

    @Test
    void getFacturacionMes_valido_deberiaDelegarAlRepo() {
        YearMonth actual = YearMonth.now();
        Map<LocalDate, BigDecimal> mockMap = Map.of();

        when(repo.getFacturacionMes(actual)).thenReturn(mockMap);

        Map<LocalDate, BigDecimal> resultado = service.getFacturacionMes(actual);

        assertEquals(mockMap, resultado);
    }
}
