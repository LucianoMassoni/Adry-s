package com.negocio.adris.model.viewmodel;

import com.negocio.adris.model.dtos.PagoDto;
import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.entities.Pago;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.PagoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.model.service.PagoService;
import com.negocio.adris.viewmodel.PagoViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoViewModelTest {
    @Mock
    private PagoService pagoService;

    private PagoViewModel viewModel;

    @BeforeEach
    void setUp() {
        when(pagoService.getTotalPagoPorDia(any())).thenReturn(BigDecimal.ZERO);

        when(pagoService.getTotalPagoPorMes(any())).thenReturn(BigDecimal.ZERO);

        viewModel = new PagoViewModel(pagoService);
    }

    // CONSTRUCTOR
    @Test
    void constructor_debeActualizarMontos() {
        verify(pagoService).getTotalPagoPorDia(any());
        verify(pagoService).getTotalPagoPorMes(any());

        assertEquals(BigDecimal.ZERO, viewModel.montoPagadoDiarioProperty().get());
        assertEquals(BigDecimal.ZERO, viewModel.montoPagadoMensualProperty().get());
    }

    // CARGAR PAGOS
    @Test
    void cargarPagos_debeActualizarLista() {
        List<Pago> lista = List.of(crearPago(1L), crearPago(2L));
        when(pagoService.getPagos()).thenReturn(lista);

        viewModel.cargarPagos();

        assertEquals(2, viewModel.getPagos().size());
        verify(pagoService).getPagos();
    }

    // LIMPIAR FORMULARIO
    @Test
    void limpiarFormulario_debeResetearCampos() {
        viewModel.idProperty().set(10L);
        viewModel.gastoProperty().set(new Gasto());
        viewModel.fechaPagoProperty().set(LocalDateTime.now());
        viewModel.montoPagadoProperty().set(BigDecimal.TEN);

        viewModel.limpiarFormulario();

        assertEquals(0, viewModel.idProperty().get());
        assertNull(viewModel.gastoProperty().get());
        assertNull(viewModel.fechaPagoProperty().get());
        assertNull(viewModel.montoPagadoProperty().get());
    }

    // CARGAR PAGO
    @Test
    void cargarPago_debeSetearCampos() {
        Pago pago = crearPago(5L);

        viewModel.cargarPago(pago);

        assertEquals(5L, viewModel.idProperty().get());
        assertEquals(pago.getGasto(), viewModel.gastoProperty().get());
        assertEquals(pago.getFechaPago(), viewModel.fechaPagoProperty().get());
        assertEquals(pago.getMontoPagado(), viewModel.montoPagadoProperty().get());
    }

    // CREAR PAGO
    @Test
    void crearPago_debeLlamarServiceYActualizarMontoDiario() throws GastoNotFoundException, ProveedorNotFoundException {
        Gasto gasto = crearGasto(1L);

        viewModel.gastoProperty().set(gasto);
        viewModel.fechaPagoProperty().set(LocalDateTime.now());
        viewModel.montoPagadoProperty().set(BigDecimal.valueOf(500));

        viewModel.crearPago();

        verify(pagoService).guardar(any(PagoDto.class));
        verify(pagoService, times(2)).getTotalPagoPorDia(any());
        // una del constructor + una de crearPago()
    }

    // MODIFICAR PAGO
    @Test
    void modificarPago_debeLlamarServiceYActualizarMontoDiario() throws PagoNotFoundException, GastoNotFoundException, ProveedorNotFoundException {
        Gasto gasto = crearGasto(2L);

        viewModel.idProperty().set(10L);
        viewModel.gastoProperty().set(gasto);
        viewModel.fechaPagoProperty().set(LocalDateTime.now());
        viewModel.montoPagadoProperty().set(BigDecimal.valueOf(800));

        viewModel.modificarPago();

        verify(pagoService).modificar(eq(10L), any(PagoDto.class));
        verify(pagoService, times(2)).getTotalPagoPorDia(any());
    }

    // ELIMINAR PAGO
    @Test
    void eliminarPago_debeLlamarServiceYActualizarMontoDiario() throws PagoNotFoundException {
        viewModel.idProperty().set(20L);

        viewModel.eliminarPago();

        verify(pagoService).eliminar(20L);
        verify(pagoService, times(2)).getTotalPagoPorDia(any());
    }

    // HELPERS
    private Pago crearPago(Long id) {
        Pago pago = new Pago();
        pago.setId(id);
        pago.setGasto(crearGasto(1L));
        pago.setFechaPago(LocalDateTime.now());
        pago.setMontoPagado(BigDecimal.valueOf(1000));

        return pago;
    }

    private Gasto crearGasto(Long id) {
        Gasto gasto = new Gasto();
        gasto.setId(id);
        gasto.setMonto(BigDecimal.valueOf(1000));

        return gasto;
    }
}

