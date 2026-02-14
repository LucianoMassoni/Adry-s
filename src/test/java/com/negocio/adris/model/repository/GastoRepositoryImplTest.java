package com.negocio.adris.model.repository;

import com.google.inject.Provider;
import com.negocio.adris.config.DBInitializer;
import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.entities.Pago;
import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.PagoNotFoundException;
import com.negocio.adris.model.repositories.GastoRepositoryImpl;
import com.negocio.adris.model.repositories.PagoRepositoryImpl;
import com.negocio.adris.model.repositories.ProveedorRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GastoRepositoryImplTest {
    private Provider<Connection> testProvider;
    private Connection keepAliveConn;

    private GastoRepositoryImpl gastoRepo;
    private ProveedorRepositoryImpl proveedorRepo;
    private PagoRepositoryImpl pagoRepo;

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
        gastoRepo = new GastoRepositoryImpl(testProvider);
        pagoRepo = new PagoRepositoryImpl(testProvider);
    }

    @AfterEach
    void tearDown() throws SQLException {
        keepAliveConn.close();
    }

    // -------------- HELPERS

    private Proveedor crearProveedorPersistido() {
        Proveedor proveedor = new Proveedor(
                0L,
                "Proveedor Test",
                "123456"
        );

        proveedorRepo.save(proveedor);
        return proveedor;
    }

    private Gasto crearGastoPersistido() {
        Proveedor proveedor = crearProveedorPersistido();

        Gasto gasto = new Gasto(
                0L,
                proveedor,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(10),
                BigDecimal.valueOf(1000),
                "nota test",
                false
        );

        gastoRepo.save(gasto);
        return gasto;
    }

    private Pago crearPagoPersistido(Gasto gasto) {
        Pago pago;
        pago = new Pago(
                0L,
                gasto,
                LocalDateTime.now(),
                BigDecimal.valueOf(500)
        );

        pagoRepo.save(pago);
        return pago;
    }

    // -------------- SAVE

    @Test
    void save_deberiaAsignarId() {
        Gasto gasto = crearGastoPersistido();
        assertTrue(gasto.getId() > 0);
    }

    @Test
    void save_deberiaPersistirCorrectamente() throws GastoNotFoundException {
        Gasto gasto = crearGastoPersistido();

        Gasto encontrado = gastoRepo.findById(gasto.getId());

        assertEquals(0, gasto.getMonto().compareTo(encontrado.getMonto()));
        assertEquals(gasto.getProveedor().getId(), encontrado.getProveedor().getId());
    }


    // -------------- UPDATE

    @Test
    void update_deberiaModificarGasto() throws GastoNotFoundException {
        Gasto gasto = crearGastoPersistido();

        gasto.setMonto(BigDecimal.valueOf(2000));
        gasto.setNota("nota modificada");
        gasto.setSaldado(true);

        gastoRepo.update(gasto);

        Gasto actualizado = gastoRepo.findById(gasto.getId());

        assertEquals(0, BigDecimal.valueOf(2000).compareTo(actualizado.getMonto()));
        assertEquals("nota modificada", actualizado.getNota());
        assertTrue(actualizado.isSaldado());
    }

    @Test
    void update_gastoInexistente_noDeberiaRomper() {
        Gasto gasto = crearGastoPersistido();

        assertDoesNotThrow(() -> gastoRepo.update(gasto));
    }

    // -------------- DELETE

    @Test
    void delete_deberiaDesactivarGasto() throws GastoNotFoundException {
        Gasto gasto = crearGastoPersistido();

        gastoRepo.delete(gasto.getId());

        assertThrows(GastoNotFoundException.class, () -> gastoRepo.findById(gasto.getId()));
    }


    @Test
    void delete_gastoInexistente_noDeberiaRomper() {
        assertDoesNotThrow(() -> gastoRepo.delete(999L));
    }

    // -------------- FIND BY ID

    @Test
    void findById_deberiaRetornarGastoConProveedor() throws GastoNotFoundException {
        Gasto gasto = crearGastoPersistido();

        Gasto encontrado = gastoRepo.findById(gasto.getId());

        assertNotNull(encontrado);
        assertNotNull(encontrado.getProveedor());
    }


    @Test
    void findById_deberiaRetornarGastoConPagos() throws GastoNotFoundException {
        Gasto gasto = crearGastoPersistido();
        crearPagoPersistido(gasto);
        crearPagoPersistido(gasto);

        Gasto encontrado = gastoRepo.findById(gasto.getId());

        assertEquals(2, encontrado.getPagos().size());
    }

    @Test
    void findById_inexistente_deberiaLanzarExcepcion() {
        assertThrows(GastoNotFoundException.class,
                () -> gastoRepo.findById(999L));
    }

    // -------------- FIND ALL

    @Test
    void findAll_deberiaRetornarSoloActivos() {
        crearGastoPersistido();
        crearGastoPersistido();

        List<Gasto> lista = gastoRepo.findAll();

        assertEquals(2, lista.size());
    }



    @Test
    void findAll_sinDatos_deberiaRetornarListaVacia() {
        List<Gasto> lista = gastoRepo.findAll();

        assertTrue(lista.isEmpty());
    }

    @Test
    void findAll_noDeberiaTraerEliminados() throws GastoNotFoundException {
        Gasto gasto = crearGastoPersistido();

        gastoRepo.delete(gasto.getId());

        List<Gasto> lista = gastoRepo.findAll();

        assertTrue(lista.isEmpty());
    }

    @Test
    void findAll_sinGastos_deberiaRetornarListaVacia() {
        List<Gasto> lista = gastoRepo.findAll();

        assertTrue(lista.isEmpty());
    }

    // -------------- PAGOS ASOCIADOS
    @Test
    void gasto_noDeberiaTraerPagosDesactivados() throws PagoNotFoundException, GastoNotFoundException {
        Gasto gasto = crearGastoPersistido();
        Pago pago = crearPagoPersistido(gasto);

        pagoRepo.delete(pago.getId());

        Gasto encontrado = gastoRepo.findById(gasto.getId());

        assertTrue(encontrado.getPagos().isEmpty());
    }
}

