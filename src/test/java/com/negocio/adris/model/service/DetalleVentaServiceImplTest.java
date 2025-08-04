package com.negocio.adris.model.service;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.negocio.adris.model.dtos.DetalleVentaDto;
import com.negocio.adris.model.entities.DetalleVenta;
import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.enums.TipoProducto;
import com.negocio.adris.model.enums.UnidadMedida;
import com.negocio.adris.model.exceptions.DetalleVentaNotFoundException;
import com.negocio.adris.model.repositories.DetalleVentaRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class DetalleVentaServiceImplTest {
//    @Mock
    private DetalleVentaRepository repo;
//
    private DetalleVentaServiceImpl detalleVentaService;

    @BeforeEach
    void setUp() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        repo = mock(DetalleVentaRepository.class);
        ProductoService productoService = mock(ProductoService.class); // 👈 el que te falta

        detalleVentaService = new DetalleVentaServiceImpl(validator, repo, productoService);

//        MockitoAnnotations.openMocks(this);
//
//        Injector injector = Guice.createInjector(new AbstractModule() {
//            @Override
//            protected void configure() {
//                bind(DetalleVentaRepository.class).toInstance(repo);
//                bind(Validator.class).toProvider(() -> Validation.buildDefaultValidatorFactory().getValidator()); // valido con mi config de validator
//            }
//        });
//
//        detalleVentaService = injector.getInstance(DetalleVentaServiceImpl.class);
    }

    @Test
    void validarDto_Correcto(){
        DetalleVentaDto dto = new DetalleVentaDto(
                new Producto(1, "a", "b", 12, UnidadMedida.UNIDAD, 4, BigDecimal.valueOf(900), BigDecimal.valueOf(10), BigDecimal.valueOf(1000), TipoProducto.SNACKS_Y_SUELTOS),
                1,
                BigDecimal.valueOf(0)
        );
        assertDoesNotThrow(() -> detalleVentaService.validarDetalleVentaDto(dto));
    }

    @Test
    void validarDto_productoIncorrecto(){
        DetalleVentaDto dto = new DetalleVentaDto(
                null,
                2,
                BigDecimal.valueOf(0)
        );
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> detalleVentaService.validarDetalleVentaDto(dto));
        assertEquals("DetalleVenta necesita un producto al cual hacer referencia", exception.getMessage());
    }

    @Test
    void validarDto_CantidadIncorrecta(){
        DetalleVentaDto dto = new DetalleVentaDto(
                new Producto(1, "a", "b", 12, UnidadMedida.UNIDAD, 4, BigDecimal.valueOf(900), BigDecimal.valueOf(10), BigDecimal.valueOf(1000), TipoProducto.SNACKS_Y_SUELTOS),
                0,
                BigDecimal.valueOf(0)
        );
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> detalleVentaService.validarDetalleVentaDto(dto));
        assertEquals("La cantidad no puede ser negativa", exception.getMessage());
    }

    @Test
    void validarDto_descuentoIncorrecto(){
        DetalleVentaDto dto = new DetalleVentaDto(
                new Producto(1, "a", "b", 12, UnidadMedida.UNIDAD, 4, BigDecimal.valueOf(900), BigDecimal.valueOf(10), BigDecimal.valueOf(1000), TipoProducto.SNACKS_Y_SUELTOS),
                2,
                BigDecimal.valueOf(-2)
        );
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> detalleVentaService.validarDetalleVentaDto(dto));
        assertEquals("El descuento no puede ser negativo", exception.getMessage());
    }

    @Test
    void ConvertirDtoADetalleVenta_Correcto(){
        DetalleVentaDto dto = new DetalleVentaDto(
                new Producto(1, "a", "b", 12, UnidadMedida.UNIDAD, 4, BigDecimal.valueOf(900), BigDecimal.valueOf(10), BigDecimal.valueOf(1000), TipoProducto.SNACKS_Y_SUELTOS),
                2,
                BigDecimal.valueOf(0)
        );

        assertDoesNotThrow(() -> detalleVentaService.convertirDtoADetalleVenta(dto, 2L));

        DetalleVenta dv = detalleVentaService.convertirDtoADetalleVenta(dto, 2L);

        assertEquals(BigDecimal.valueOf(2000), dv.getSubtotal());
    }

    @Test
    void ConvertirDtoADetalleVenta_aplicandoDescuento_Correcto(){
        DetalleVentaDto dto = new DetalleVentaDto(
                new Producto(1, "a", "b", 12, UnidadMedida.UNIDAD, 4, BigDecimal.valueOf(900), BigDecimal.valueOf(10), BigDecimal.valueOf(1000), TipoProducto.SNACKS_Y_SUELTOS),
                2,
                BigDecimal.valueOf(10)
        );

        assertDoesNotThrow(() -> detalleVentaService.convertirDtoADetalleVenta(dto, 2L));

        DetalleVenta dv = detalleVentaService.convertirDtoADetalleVenta(dto, 2L);

        assertEquals(BigDecimal.valueOf(1800.0), dv.getSubtotal());
    }

    @Test
    void CrearDetalleVenta_Correcto() {
        DetalleVentaDto dto = new DetalleVentaDto(
                new Producto(1, "a", "b", 12, UnidadMedida.UNIDAD, 4, BigDecimal.valueOf(900), BigDecimal.valueOf(10), BigDecimal.valueOf(1000), TipoProducto.SNACKS_Y_SUELTOS),
                2,
                BigDecimal.valueOf(0)
        );

        assertDoesNotThrow(() ->  detalleVentaService.crearDetalleVenta(dto, 2));
    }

    @Test
    void modificarDetalleVenta_ConDatosValidos_ActualizaCorrectamente() throws DetalleVentaNotFoundException {
        // Configuración
        DetalleVentaDto dto = new DetalleVentaDto(
                new Producto(1, "a", "b", 12, UnidadMedida.UNIDAD, 4, BigDecimal.valueOf(900), BigDecimal.valueOf(10), BigDecimal.valueOf(1000), TipoProducto.SNACKS_Y_SUELTOS),
                2,
                BigDecimal.ZERO
        );
        DetalleVenta detalleExistente = new DetalleVenta(/* datos iniciales */);
        when(repo.findById(anyLong())).thenReturn(detalleExistente);

        // Ejecución
        detalleVentaService.modificarDetalleVenta(dto, 1L);

        // Verificación
        verify(repo).update(any(DetalleVenta.class));
        assertEquals(BigDecimal.valueOf(2000), detalleExistente.getSubtotal()); // Verifica cálculo correcto
    }

    @Test
    void modificarDetalleVenta_ConIdInexistente_LanzaException() throws DetalleVentaNotFoundException {
        when(repo.findById(anyLong())).thenThrow(new DetalleVentaNotFoundException("No encontrado"));

        assertThrows(DetalleVentaNotFoundException.class, () -> detalleVentaService.modificarDetalleVenta( new DetalleVentaDto(
                new Producto(1, "a", "b", 12, UnidadMedida.UNIDAD, 4, BigDecimal.valueOf(900), BigDecimal.valueOf(10), BigDecimal.valueOf(1000), TipoProducto.SNACKS_Y_SUELTOS),
                2,
                BigDecimal.ZERO
        ), 999));
    }

    @Test
    void eliminarDetalleVenta_ConIdExistente_EliminaCorrectamente() throws DetalleVentaNotFoundException {
        when(repo.findById(anyLong())).thenReturn(new DetalleVenta());

        detalleVentaService.eliminarDetalleVenta(1);

        verify(repo).delete(1);
    }

    @Test
    void eliminarDetalleVenta_ConIdInexistente_LanzaException() throws DetalleVentaNotFoundException {
        when(repo.findById(anyLong())).thenThrow(new DetalleVentaNotFoundException("No encontrado"));

        assertThrows(DetalleVentaNotFoundException.class, () -> detalleVentaService.eliminarDetalleVenta(999L));
    }

    @Test
    void obtenerDetalleVenta_ConIdExistente_RetornaDetalle() throws DetalleVentaNotFoundException {
        DetalleVenta detalleMock = new DetalleVenta(1, 1, 1, 1, BigDecimal.valueOf(2), BigDecimal.valueOf(10), BigDecimal.valueOf(20));
        when(repo.findById(1L)).thenReturn(detalleMock);

        DetalleVenta resultado = detalleVentaService.obtenerDetalleVenta(1L);

        assertEquals(detalleMock, resultado);
    }

    @Test
    void obtenerDetalleVenta_ConIdInexistente_LanzaException() throws DetalleVentaNotFoundException {
        when(repo.findById(anyLong())).thenThrow(new DetalleVentaNotFoundException("No encontrado"));

        assertThrows(DetalleVentaNotFoundException.class, () -> detalleVentaService.obtenerDetalleVenta(999));
    }

    @Test
    void obtenerTodosLosDetalleVenta_ConDatos_RetornaLista() throws DetalleVentaNotFoundException {
        List<DetalleVenta> listaMock = List.of(new DetalleVenta(), new DetalleVenta());
        when(repo.findAll()).thenReturn(listaMock);

        List<DetalleVenta> resultado = detalleVentaService.obtenerTodosLosDetalleVenta();

        assertEquals(2, resultado.size());
    }

    @Test
    void obtenerTodosLosDetalleVenta_SinDatos_LanzaException() throws DetalleVentaNotFoundException {
        when(repo.findAll()).thenThrow(DetalleVentaNotFoundException.class);

        assertThrows(DetalleVentaNotFoundException.class, () -> detalleVentaService.obtenerTodosLosDetalleVenta());
    }

}
