package com.negocio.adris.model.viewmodel;

import com.negocio.adris.model.dtos.ProductoDivisibleDto;
import com.negocio.adris.model.dtos.ProductoDto;
import com.negocio.adris.model.exceptions.ProductoNotFoundException;
import com.negocio.adris.model.service.ProductoService;
import com.negocio.adris.viewmodel.ProductoViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoViewModelTest {
    @Mock
    private ProductoService productoService;

    private ProductoViewModel viewModel;

    @BeforeEach
    void setUp() throws ProductoNotFoundException {
        when(productoService.obtenerTodosProductos()).thenReturn(Collections.emptyList());

        viewModel = new ProductoViewModel(productoService);
    }

    // ----------------- CONSTRUCTOR
    @Test
    void constructor_debeCargarProductos() throws ProductoNotFoundException {
        verify(productoService).obtenerTodosProductos();
    }

    @Test
    void constructor_siServiceFalla_debePropagarException() throws ProductoNotFoundException {
        when(productoService.obtenerTodosProductos()).thenThrow(new ProductoNotFoundException("Error"));

        assertThrows(ProductoNotFoundException.class, () -> new ProductoViewModel(productoService));
    }

    // ----------------- BINDING PRECIO SUGERIDO
    @Test
    void precioSugerido_debeCalcularCorrectamente() {
        viewModel.costoProperty().set(new BigDecimal("100"));
        viewModel.gananciaProperty().set(new BigDecimal("25"));

        assertEquals(new BigDecimal("125.00"), viewModel.precioSugeridoProperty().get());
    }

    @Test
    void precioSugerido_siGananciaEsCero_debeSerCosto() {
        viewModel.costoProperty().set(new BigDecimal("100"));
        viewModel.gananciaProperty().set(BigDecimal.ZERO);

        assertEquals(new BigDecimal("100.00"), viewModel.precioSugeridoProperty().get());
    }

    @Test
    void precioSugerido_siValoresNull_debeSerCero() {
        viewModel.costoProperty().set(null);
        viewModel.gananciaProperty().set(null);

        assertEquals(BigDecimal.ZERO, viewModel.precioSugeridoProperty().get());
    }

    // ----------------- GUARDAR PRODUCTO
    @Test
    void guardarProducto_noDivisible_debeLlamarCrearProducto() throws ProductoNotFoundException {
        viewModel.nombreProperty().set("Arroz");
        viewModel.marcaProperty().set("MarcaX");
        viewModel.esDivisibleProperty().set(false);

        viewModel.guardarProducto();

        verify(productoService).crearProducto(any(ProductoDto.class));
        verify(productoService, never()).crearProductoDivisible(any());
    }

    @Test
    void guardarProducto_divisible_debeLlamarCrearProductoDivisible() throws ProductoNotFoundException {
        viewModel.nombreProperty().set("Harina");
        viewModel.marcaProperty().set("MarcaY");
        viewModel.esDivisibleProperty().set(true);

        viewModel.guardarProducto();

        verify(productoService).crearProductoDivisible(any(ProductoDivisibleDto.class));
        verify(productoService, never()).crearProducto(any());
    }

    @Test
    void guardarProducto_siServiceFalla_debePropagarExceptionYNoLimpiar() {
        viewModel.nombreProperty().set("Arroz");
        viewModel.esDivisibleProperty().set(false);

        doThrow(new RuntimeException("Error DB"))
                .when(productoService)
                .crearProducto(any());

        assertThrows(RuntimeException.class, () -> viewModel.guardarProducto());

        // Verifica que NO se limpió el formulario
        assertEquals("Arroz", viewModel.nombreProperty().get());
    }

    // ----------------- MODIFICAR PRODUCTO
    @Test
    void modificarProducto_noDivisible_debeLlamarModificar() throws ProductoNotFoundException {
        viewModel.idProperty().set(10L);
        viewModel.esDivisibleProperty().set(false);

        viewModel.modificarProducto();

        verify(productoService).modificarProducto(eq(10L), any(ProductoDto.class));
    }

    @Test
    void modificarProducto_divisible_debeLlamarModificarDivisible() throws ProductoNotFoundException {
        viewModel.idProperty().set(10L);
        viewModel.esDivisibleProperty().set(true);

        viewModel.modificarProducto();

        verify(productoService).modificarProductoDivisible(eq(10L), any(ProductoDivisibleDto.class));
    }

    @Test
    void modificarProducto_siServiceFalla_debePropagarException() throws ProductoNotFoundException {
        viewModel.idProperty().set(5L);
        viewModel.esDivisibleProperty().set(false);

        doThrow(new ProductoNotFoundException("No existe"))
                .when(productoService)
                .modificarProducto(eq(5L), any());

        assertThrows(ProductoNotFoundException.class, () -> viewModel.modificarProducto());
    }

    // ----------------- ELIMINAR PRODUCTO
    @Test
    void eliminarProducto_debeLlamarServiceYRecargar() throws ProductoNotFoundException {
        viewModel.eliminarProducto(1L);

        verify(productoService).eliminarProducto(1L);
        verify(productoService, atLeastOnce()).obtenerTodosProductos();
    }

    @Test
    void eliminarProducto_siNoExiste_debePropagarException() throws ProductoNotFoundException {
        doThrow(new ProductoNotFoundException("No existe"))
                .when(productoService)
                .eliminarProducto(99L);

        assertThrows(ProductoNotFoundException.class, () -> viewModel.eliminarProducto(99L));
    }

    // ----------------- LIMPIAR FORMULARIO
    @Test
    void limpiarFormulario_debeResetearTodosLosCampos() {
        viewModel.nombreProperty().set("Algo");
        viewModel.costoProperty().set(new BigDecimal("10"));
        viewModel.esDivisibleProperty().set(true);

        viewModel.limpiarFormulario();

        assertEquals("", viewModel.nombreProperty().get());
        assertEquals(BigDecimal.ZERO, viewModel.costoProperty().get());
        assertFalse(viewModel.esDivisibleProperty().get());
    }
}

