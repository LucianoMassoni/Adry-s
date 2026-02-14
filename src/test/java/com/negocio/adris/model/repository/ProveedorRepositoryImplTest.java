package com.negocio.adris.model.repository;

import com.google.inject.Provider;
import com.negocio.adris.config.DBInitializer;
import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.model.repositories.ProveedorRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProveedorRepositoryImplTest {
    private Provider<Connection> testProvider;
    private Connection keepAliveConn;

    private ProveedorRepositoryImpl proveedorRepo;

    @BeforeEach
    void setUp() throws SQLException {
        String url = "jdbc:sqlite:file:testdb?mode=memory&cache=shared";

        keepAliveConn = DriverManager.getConnection(url);

        testProvider = () -> {
            try {
                return DriverManager.getConnection(url);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };

        new DBInitializer(testProvider);

        proveedorRepo = new ProveedorRepositoryImpl(testProvider);
    }

    @AfterEach
    void tearDown() throws SQLException {
        keepAliveConn.close();
    }

    private Proveedor crearProveedorPersistido() {
        Proveedor p = new Proveedor(0L, "Proveedor Test", "123456");
        proveedorRepo.save(p);
        return p;
    }

    // -------------------- SAVE

    @Test
    void save_deberiaAsignarId() {
        Proveedor p = new Proveedor(0L, "Proveedor A", "1111");

        proveedorRepo.save(p);

        assertTrue(p.getId() > 0);
    }

    @Test
    void save_deberiaPersistirDatosCorrectamente() throws ProveedorNotFoundException {
        Proveedor p = new Proveedor(0L, "Proveedor A", "1111");
        proveedorRepo.save(p);

        Proveedor encontrado = proveedorRepo.findById(p.getId());

        assertEquals("Proveedor A", encontrado.getNombre());
        assertEquals("1111", encontrado.getTelefono());
    }

    // -------------------- UPDATE
    @Test
    void update_deberiaModificarDatos() throws ProveedorNotFoundException {
        Proveedor p = crearProveedorPersistido();

        p.setNombre("Nuevo Nombre");
        p.setTelefono("9999");

        proveedorRepo.update(p);

        Proveedor actualizado = proveedorRepo.findById(p.getId());

        assertEquals("Nuevo Nombre", actualizado.getNombre());
        assertEquals("9999", actualizado.getTelefono());
    }

    // -------------------- DELETE (soft delete)
    @Test
    void delete_deberiaDesactivarProveedor() {
        Proveedor p = crearProveedorPersistido();

        proveedorRepo.delete(p.getId());

        List<Proveedor> todos = proveedorRepo.findAll();

        assertTrue(todos.isEmpty());
    }

    @Test
    void delete_noDeberiaEliminarFisicamente() throws ProveedorNotFoundException {
        Proveedor p = crearProveedorPersistido();

        long id = p.getId();

        proveedorRepo.delete(id);

        assertThrows(ProveedorNotFoundException.class, () ->proveedorRepo.findById(id));

    }

    // -------------------- FIND BY ID
    @Test
    void findById_deberiaRetornarProveedor() throws ProveedorNotFoundException {
        Proveedor p = crearProveedorPersistido();

        Proveedor encontrado = proveedorRepo.findById(p.getId());

        assertNotNull(encontrado);
        assertEquals(p.getId(), encontrado.getId());
    }

    @Test
    void findById_inexistente_deberiaLanzarExcepcion() {
        assertThrows(ProveedorNotFoundException.class,
                () -> proveedorRepo.findById(999L));
    }

    // -------------------- FIND ALL
    @Test
    void findAll_deberiaRetornarSoloActivos() {
        Proveedor p1 = crearProveedorPersistido();
        Proveedor p2 = crearProveedorPersistido();

        proveedorRepo.delete(p1.getId());

        List<Proveedor> lista = proveedorRepo.findAll();

        assertEquals(1, lista.size());
        assertEquals(p2.getId(), lista.get(0).getId());
    }

    @Test
    void findAll_sinProveedores_deberiaRetornarListaVacia() {
        List<Proveedor> lista = proveedorRepo.findAll();

        assertTrue(lista.isEmpty());
    }
}
