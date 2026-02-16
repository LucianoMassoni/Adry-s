package com.negocio.adris.model.viewmodel;

import com.negocio.adris.model.dtos.GastoDto;
import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.model.service.GastoService;
import com.negocio.adris.viewmodel.GastoViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GastoViewModelTest {
    @Mock
    private GastoService gastoService;

    private GastoViewModel viewModel;

    @BeforeEach
    void setUp() {
        when(gastoService.getGastos()).thenReturn(List.of());
        viewModel = new GastoViewModel(gastoService);
    }

    // CARGA INICIAL
    @Test
    void constructor_debeCargarGastos() {
        verify(gastoService).getGastos();
        assertNotNull(viewModel.getGastos());
    }

    // CARGAR GASTOS
    @Test
    void cargarGastos_debeActualizarLista() {
        List<Gasto> lista = List.of(crearGasto(1L), crearGasto(2L));
        when(gastoService.getGastos()).thenReturn(lista);

        viewModel.cargarGastos();

        assertEquals(2, viewModel.getGastos().size());
        verify(gastoService, times(2)).getGastos();
    }

    // GET GASTO
    @Test
    void getGasto_debeRetornarDesdeService() throws GastoNotFoundException, ProveedorNotFoundException {
        Gasto gasto = crearGasto(1L);
        when(gastoService.getGastoById(1L)).thenReturn(gasto);

        Gasto resultado = viewModel.getGasto(1L);

        assertEquals(1L, resultado.getId());
        verify(gastoService).getGastoById(1L);
    }

    // LIMPIAR FORMULARIO
    @Test
    void limpiarFormulario_debeResetearCampos() {
        viewModel.idProperty().set(10);
        viewModel.proveedorProperty().set(new Proveedor());
        viewModel.fechaVencimientoProperty().set(LocalDateTime.now());
        viewModel.montoProperty().set(BigDecimal.TEN);
        viewModel.notaProperty().set("nota");
        viewModel.saldadoProperty().set(true);

        viewModel.limpiarFormulario();

        assertEquals(0, viewModel.idProperty().get());
        assertNull(viewModel.proveedorProperty().get());
        assertNull(viewModel.fechaVencimientoProperty().get());
        assertNull(viewModel.montoProperty().get());
        assertNull(viewModel.notaProperty().get());
        assertFalse(viewModel.saldadoProperty().get());
    }

    // CARGAR GASTO EN FORMULARIO
    @Test
    void cargarGasto_debeSetearCampos() {
        Gasto gasto = crearGasto(5L);

        viewModel.cargarGasto(gasto);

        assertEquals(5L, viewModel.idProperty().get());
        assertEquals(gasto.getProveedor(), viewModel.proveedorProperty().get());
        assertEquals(gasto.getMonto(), viewModel.montoProperty().get());
        assertEquals(gasto.getNota(), viewModel.notaProperty().get());
        assertEquals(gasto.isSaldado(), viewModel.saldadoProperty().get());
    }

    // GUARDAR GASTO
    @Test
    void guardarGasto_debeLlamarServiceYCargarGastos() throws ProveedorNotFoundException {
        Proveedor proveedor = crearProveedor(1L);

        viewModel.proveedorProperty().set(proveedor);
        viewModel.fechaVencimientoProperty().set(LocalDateTime.now());
        viewModel.montoProperty().set(BigDecimal.valueOf(1000));
        viewModel.notaProperty().set("Compra insumos");

        viewModel.guardarGasto();

        verify(gastoService).crear(any(GastoDto.class));
        verify(gastoService, times(2)).getGastos();
    }

    // MODIFICAR GASTO
    @Test
    void modificarGasto_debeLlamarServiceYCargarGastos() throws GastoNotFoundException, ProveedorNotFoundException {
        Proveedor proveedor = crearProveedor(2L);

        viewModel.idProperty().set(10L);
        viewModel.proveedorProperty().set(proveedor);
        viewModel.fechaVencimientoProperty().set(LocalDateTime.now());
        viewModel.montoProperty().set(BigDecimal.valueOf(500));
        viewModel.notaProperty().set("Actualizado");

        viewModel.modificarGasto();

        verify(gastoService).modificar(eq(10L), any(GastoDto.class));
        verify(gastoService, times(2)).getGastos();
    }

    // ELIMINAR GASTO
    @Test
    void eliminarGasto_debeLlamarService() throws GastoNotFoundException {
        viewModel.idProperty().set(20L);

        viewModel.eliminarGasto();

        verify(gastoService).eliminar(20L);
    }

    // HELPERS
    private Gasto crearGasto(Long id) {
        Gasto gasto = new Gasto();
        gasto.setId(id);
        gasto.setProveedor(crearProveedor(1L));
        gasto.setFechaVencimiento(LocalDateTime.now());
        gasto.setMonto(BigDecimal.valueOf(1000));
        gasto.setNota("Nota test");
        gasto.setSaldado(false);

        return gasto;
    }

    private Proveedor crearProveedor(Long id) {
        Proveedor proveedor = new Proveedor();
        proveedor.setId(id);
        proveedor.setNombre("Proveedor Test");

        return proveedor;
    }
}

