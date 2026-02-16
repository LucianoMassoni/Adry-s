package com.negocio.adris.model.viewmodel;

import com.negocio.adris.model.dtos.ProveedorDto;
import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.service.ProveedorService;
import com.negocio.adris.viewmodel.ProveedorViewModel;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProveedorViewModelTest {
    @Mock
    private ProveedorService proveedorService;

    private ProveedorViewModel viewModel;

    @BeforeEach
    void setUp() {
        when(proveedorService.getProveedores()).thenReturn(List.of());
        viewModel = new ProveedorViewModel(proveedorService);
    }

    // CONSTRUCTOR
    @Test
    void constructor_debeCargarProveedores() {
        verify(proveedorService).getProveedores();
        assertNotNull(viewModel.proveedoresProperty());
    }

    // CARGAR PROVEEDORES
    @Test
    void cargarProveedores_debeActualizarLista() {
        List<Proveedor> lista = List.of(
                crearProveedor(1L, "Proveedor 1", "123"),
                crearProveedor(2L, "Proveedor 2", "456")
        );

        when(proveedorService.getProveedores()).thenReturn(lista);

        viewModel.cargarProveedores();

        assertEquals(2, viewModel.proveedoresProperty().size());
        verify(proveedorService, times(2)).getProveedores();
        // constructor + este test
    }

    // GET PROVEEDOR
    @Test
    void getProveedor_debeDelegarAlService() throws Exception {
        Proveedor proveedor = crearProveedor(1L, "Test", "999");
        when(proveedorService.getProveedor(1L)).thenReturn(proveedor);

        Proveedor resultado = viewModel.getProveedor(1L);

        assertEquals("Test", resultado.getNombre());
        verify(proveedorService).getProveedor(1L);
    }

    // LIMPIAR FORMULARIO
    @Test
    void limpiarFormulario_debeResetearCampos() {
        viewModel.idProperty().set(10L);
        viewModel.nombreProperty().set("Proveedor");
        viewModel.telefonoProperty().set("123");

        viewModel.limpiarFormulario();

        assertEquals(0, viewModel.idProperty().get());
        assertNull(viewModel.nombreProperty().get());
        assertNull(viewModel.telefonoProperty().get());
    }

    // CARGAR PROVEEDOR EN FORMULARIO
    @Test
    void cargarProveedor_debeSetearCampos() {
        Proveedor proveedor = crearProveedor(5L, "ABC", "111");

        viewModel.cargarProveedor(proveedor);

        assertEquals(5L, viewModel.idProperty().get());
        assertEquals("ABC", viewModel.nombreProperty().get());
        assertEquals("111", viewModel.telefonoProperty().get());
    }

    // CREAR PROVEEDOR
    @Test
    void crearProveedor_debeLlamarServiceYRecargarLista() {
        viewModel.nombreProperty().set("Nuevo");
        viewModel.telefonoProperty().set("555");

        viewModel.crearProveedor();

        verify(proveedorService).guardar(any(ProveedorDto.class));
        verify(proveedorService, times(2)).getProveedores();
        // constructor + crearProveedor()
    }

    // MODIFICAR PROVEEDOR
    @Test
    void modificarProveedor_debeLlamarService() throws Exception {
        viewModel.idProperty().set(10L);
        viewModel.nombreProperty().set("Modificado");
        viewModel.telefonoProperty().set("888");

        viewModel.modificarProveedor();

        verify(proveedorService).modificar(eq(10L), any(ProveedorDto.class));
    }

    // ELIMINAR PROVEEDOR
    @Test
    void eliminarProveedor_debeLlamarService() throws Exception {
        viewModel.idProperty().set(20L);

        viewModel.eliminarProveedor();

        verify(proveedorService).eliminar(20L);
    }

    // GET PROVEEDORES
    @Test
    void getProveedores_debeRecargarLista() {
        List<Proveedor> lista = List.of(
                crearProveedor(1L, "Uno", "111")
        );

        when(proveedorService.getProveedores()).thenReturn(lista);

        ObservableList<Proveedor> resultado = viewModel.getProveedores();

        assertEquals(1, resultado.size());
        verify(proveedorService, times(2)).getProveedores();
        // constructor + getProveedores()
    }

    // HELPERS
    private Proveedor crearProveedor(Long id, String nombre, String telefono) {
        Proveedor proveedor = new Proveedor();
        proveedor.setId(id);
        proveedor.setNombre(nombre);
        proveedor.setTelefono(telefono);
        return proveedor;
    }
}

