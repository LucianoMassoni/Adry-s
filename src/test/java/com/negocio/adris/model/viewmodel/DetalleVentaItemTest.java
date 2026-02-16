package com.negocio.adris.model.viewmodel;

import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.viewmodel.DetalleVentaItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DetalleVentaItemTest {
    private DetalleVentaItem item;

    @Mock
    private Producto producto;

    @BeforeEach
    void setUp() {
        item = new DetalleVentaItem();
    }

    // -------------- SUBTOTAL - PRODUCTO NULL
    @Test
    void subtotal_siProductoEsNull_debeSerCero() {
        item.productoProperty().set(null);
        assertEquals(BigDecimal.ZERO, item.subtotalProperty().get());
    }

    // -------------- SUBTOTAL - PRODUCTO NO DIVISIBLE
    @Test
    void subtotal_noDivisible_sinDescuento() {
        when(producto.esDivisible()).thenReturn(false);
        when(producto.getPrecio()).thenReturn(new BigDecimal("100"));

        item.productoProperty().set(producto);
        item.cantidadProperty().set(new BigDecimal("2"));
        item.descuentoProperty().set(BigDecimal.ZERO);

        assertEquals(new BigDecimal("200.00"), item.subtotalProperty().get());
    }

    @Test
    void subtotal_noDivisible_conDescuento() {
        when(producto.esDivisible()).thenReturn(false);
        when(producto.getPrecio()).thenReturn(new BigDecimal("100"));

        item.productoProperty().set(producto);
        item.cantidadProperty().set(new BigDecimal("2"));
        item.descuentoProperty().set(new BigDecimal("10"));

        // 100 * 2 * 0.9 = 180
        assertEquals(new BigDecimal("180.00"), item.subtotalProperty().get());
    }

    @Test
    void subtotal_noDivisible_siCantidadNull_debeSerCero() {
        when(producto.esDivisible()).thenReturn(false);
        when(producto.getPrecio()).thenReturn(new BigDecimal("100"));

        item.productoProperty().set(producto);
        item.cantidadProperty().set(null);

        assertEquals(BigDecimal.ZERO, item.subtotalProperty().get());
    }

    // -------------- SUBTOTAL - PRODUCTO DIVISIBLE
    @Test
    void subtotal_divisible_sinDescuento() {
        when(producto.esDivisible()).thenReturn(true);

        item.productoProperty().set(producto);
        item.precioProperty().set(new BigDecimal("50"));
        item.descuentoProperty().set(BigDecimal.ZERO);

        assertEquals(new BigDecimal("50.00"), item.subtotalProperty().get());
    }

    @Test
    void subtotal_divisible_conDescuento() {
        when(producto.esDivisible()).thenReturn(true);

        item.productoProperty().set(producto);
        item.precioProperty().set(new BigDecimal("100"));
        item.descuentoProperty().set(new BigDecimal("20"));

        // 100 * 0.8 = 80
        assertEquals(new BigDecimal("80.00"), item.subtotalProperty().get());
    }

    @Test
    void subtotal_divisible_siPrecioNull_debeSerCero() {
        when(producto.esDivisible()).thenReturn(true);

        item.productoProperty().set(producto);
        item.precioProperty().set(null);

        assertEquals(BigDecimal.ZERO, item.subtotalProperty().get());
    }

    // -------------- GET ITEM ACTUAL
    @Test
    void getItemActual_siProductoNull_debeLanzarException() {
        item.productoProperty().set(null);

        assertThrows(IllegalArgumentException.class, () -> item.getItemActual());
    }

    @Test
    void getItemActual_noDivisible_sinStock_debeLanzarException() {
        when(producto.esDivisible()).thenReturn(false);
        when(producto.getCantidad()).thenReturn(1);
        when(producto.getNombre()).thenReturn("Arroz");
        when(producto.getPrecio()).thenReturn(BigDecimal.TEN);

        item.productoProperty().set(producto);
        item.cantidadProperty().set(new BigDecimal("5"));

        assertThrows(IllegalArgumentException.class, () -> item.getItemActual());
    }

    @Test
    void getItemActual_debeRetornarCopiaCorrecta() {
        when(producto.esDivisible()).thenReturn(false);
        when(producto.getCantidad()).thenReturn(10);
        when(producto.getPrecio()).thenReturn(BigDecimal.TEN);

        item.productoProperty().set(producto);
        item.cantidadProperty().set(new BigDecimal("2"));
        item.descuentoProperty().set(new BigDecimal("5"));
        item.precioProperty().set(new BigDecimal("100"));

        DetalleVentaItem copia = item.getItemActual();

        assertNotSame(item, copia);
        assertEquals(producto, copia.productoProperty().get());
        assertEquals(new BigDecimal("2"), copia.cantidadProperty().get());
        assertEquals(new BigDecimal("5"), copia.descuentoProperty().get());
        assertEquals(new BigDecimal("100"), copia.precioProperty().get());
    }

    // -------------- LIMPIAR FORMULARIO
    @Test
    void limpiarFormulario_debeResetearCampos() {
        // le doy un valor para que no tire un NullPointerException. de todos modos nunca seria null, ya testeado y validado por Jakarta.
        when(producto.getPrecio()).thenReturn(BigDecimal.TEN);

        item.productoProperty().set(producto);
        item.cantidadProperty().set(new BigDecimal("5"));
        item.descuentoProperty().set(new BigDecimal("10"));
        item.precioProperty().set(new BigDecimal("100"));

        item.limpiarFormulario();

        assertNull(item.productoProperty().get());
        assertEquals(BigDecimal.ONE, item.cantidadProperty().get());
        assertEquals(BigDecimal.ZERO, item.descuentoProperty().get());
        assertEquals(BigDecimal.ZERO, item.precioProperty().get());
        assertEquals(BigDecimal.ZERO, item.subtotalProperty().get());
    }
}

