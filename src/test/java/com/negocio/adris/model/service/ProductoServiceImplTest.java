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
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {
    @Mock
    private ProductoRepository repo;

    @Mock
    private Validator validator;

    @InjectMocks
    private ProductoServiceImpl service;


    // ---------------- CREAR PRODUCTO
    @Test
    void crearProducto_valido_deberiaGuardar() {
        ProductoDto dto = mock(ProductoDto.class);

        when(validator.validate(dto)).thenReturn(Collections.emptySet());

        when(dto.getCosto()).thenReturn(new BigDecimal("100"));
        when(dto.getGanancia()).thenReturn(new BigDecimal("20"));
        when(dto.getPrecio()).thenReturn(new BigDecimal("150"));

        when(dto.getNombre()).thenReturn("arroz");
        when(dto.getMarca()).thenReturn("marca");
        when(dto.getPeso()).thenReturn(1.0);
        when(dto.getUnidadMedida()).thenReturn(null);
        when(dto.getCantidad()).thenReturn(10);
        when(dto.getTipo()).thenReturn(null);
        when(dto.esDivisible()).thenReturn(false);

        service.crearProducto(dto);

        verify(repo).save(any(Producto.class));
    }

    @Test
    void crearProducto_dtoInvalido_deberiaLanzarException() {
        ProductoDto dto = mock(ProductoDto.class);

        ConstraintViolation<ProductoDto> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Error validacion");

        when(validator.validate(dto)).thenReturn(Set.of(violation));

        assertThrows(IllegalArgumentException.class, () -> service.crearProducto(dto));

        verify(repo, never()).save(any());
    }

    @Test
    void crearProducto_unidadNoValida_deberiaLanzarException() {
        ProductoDto dto = mock(ProductoDto.class);

        when(validator.validate(dto)).thenReturn(Collections.emptySet());

        TipoProducto tipo = mock(TipoProducto.class);
        UnidadMedida unidad = mock(UnidadMedida.class);

        when(dto.getTipo()).thenReturn(tipo);
        when(dto.getUnidadMedida()).thenReturn(unidad);
        when(tipo.admiteUnidad(unidad)).thenReturn(false);
        when(unidad.getSimbolo()).thenReturn("kg");

        assertThrows(IllegalArgumentException.class, () -> service.crearProducto(dto));

        verify(repo, never()).save(any());
    }

    @Test
    void crearProducto_precioMenorAlPermitido_deberiaLanzarException() {
        ProductoDto dto = mock(ProductoDto.class);

        when(validator.validate(dto)).thenReturn(Collections.emptySet());

        when(dto.getCosto()).thenReturn(new BigDecimal("100"));
        when(dto.getGanancia()).thenReturn(new BigDecimal("20"));
        when(dto.getPrecio()).thenReturn(new BigDecimal("60"));

        when(dto.getTipo()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> service.crearProducto(dto));

        verify(repo, never()).save(any());
    }

    // ---------------- MODIFICAR PRODUCTO
    @Test
    void modificarProducto_valido_deberiaActualizar() throws ProductoNotFoundException {
        ProductoDto dto = mock(ProductoDto.class);

        when(validator.validate(dto)).thenReturn(Collections.emptySet());

        when(dto.getCosto()).thenReturn(new BigDecimal("100"));
        when(dto.getGanancia()).thenReturn(new BigDecimal("10"));
        when(dto.getPrecio()).thenReturn(new BigDecimal("120"));

        when(dto.getNombre()).thenReturn("arroz");
        when(dto.getMarca()).thenReturn("marca");
        when(dto.getPeso()).thenReturn(1.0);
        when(dto.getPesoActual()).thenReturn(1.0);
        when(dto.getCantidad()).thenReturn(10);
        when(dto.getTipo()).thenReturn(null);
        when(dto.esDivisible()).thenReturn(false);

        Producto producto = mock(Producto.class);
        when(repo.findById(1L)).thenReturn(producto);

        service.modificarProducto(1L, dto);

        verify(producto).setNombre(any());
        verify(producto).setMarca(any());
        verify(repo).update(producto);
    }

    @Test
    void modificarProducto_noExiste_deberiaLanzarException() throws ProductoNotFoundException {
        ProductoDto dto = mock(ProductoDto.class);

        when(validator.validate(dto)).thenReturn(Collections.emptySet());
        when(dto.getCosto()).thenReturn(new BigDecimal("100"));
        when(dto.getGanancia()).thenReturn(new BigDecimal("10"));
        when(dto.getPrecio()).thenReturn(new BigDecimal("120"));
        when(dto.getTipo()).thenReturn(null);

        when(repo.findById(1L)).thenThrow(new ProductoNotFoundException("No existe"));

        assertThrows(ProductoNotFoundException.class, () -> service.modificarProducto(1L, dto));

        verify(repo, never()).update(any());
    }

    // ---------------- ELIMINAR
    @Test
    void eliminarProducto_valido_deberiaEliminar() throws ProductoNotFoundException {
        Producto producto = mock(Producto.class);

        when(repo.findById(1L)).thenReturn(producto);

        service.eliminarProducto(1L);

        verify(repo).delete(1L);
    }

    @Test
    void eliminarProducto_noExiste_deberiaLanzarException() throws ProductoNotFoundException {
        when(repo.findById(1L)).thenThrow(new ProductoNotFoundException("No existe"));

        assertThrows(ProductoNotFoundException.class, () -> service.eliminarProducto(1L));

        verify(repo, never()).delete(anyLong());
    }

    // ---------------- OBTENER
    @Test
    void obtenerProductoPorId_valido() throws ProductoNotFoundException {
        Producto producto = mock(Producto.class);

        when(repo.findById(1L)).thenReturn(producto);

        Producto resultado = service.obtenerProductoPorId(1L);

        assertEquals(producto, resultado);
    }

    @Test
    void obtenerProductoPorId_noExiste_deberiaLanzarException() throws ProductoNotFoundException {
        when(repo.findById(1L)).thenThrow(new ProductoNotFoundException("No existe"));

        assertThrows(ProductoNotFoundException.class, () -> service.obtenerProductoPorId(1L));
    }

    // ---------------- COMPRAR PRODUCTO

    @Test
    void comprarProducto_valido_deberiaRestarCantidad() {
        Producto producto = mock(Producto.class);

        when(producto.getCantidad()).thenReturn(10);

        service.comprarProducto(producto, new BigDecimal("3"));

        verify(producto).setCantidad(7);
        verify(repo).update(producto);
    }

    @Test
    void comprarProducto_cantidadNegativa_deberiaLanzarException() {
        Producto producto = mock(Producto.class);

        when(producto.getCantidad()).thenReturn(-1);
        when(producto.getNombre()).thenReturn("Arroz");

        assertThrows(IllegalArgumentException.class, () -> service.comprarProducto(producto, new BigDecimal("2")));

        verify(repo, never()).update(any());
    }
}

