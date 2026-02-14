package com.negocio.adris.model.repository;

import com.google.inject.Provider;
import com.negocio.adris.config.DBInitializer;
import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.entities.Pago;
import com.negocio.adris.model.entities.Proveedor;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PagoRepositoryTest {
    private Provider<Connection> testProvider;
    private Connection keepAliveConn;

    private PagoRepositoryImpl pagoRepo;
    private ProveedorRepositoryImpl proveedorRepo;
    private GastoRepositoryImpl gastoRepo;

    @BeforeEach
    void setUp() throws Exception {
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

        pagoRepo = new PagoRepositoryImpl(testProvider);
        proveedorRepo = new ProveedorRepositoryImpl(testProvider);
        gastoRepo = new GastoRepositoryImpl(testProvider);
    }

    @AfterEach
    void tearDown() throws Exception {
        keepAliveConn.close();
    }

    // --------------- HELPERS
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

    private Pago crearPagoPersistido() {
        Gasto gasto = crearGastoPersistido();

        Pago pago = new Pago(
                0L,
                gasto,
                LocalDateTime.now(),
                BigDecimal.valueOf(500)
        );

        pagoRepo.save(pago);
        return pago;
    }

    // --------------- SAVE

    @Test
    void save_deberiaAsignarId() {
        Pago pago = crearPagoPersistido();

        assertTrue(pago.getId() > 0);
    }

    @Test
    void save_deberiaPersistirCorrectamente() throws Exception {
        Pago pago = crearPagoPersistido();

        Pago encontrado = pagoRepo.findById(pago.getId());

        assertEquals(0, pago.getMontoPagado().compareTo(encontrado.getMontoPagado()));
        assertEquals(pago.getGasto().getId(), encontrado.getGasto().getId());
    }

    // --------------- UPDATE

    @Test
    void update_deberiaModificarPago() throws Exception {
        Pago pago = crearPagoPersistido();

        pago.setMontoPagado(BigDecimal.valueOf(800));

        pagoRepo.update(pago);

        Pago actualizado = pagoRepo.findById(pago.getId());

        assertEquals(0, BigDecimal.valueOf(800).compareTo(actualizado.getMontoPagado()));
    }

    @Test
    void update_pagoInexistente_noDeberiaAfectarNada() {
        Pago pago = new Pago(
                999L,
                crearGastoPersistido(),
                LocalDateTime.now(),
                BigDecimal.valueOf(500)
        );

        assertDoesNotThrow(() -> pagoRepo.update(pago));
    }

    // --------------- DELETE

    @Test
    void delete_deberiaDesactivarPago() throws Exception {
        Pago pago = crearPagoPersistido();

        pagoRepo.delete(pago.getId());

        assertThrows(PagoNotFoundException.class, () -> pagoRepo.findById(pago.getId()));
    }

    @Test
    void delete_pagoInexistente_noDeberiaRomper() {
        assertDoesNotThrow(() -> pagoRepo.delete(999L));
    }


    // --------------- FIND BY ID

    @Test
    void findById_deberiaRetornarPagoConRelaciones() throws Exception {
        Pago pago = crearPagoPersistido();

        Pago encontrado = pagoRepo.findById(pago.getId());

        assertNotNull(encontrado);
        assertEquals(pago.getId(), encontrado.getId());

        assertNotNull(encontrado.getGasto());
        assertNotNull(encontrado.getGasto().getProveedor());
    }

    @Test
    void findById_inexistente_deberiaLanzarExcepcion() {
        assertThrows(PagoNotFoundException.class,
                () -> pagoRepo.findById(999L));
    }

    // --------------- FIND ALL

    @Test
    void findAll_deberiaRetornarSoloActivos() {
        crearPagoPersistido();
        crearPagoPersistido();

        List<Pago> lista = pagoRepo.findAll();

        assertEquals(2, lista.size());
    }

    @Test
    void findAll_sinPagos_deberiaRetornarListaVacia() {
        List<Pago> lista = pagoRepo.findAll();

        assertTrue(lista.isEmpty());
    }

    @Test
    void findAll_noDeberiaTraerPagosDesactivados() throws Exception {
        Pago pago = crearPagoPersistido();

        pagoRepo.delete(pago.getId());

        List<Pago> lista = pagoRepo.findAll();

        assertTrue(lista.isEmpty());
    }


    // --------------- getAllPagosPorFecha

    @Test
    void getAllPagosPorFecha_deberiaFiltrarPorFecha() {
        Pago pago = crearPagoPersistido();

        String fecha = pago.getFechaPago()
                .toLocalDate()
                .toString();

        List<Pago> lista = pagoRepo.getAllPagosPorFecha(fecha);

        assertFalse(lista.isEmpty());
    }

    @Test
    void getAllPagosPorFecha_sinCoincidencias_deberiaRetornarListaVacia() {
        crearPagoPersistido();

        List<Pago> lista = pagoRepo.getAllPagosPorFecha("1900-01-01");

        assertTrue(lista.isEmpty());
    }


    // --------------- getEgresosMes
    @Test
    void getEgresosMes_deberiaAgruparPorDia() {
        crearPagoPersistido();
        crearPagoPersistido();

        YearMonth yearMonth = YearMonth.now();

        Map<LocalDate, BigDecimal> map = pagoRepo.getEgresosMes(yearMonth);

        assertFalse(map.isEmpty());
    }

    @Test
    void getEgresosMes_deberiaSumarCorrectamentePorDia() {
        Gasto gasto = crearGastoPersistido();

        Pago p1 = new Pago(
                0,
                gasto,
                LocalDateTime.now(),
                BigDecimal.valueOf(100)
        );

        Pago p2 = new Pago(
                0,
                gasto,
                LocalDateTime.now(),
                BigDecimal.valueOf(200)
        );

        pagoRepo.save(p1);
        pagoRepo.save(p2);

        YearMonth ym = YearMonth.now();

        Map<LocalDate, BigDecimal> map = pagoRepo.getEgresosMes(ym);

        BigDecimal totalEsperado = BigDecimal.valueOf(300);

        assertEquals(0, totalEsperado.compareTo(map.get(LocalDate.now())));
    }

}


