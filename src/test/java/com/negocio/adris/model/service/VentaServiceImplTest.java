package com.negocio.adris.model.service;

import com.negocio.adris.model.dtos.DetalleVentaDto;
import com.negocio.adris.model.dtos.VentaDto;
import com.negocio.adris.model.entities.DetalleVenta;
import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.entities.Venta;
import com.negocio.adris.model.enums.FormaDePago;
import com.negocio.adris.model.enums.TipoProducto;
import com.negocio.adris.model.enums.UnidadMedida;
import com.negocio.adris.model.exceptions.VentaNotFoundException;
import com.negocio.adris.model.repositories.VentaRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VentaServiceImplTest {
    @Mock
    private VentaRepository ventaRepo;


    @InjectMocks
    private VentaServiceImpl ventaService;
    @InjectMocks
    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        ventaService = new VentaServiceImpl(validator, ventaRepo, productoService);
    }

    @Test
    void validarVenta_ConDtoValido_NoLanzaExcepcion() {
        VentaDto dto = new VentaDto(
                List.of(new DetalleVentaDto(new Producto(1, "a", "b", 12, 12, UnidadMedida.UNIDAD, 4, BigDecimal.valueOf(900), BigDecimal.valueOf(10), BigDecimal.valueOf(1000), TipoProducto.SNACKS_Y_SUELTOS, false),
                        BigDecimal.valueOf(1),
                        BigDecimal.valueOf(0),
                        null)),
                LocalDateTime.now(),
                FormaDePago.EFECTIVO
        );

        assertDoesNotThrow(() -> ventaService.validarVenta(dto));
    }

    @Test
    void validarVenta_ConFechaNula_LanzaExcepcion() {
        VentaDto dto = new VentaDto(// Fecha nula
                List.of(new DetalleVentaDto()),
                null,
                FormaDePago.TARJETA
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ventaService.validarVenta(dto));

        assertTrue(exception.getMessage().contains("fecha"));
    }

    @Test
    void calcularTotal_ConDetalles_RetornaSumaCorrecta() {
        Producto producto = new Producto(1, "a", "b", 12, 12, UnidadMedida.UNIDAD, 4, BigDecimal.valueOf(900), BigDecimal.valueOf(10), BigDecimal.valueOf(1000), TipoProducto.SNACKS_Y_SUELTOS, false);
        DetalleVentaDto detalle1 = new DetalleVentaDto(producto, BigDecimal.valueOf(2), BigDecimal.valueOf(10), null); // 1000 * 2 - 10% = 1800
        DetalleVentaDto detalle2 = new DetalleVentaDto(producto, BigDecimal.valueOf(1), BigDecimal.valueOf(5), null);  // 1000 * 1 - 5% = 950


        BigDecimal total = ventaService.calcularTotal(List.of(detalle1, detalle2));

        assertEquals(BigDecimal.valueOf(2750), total); // 190 + 95
    }

    @Test
    void crearVenta_ConDetalleInvalido_LanzaExcepcion() {
        VentaDto dto = new VentaDto(
                List.of(new DetalleVentaDto(null, BigDecimal.valueOf(1), BigDecimal.ZERO, null)), // Producto nulo
                LocalDateTime.now(),
                FormaDePago.EFECTIVO
        );



        assertThrows(IllegalArgumentException.class,
                () -> ventaService.crearVenta(dto));

        verify(ventaRepo, never()).save(any());
    }


    @Test
    void eliminarVenta_ConIdExistente_EliminaCorrectamente() throws VentaNotFoundException {
        when(ventaRepo.findById(1L)).thenReturn(new Venta());

        ventaService.eliminarVenta(1L);

        verify(ventaRepo).delete(1L);
    }

    @Test
    void eliminarVenta_ConIdInexistente_LanzaExcepcion() throws VentaNotFoundException {
        when(ventaRepo.findById(anyLong())).thenThrow(new VentaNotFoundException("No encontrado"));

        assertThrows(VentaNotFoundException.class,
                () -> ventaService.eliminarVenta(999L));
    }

    @Test
    void obtenerVenta_ConIdExistente_RetornaVenta() throws VentaNotFoundException {
        Venta ventaMock = new Venta(1L, FormaDePago.TARJETA, LocalDateTime.now(), BigDecimal.TEN,  new ArrayList<>());
        when(ventaRepo.findById(1L)).thenReturn(ventaMock);

        Venta resultado = ventaService.obtenerVenta(1L);

        assertEquals(ventaMock, resultado);
    }

    @Test
    void obtenerTodasLasVentas_ConDatos_RetornaLista() throws VentaNotFoundException {
        List<Venta> ventasMock = List.of(
                new Venta(1L, FormaDePago.EFECTIVO,LocalDateTime.now(), BigDecimal.TEN, new ArrayList<>()),
                new Venta(2L, FormaDePago.EFECTIVO, LocalDateTime.now(), BigDecimal.valueOf(20), new ArrayList<>())
        );
        when(ventaRepo.findAll()).thenReturn(ventasMock);

        List<Venta> resultado = ventaService.obtenerTodasLasVentas();

        assertEquals(2, resultado.size());
    }

    @Test
    void obtenerTodasLasVentas_SinDatos_LanzaExcepcion() throws VentaNotFoundException {
        when(ventaRepo.findAll()).thenThrow(new VentaNotFoundException("no se encontraron ventas"));

        assertThrows(VentaNotFoundException.class,
                () -> ventaService.obtenerTodasLasVentas());
    }
}
