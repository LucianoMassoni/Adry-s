package com.negocio.adris.model.service;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.negocio.adris.model.dtos.ProductoDto;
import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.enums.TipoProducto;
import com.negocio.adris.model.enums.UnidadMedida;
import com.negocio.adris.model.exceptions.ProductoNotFoundException;
import com.negocio.adris.model.repositories.ProductoRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductoServiceImplTest {
    @Mock
    private ProductoRepository repo;

    private ProductoServiceImpl productoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ProductoRepository.class).toInstance(repo);
                bind(Validator.class).toProvider(() -> Validation.buildDefaultValidatorFactory().getValidator()); // valido con mi config de validator
            }
        });

        productoService = injector.getInstance(ProductoServiceImpl.class);
    }


    @Test
    void CrearProducto_ConDtoValido(){
        ProductoDto dto = new ProductoDto(
                "papita",
                "quentos",
                55,
                UnidadMedida.UNIDAD,
                1,
                BigDecimal.valueOf(900),
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(990),
                TipoProducto.SNACKS_Y_SUELTOS
        );

        assertDoesNotThrow(() -> productoService.crearProducto(dto));
    }


    @Test
    void CrearProducto_ConDtoNombreInvalido_DebeTirarError(){
        ProductoDto dto = new ProductoDto(
                "",
                "quentos",
                55,
                UnidadMedida.UNIDAD,
                1,
                BigDecimal.valueOf(900),
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(990),
                TipoProducto.SNACKS_Y_SUELTOS
        );


        Exception exception = assertThrows(IllegalArgumentException.class, () -> productoService.crearProducto(dto));

        assertTrue(exception.getMessage().contains("El nombre es necesario"));
        verify(repo, never()).save(any());
    }

    @Test
    void CrearProducto_ConDtoMarcaInvalido_DebeTirarError(){
        ProductoDto dto = new ProductoDto(
                "papitas",
                "",
                55,
                UnidadMedida.UNIDAD,
                1,
                BigDecimal.valueOf(900),
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(990),
                TipoProducto.SNACKS_Y_SUELTOS
        );


        Exception exception = assertThrows(IllegalArgumentException.class, () -> productoService.crearProducto(dto));

        assertTrue(exception.getMessage().contains("La marca es necesaria"));
        verify(repo, never()).save(any());
    }

    @Test
    void CrearProducto_ConDtoPesoInvalido_DebeTirarError(){
        ProductoDto dto = new ProductoDto(
                "papitas",
                "quentos",
                0,
                UnidadMedida.UNIDAD,
                1,
                BigDecimal.valueOf(900),
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(990),
                TipoProducto.SNACKS_Y_SUELTOS
        );


        Exception exception = assertThrows(IllegalArgumentException.class, () -> productoService.crearProducto(dto));

        assertTrue(exception.getMessage().contains("El peso del producto no puede ser negativo"));
        verify(repo, never()).save(any());
    }

    @Test
    void CrearProducto_ConDtoCantidadInvalido_DebeTirarError(){
        ProductoDto dto = new ProductoDto(
                "papitas",
                "quentos",
                55,
                UnidadMedida.UNIDAD,
                0,
                BigDecimal.valueOf(900),
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(990),
                TipoProducto.SNACKS_Y_SUELTOS
        );


        Exception exception = assertThrows(IllegalArgumentException.class, () -> productoService.crearProducto(dto));

        assertTrue(exception.getMessage().contains("La cantidad de productos no puede ser negativa"));
        verify(repo, never()).save(any());
    }

    @Test
    void CrearProducto_ConDtoCostoInvalido_DebeTirarError(){
        ProductoDto dto = new ProductoDto(
                "papitas",
                "quentos",
                55,
                UnidadMedida.UNIDAD,
                5,
                BigDecimal.valueOf(0),
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(990),
                TipoProducto.SNACKS_Y_SUELTOS
        );


        Exception exception = assertThrows(IllegalArgumentException.class, () -> productoService.crearProducto(dto));

        assertTrue(exception.getMessage().contains("El costo del producto no puede ser negativo"));
        verify(repo, never()).save(any());
    }

    @Test
    void CrearProducto_ConDtoGananciaInvalido_DebeTirarError(){
        ProductoDto dto = new ProductoDto(
                "papitas",
                "quentos",
                55,
                UnidadMedida.UNIDAD,
                5,
                BigDecimal.valueOf(900),
                BigDecimal.valueOf(0),
                BigDecimal.valueOf(990),
                TipoProducto.SNACKS_Y_SUELTOS
        );


        Exception exception = assertThrows(IllegalArgumentException.class, () -> productoService.crearProducto(dto));

        assertTrue(exception.getMessage().contains("La ganancia del producto no puede ser negativa"));
        verify(repo, never()).save(any());
    }

    @Test
    void CrearProducto_ConDtoPrecioInvalido_DebeTirarError(){
        ProductoDto dto = new ProductoDto(
                "papitas",
                "quentos",
                55,
                UnidadMedida.UNIDAD,
                5,
                BigDecimal.valueOf(900),
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(0), 
                TipoProducto.SNACKS_Y_SUELTOS
        );


        Exception exception = assertThrows(IllegalArgumentException.class, () -> productoService.crearProducto(dto));

        assertTrue(exception.getMessage().contains("El precio de un producto no puede ser negativo"));
        verify(repo, never()).save(any());
    }

    @Test
    void obtenerProductoPorId_conProductoExistente() throws ProductoNotFoundException {
        Producto productoMock = new Producto(
                1,
                "papitas",
                "quentos",
                55,
                UnidadMedida.UNIDAD,
                5,
                BigDecimal.valueOf(900),
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(990),
                TipoProducto.SNACKS_Y_SUELTOS
        );

        when(repo.findById(1)).thenReturn(productoMock);

        Producto producto = productoService.obtenerProductoPorId(1);

        assertEquals(producto, productoMock);
    }

    @Test
    void obtenerProductoPorId_conProductoInexistente() throws ProductoNotFoundException {
        long id = 1;

        when(repo.findById(anyLong())).thenThrow(new ProductoNotFoundException("Producto con ID " + id + " no encontrado"));

        assertThrows(ProductoNotFoundException.class, () -> {
            productoService.obtenerProductoPorId(id);
        });
    }

    @Test
    void eliminarProducto_deberiaBorrarCuandoExiste() throws ProductoNotFoundException {
        // Configurar el mock para simular que existe
        Producto productoMock = new Producto(
                1,
                "Pan",
                "adero",
                1.0,
                UnidadMedida.UNIDAD,
                10,
                BigDecimal.valueOf(90.0),
                BigDecimal.valueOf(0.2),
                BigDecimal.valueOf(108.0),
                TipoProducto.PANIFICADOS);
        when(repo.findById(1)).thenReturn(productoMock);

        productoService.eliminarProducto(1);

        verify(repo, times(1)).delete(1);
    }

    @Test
    void eliminarProducto_deberiaLanzarExcepcionCuandoNoExiste() throws ProductoNotFoundException {
        // Configurar el mock para simular que NO existe
        when(repo.findById(anyLong())).thenThrow(new ProductoNotFoundException("Producto no encontrado"));

        // Verificar que lanza la excepción
        assertThrows(ProductoNotFoundException.class, () -> {
            productoService.eliminarProducto(999);
        });

        // Verificar que NUNCA se llamó a delete()
        verify(repo, never()).delete(anyLong());
    }

    @Test
    void modificarProducto_deberiaActualizarCuandoExiste() throws ProductoNotFoundException {
        // Configurar producto existente
        Producto productoExistente = new Producto(1,
                "Pan",
                "Adero",
                1.0,
                UnidadMedida.UNIDAD,
                10,
                BigDecimal.valueOf(90.0),
                BigDecimal.valueOf(0.2),
                BigDecimal.valueOf(108.0),
                TipoProducto.PANIFICADOS);

        when(repo.findById(1)).thenReturn(productoExistente);

        // Nuevos datos para actualizar
        ProductoDto dto = new ProductoDto(
                "pan con semillas",
                "Adero",
                1.0,
                UnidadMedida.UNIDAD,
                5,
                BigDecimal.valueOf(95.0),
                BigDecimal.valueOf(0.15),
                BigDecimal.valueOf(109.25),
                TipoProducto.PANIFICADOS);

        // Ejecutar modificación
        productoService.modificarProducto(1, dto);

        // Verificar que se llamó a update() con los datos correctos
        verify(repo).update(argThat(productoActualizado ->
                productoActualizado.getNombre().equals("Pan con semillas") &&
                        productoActualizado.getCantidad() == 5
        ));
    }

    @Test
    void crearProducto_UnidadMediaIncorrecta_debeTirarError(){
        ProductoDto dto = new ProductoDto(
                "pan con semillas",
                "Adero",
                1.0,
                UnidadMedida.LITROS,
                5,
                BigDecimal.valueOf(95.0),
                BigDecimal.valueOf(0.15),
                BigDecimal.valueOf(109.25),
                TipoProducto.PANIFICADOS);

        assertThrows(IllegalArgumentException.class, () -> productoService.crearProducto(dto));
    }
}
