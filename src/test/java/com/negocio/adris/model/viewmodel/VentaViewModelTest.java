package com.negocio.adris.model.viewmodel;

import com.negocio.adris.model.dtos.VentaDto;
import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.entities.Venta;
import com.negocio.adris.model.enums.FormaDePago;
import com.negocio.adris.model.exceptions.VentaNotFoundException;
import com.negocio.adris.model.service.VentaService;
import com.negocio.adris.viewmodel.DetalleVentaItem;
import com.negocio.adris.viewmodel.VentaViewModel;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VentaViewModelTest {
    @Mock
    private VentaService ventaService;

    private VentaViewModel viewModel;

    @BeforeEach
    void setUp() throws VentaNotFoundException {
        when(ventaService.obtenerGananciaPorDia(any())).thenReturn(BigDecimal.ZERO);
        when(ventaService.obtenerGananciaPorMes(any())).thenReturn(BigDecimal.ZERO);

        viewModel = new VentaViewModel(ventaService);
        viewModel.formaDePagoProperty().set(FormaDePago.TARJETA);
    }

    // TOTAL SIN DESCUENTO
    @Test
    void recalcularTotal_sumaCorrectamente() {
        viewModel.agregarItem(crearItem(new BigDecimal("1000")));
        viewModel.agregarItem(crearItem(new BigDecimal("500")));

        viewModel.recalcularTotal();

        assertEquals(new BigDecimal("1500.00"), viewModel.totalProperty().get());
    }

    // DESCUENTO POR EFECTIVO
    @Test
    void recalcularTotal_aplicaDescuentoEfectivoSiMayorA20000() {
        viewModel.formaDePagoProperty().set(FormaDePago.EFECTIVO);

        viewModel.agregarItem(crearItem(new BigDecimal("25000")));

        viewModel.recalcularTotal();

        assertEquals(new BigDecimal("22500.00"), viewModel.totalProperty().get());
    }

    // GUARDAR VENTA
    @Test
    void guardarVenta_llamaAlService_yLimpiaEstado() {
        viewModel.agregarItem(crearItem(new BigDecimal("1000")));

        viewModel.guardarVenta();

        verify(ventaService).crearVenta(any(VentaDto.class));

        assertEquals(0, viewModel.idProperty().get());
        assertTrue(viewModel.getDetalleVentas().isEmpty());
    }

    // AGREGAR CANTIDAD
    @Test
    void agregarCantidad_incrementaSiHayStock() {
        Producto producto = crearProducto(new BigDecimal("100"), 10);

        DetalleVentaItem item = new DetalleVentaItem();
        item.productoProperty().set(producto);
        item.cantidadProperty().set(BigDecimal.ONE);

        viewModel.agregarCantidad(item);

        assertEquals(new BigDecimal("2"), item.cantidadProperty().get());
    }

    // SACAR CANTIDAD
    @Test
    void sacarCantidad_decrementaSiMayorAUno() {
        DetalleVentaItem item = new DetalleVentaItem();
        item.cantidadProperty().set(new BigDecimal("3"));

        viewModel.sacarCantidad(item);

        assertEquals(new BigDecimal("2"), item.cantidadProperty().get());
    }

    // CANCELAR
    @Test
    void cancelar_limpiaItemsYReseteaId() {
        viewModel.idProperty().set(10);
        viewModel.agregarItem(crearItem(new BigDecimal("100")));

        viewModel.cancelar();

        assertEquals(0, viewModel.idProperty().get());
        assertTrue(viewModel.getDetalleVentas().isEmpty());
    }

    // GANANCIAS OK
    @Test
    void constructor_actualizaGananciasCorrectamente() throws VentaNotFoundException {
        when(ventaService.obtenerGananciaPorDia(any())).thenReturn(new BigDecimal("1000"));
        when(ventaService.obtenerGananciaPorMes(any())).thenReturn(new BigDecimal("5000"));

        viewModel = new VentaViewModel(ventaService);

        assertEquals(new BigDecimal("1000"), viewModel.gananciaDiariaProperty().get());
        assertEquals(new BigDecimal("5000"), viewModel.gananciaMensualProperty().get());
    }

    // GANANCIAS CON EXCEPCIÓN
    @Test
    void ganancias_siServiceLanzaException_seteaCero() throws VentaNotFoundException {
        when(ventaService.obtenerGananciaPorDia(any())).thenThrow(new VentaNotFoundException("No hay ventas"));
        when(ventaService.obtenerGananciaPorMes(any())).thenThrow(new VentaNotFoundException("No hay ventas"));

        viewModel = new VentaViewModel(ventaService);

        assertEquals(BigDecimal.ZERO, viewModel.gananciaDiariaProperty().get());
        assertEquals(BigDecimal.ZERO, viewModel.gananciaMensualProperty().get());
    }

    // GET VENTAS
    @Test
    void getVentas_retornaListaDelService() throws VentaNotFoundException {
        List<Venta> ventas = List.of(new Venta(), new Venta());

        when(ventaService.obtenerTodasLasVentas()).thenReturn(ventas);

        ObservableList<Venta> resultado = viewModel.getVentas();

        assertEquals(2, resultado.size());
        verify(ventaService).obtenerTodasLasVentas();
    }

    // HELPERS
    private DetalleVentaItem crearItem(BigDecimal subtotal) {
        Producto producto = crearProducto(subtotal, 100);

        DetalleVentaItem item = new DetalleVentaItem();
        item.productoProperty().set(producto);
        item.cantidadProperty().set(BigDecimal.ONE);
        item.descuentoProperty().set(BigDecimal.ZERO);
        item.precioProperty().set(subtotal);

        // Forzamos subtotal manualmente
        item.subtotalProperty().set(subtotal);

        return item;
    }

    private Producto crearProducto(BigDecimal precio, int stock) {
        Producto producto = new Producto();
        producto.setPrecio(precio);
        producto.setCantidad(stock);
        producto.setEsDivisible(false);

        return producto;
    }
}

