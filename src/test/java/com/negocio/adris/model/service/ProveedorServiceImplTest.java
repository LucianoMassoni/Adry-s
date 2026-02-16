package com.negocio.adris.model.service;

import com.negocio.adris.model.dtos.ProveedorDto;
import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.model.repositories.ProveedorRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProveedorServiceImplTest {
    @Mock
    private ProveedorRepository repo;

    @Mock
    private Validator validator;

    @InjectMocks
    private ProveedorServiceImpl service;


    // ---------------- GUARDAR
    @Test
    void guardar_proveedorValido_deberiaGuardar() {
        ProveedorDto dto = mock(ProveedorDto.class);

        when(validator.validate(dto)).thenReturn(Collections.emptySet());
        when(dto.getNombre()).thenReturn("Proveedor Test");
        when(dto.getTelefono()).thenReturn("123456");

        service.guardar(dto);

        verify(repo).save(any(Proveedor.class));
    }

    @Test
    void guardar_dtoInvalido_deberiaLanzarException() {
        ProveedorDto dto = mock(ProveedorDto.class);

        ConstraintViolation<ProveedorDto> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("El proveedor necesita un nombre");

        when(validator.validate(dto)).thenReturn(Set.of(violation));

        assertThrows(IllegalArgumentException.class, () -> service.guardar(dto));

        verify(repo, never()).save(any());
    }

    // ---------------- MODIFICAR
    @Test
    void modificar_proveedorExistente_deberiaActualizar() throws ProveedorNotFoundException {
        ProveedorDto dto = mock(ProveedorDto.class);

        when(validator.validate(dto)).thenReturn(Collections.emptySet());
        when(dto.getNombre()).thenReturn("Nuevo Nombre");
        when(dto.getTelefono()).thenReturn("999");

        Proveedor proveedorExistente = mock(Proveedor.class);

        when(repo.findById(1L)).thenReturn(proveedorExistente);

        service.modificar(1L, dto);

        verify(proveedorExistente).setNombre("Nuevo Nombre");
        verify(proveedorExistente).setTelefono("999");
        verify(repo).update(proveedorExistente);
    }

    @Test
    void modificar_dtoInvalido_noDebeActualizar() throws ProveedorNotFoundException {
        ProveedorDto dto = mock(ProveedorDto.class);

        ConstraintViolation<ProveedorDto> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Error");

        when(validator.validate(dto)).thenReturn(Set.of(violation));

        assertThrows(IllegalArgumentException.class, () -> service.modificar(1L, dto));

        verify(repo, never()).findById(anyLong());
        verify(repo, never()).update(any());
    }

    @Test
    void modificar_proveedorNoExiste_deberiaLanzarProveedorNotFoundException() throws ProveedorNotFoundException {
        ProveedorDto dto = mock(ProveedorDto.class);

        when(validator.validate(dto)).thenReturn(Collections.emptySet());
        when(repo.findById(1L)).thenThrow(new ProveedorNotFoundException("No existe"));

        assertThrows(ProveedorNotFoundException.class, () -> service.modificar(1L, dto));

        verify(repo, never()).update(any());
    }

    @Test
    void modificar_validacionFalla_noDebeBuscarEnRepo() throws ProveedorNotFoundException {
        ProveedorDto dto = mock(ProveedorDto.class);

        ConstraintViolation<ProveedorDto> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Nombre obligatorio");

        when(validator.validate(dto)).thenReturn(Set.of(violation));

        assertThrows(IllegalArgumentException.class, () -> service.modificar(1L, dto));

        verify(repo, never()).findById(anyLong());
    }

    // ---------------- ELIMINAR
    @Test
    void eliminar_proveedorExistente_deberiaEliminar() throws ProveedorNotFoundException {
        Proveedor proveedor = mock(Proveedor.class);

        when(repo.findById(1L)).thenReturn(proveedor);

        service.eliminar(1L);

        verify(repo).delete(1L);
    }

    @Test
    void eliminar_proveedorNoExiste_deberiaLanzarProveedorNotFoundException() throws ProveedorNotFoundException {
        when(repo.findById(1L)).thenThrow(new ProveedorNotFoundException("No existe"));

        assertThrows(ProveedorNotFoundException.class, () -> service.eliminar(1L));

        verify(repo, never()).delete(anyLong());
    }

    // ---------------- GET
    @Test
    void getProveedor_deberiaRetornarProveedor() throws ProveedorNotFoundException {
        Proveedor proveedor = mock(Proveedor.class);

        when(repo.findById(1L)).thenReturn(proveedor);

        Proveedor resultado = service.getProveedor(1L);

        assertEquals(proveedor, resultado);
    }

    @Test
    void getProveedores_deberiaRetornarLista() {
        List<Proveedor> lista = List.of(mock(Proveedor.class), mock(Proveedor.class));

        when(repo.findAll()).thenReturn(lista);

        List<Proveedor> resultado = service.getProveedores();

        assertEquals(2, resultado.size());
    }

    @Test
    void getProveedor_proveedorNoExiste_deberiaLanzarProveedorNotFoundException() throws ProveedorNotFoundException {
        when(repo.findById(1L)).thenThrow(new ProveedorNotFoundException("No existe"));

        assertThrows(ProveedorNotFoundException.class, () -> service.getProveedor(1L));
    }
}
