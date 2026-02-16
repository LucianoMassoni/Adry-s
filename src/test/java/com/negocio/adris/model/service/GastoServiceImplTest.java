package com.negocio.adris.model.service;

import com.negocio.adris.model.dtos.GastoDto;
import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.entities.Pago;
import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.model.repositories.GastoRepository;
import com.negocio.adris.utils.onCreate;
import com.negocio.adris.utils.onUpdate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GastoServiceImplTest {

    @Mock
    private GastoRepository repo;

    @Mock
    private Validator validator;

    @Mock
    private ProveedorService proveedorService;

    @InjectMocks
    private GastoServiceImpl service;


    // ------------ CREAR ------------
    @Test
    void crear_gastoValido_deberiaGuardarCorrectamente() throws ProveedorNotFoundException {
        GastoDto dto = new GastoDto();
        dto.setProveedorId(1L);
        dto.setFechaVencimiento(LocalDateTime.now().plusDays(10));
        dto.setMonto(BigDecimal.valueOf(1000));
        dto.setNota("Compra fiambre");

        when(validator.validate(dto, onCreate.class)).thenReturn(Collections.emptySet());

        Proveedor proveedor = new Proveedor(1L, "Proveedor 1", "123");

        when(proveedorService.getProveedor(1L)).thenReturn(proveedor);

        ArgumentCaptor<Gasto> captor = ArgumentCaptor.forClass(Gasto.class);

        service.crear(dto);

        verify(repo).save(captor.capture());

        Gasto guardado = captor.getValue();

        assertEquals(proveedor, guardado.getProveedor());
        assertEquals(dto.getMonto(), guardado.getMonto());
        assertEquals(dto.getNota(), guardado.getNota());
        assertEquals(dto.getFechaVencimiento(), guardado.getFechaVencimiento());
        assertFalse(guardado.isSaldado());
        assertNotNull(guardado.getFechaDeudaContraida());
    }

    @Test
    void crear_conErroresDeValidacion_deberiaLanzarException() {
        GastoDto dto = new GastoDto();

        ConstraintViolation<GastoDto> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Error");

        when(validator.validate(dto, onCreate.class)).thenReturn(Set.of(violation));

        assertThrows(IllegalArgumentException.class, () -> service.crear(dto));

        verify(repo, never()).save(any());
    }

    @Test
    void crear_proveedorInexistente_deberiaLanzarException() throws ProveedorNotFoundException {
        GastoDto dto = new GastoDto();
        dto.setProveedorId(99L);
        dto.setFechaVencimiento(LocalDateTime.now().plusDays(5));

        when(validator.validate(dto, onCreate.class)).thenReturn(Collections.emptySet());
        when(proveedorService.getProveedor(99L)).thenThrow(new ProveedorNotFoundException("No existe"));

        assertThrows(ProveedorNotFoundException.class, () -> service.crear(dto));

        verify(repo, never()).save(any());
    }

    // ------------ MODIFICAR ------------
    @Test
    void modificar_gastoValido_deberiaActualizar() throws ProveedorNotFoundException, GastoNotFoundException {
        GastoDto dto = new GastoDto();
        dto.setProveedorId(1L);
        dto.setFechaVencimiento(LocalDateTime.now().plusDays(20));
        dto.setMonto(BigDecimal.valueOf(2000));
        dto.setNota("Actualizado");

        Gasto existente = new Gasto(
                1L,
                new Proveedor(1L, "Old", "111"),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5),
                BigDecimal.valueOf(500),
                "Viejo",
                false
        );

        when(validator.validate(dto, onUpdate.class)).thenReturn(Collections.emptySet());

        when(repo.findById(1L)).thenReturn(existente);

        Proveedor proveedorNuevo = new Proveedor(1L, "Nuevo", "222");

        when(proveedorService.getProveedor(1L)).thenReturn(proveedorNuevo);

        service.modificar(1L, dto);

        verify(repo).update(existente);

        assertEquals(proveedorNuevo, existente.getProveedor());
        assertEquals(dto.getMonto(), existente.getMonto());
        assertEquals(dto.getNota(), existente.getNota());
        assertEquals(dto.getFechaVencimiento(), existente.getFechaVencimiento());
    }

    @Test
    void modificar_conErroresDeValidacion_deberiaLanzarException() {
        GastoDto dto = new GastoDto();

        ConstraintViolation<GastoDto> violation = mock(ConstraintViolation.class);

        when(validator.validate(dto, onUpdate.class)).thenReturn(Set.of(violation));

        assertThrows(IllegalArgumentException.class, () -> service.modificar(1L, dto));

        verify(repo, never()).update(any());
    }

    // ------------ ELIMINAR ------------
    @Test
    void eliminar_deberiaLlamarAlRepo() throws GastoNotFoundException {
        Gasto gasto = mock(Gasto.class);
        when(repo.findById(1L)).thenReturn(gasto);

        service.eliminar(1L);

        verify(repo).delete(1L);
    }

    @Test
    void eliminar_GastoInexistente_deberiaTirarError() throws GastoNotFoundException {
        when(repo.findById(anyLong())).thenThrow(GastoNotFoundException.class);

        assertThrows(GastoNotFoundException.class, () -> service.eliminar(anyLong()));

        verify(repo, never()).delete(anyLong());
    }

    // ------------ GET ------------
    @Test
    void getGastoById_deberiaRetornarGasto() throws GastoNotFoundException {
        Gasto gasto = mock(Gasto.class);
        when(repo.findById(1L)).thenReturn(gasto);

        Gasto resultado = service.getGastoById(1L);

        assertEquals(gasto, resultado);
    }

    @Test
    void getGastos_deberiaRetornarLista() {
        List<Gasto> lista = List.of(mock(Gasto.class));
        when(repo.findAll()).thenReturn(lista);

        List<Gasto> resultado = service.getGastos();

        assertEquals(lista, resultado);
    }

    // ------------ AGREGAR PAGO ------------
    @Test
    void agregarPago_deberiaAgregarYActualizar() {
        Gasto gasto = mock(Gasto.class);
        List<Pago> pagos = new ArrayList<>();
        when(gasto.getPagos()).thenReturn(pagos);

        Pago pago = mock(Pago.class);

        service.agregarPago(gasto, pago);

        assertTrue(pagos.contains(pago));
        verify(repo).update(gasto);
    }

    // ------------ SALDAR ------------
    @Test
    void saldarDeuda_deberiaMarcarComoSaldadoYActualizar() {
        Gasto gasto = mock(Gasto.class);

        service.saldarDeuda(gasto);

        verify(gasto).setSaldado(true);
        verify(repo).update(gasto);
    }
}
