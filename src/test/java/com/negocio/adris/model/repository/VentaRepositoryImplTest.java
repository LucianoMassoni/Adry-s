package com.negocio.adris.model.repository;

import com.google.inject.Provider;
import com.negocio.adris.config.DBInitializer;
import com.negocio.adris.model.entities.DetalleVenta;
import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.entities.Venta;
import com.negocio.adris.model.enums.FormaDePago;
import com.negocio.adris.model.enums.UnidadMedida;
import com.negocio.adris.model.exceptions.VentaNotFoundException;
import com.negocio.adris.model.repositories.ProductoRepositoryImpl;
import com.negocio.adris.model.repositories.VentaRepositoryImpl;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VentaRepositoryImplTest {

    private Provider<Connection> testProvider;
    private Connection keepAliveConn;

    private VentaRepositoryImpl ventaRepo;
    private ProductoRepositoryImpl productoRepo;

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

        ventaRepo = new VentaRepositoryImpl(testProvider);
        productoRepo = new ProductoRepositoryImpl(testProvider);
    }

    @AfterEach
    void tearDown() throws Exception {
        keepAliveConn.close();
    }

    // ---------------- SAVE ----------------

    @Test
    void save_deberiaGuardarVentaConDetalles() {
        Producto producto = crearProductoPersistido();

        Venta venta = ventaBase(producto);

        ventaRepo.save(venta);

        assertNotNull(venta.getId());
        assertTrue(venta.getId() > 0);

        List<Venta> ventas = ventaRepo.findAll();

        assertEquals(1, ventas.size());
        assertEquals(1, ventas.getFirst().getDetalleVentas().size());
    }

    @Test
    void save_ventaConMultiplesDetalles_deberiaGuardarlosTodos() throws Exception {
        Producto p1 = crearProductoPersistido();
        Producto p2 = crearProductoPersistido();

        DetalleVenta d1 = new DetalleVenta(
                0, p1,
                BigDecimal.ONE,
                BigDecimal.valueOf(100),
                BigDecimal.ZERO,
                BigDecimal.valueOf(100)
        );

        DetalleVenta d2 = new DetalleVenta(
                0, p2,
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(200),
                BigDecimal.ZERO,
                BigDecimal.valueOf(400)
        );

        List<DetalleVenta> lista = new ArrayList<>();
        lista.add(d1);
        lista.add(d2);
        System.out.println(lista.size());

        Venta venta = new Venta(
                0,
                FormaDePago.EFECTIVO,
                LocalDateTime.now(),
                BigDecimal.valueOf(500),
                lista
        );

        ventaRepo.save(venta);

        Venta encontrada = ventaRepo.findById(venta.getId());

        assertEquals(2, encontrada.getDetalleVentas().size());
    }

    // ---------------- FIND BY ID ----------------

    @Test
    void findById_deberiaReconstruirVentaCompleta() throws Exception {
        Producto producto = crearProductoPersistido();
        Venta venta = ventaBase(producto);

        ventaRepo.save(venta);
        long id = venta.getId();

        Venta encontrada = ventaRepo.findById(id);

        assertEquals(id, encontrada.getId());
        assertEquals(venta.getFormaDePago(), encontrada.getFormaDePago());
        assertEquals(0, venta.getTotal().compareTo(encontrada.getTotal()));
        assertEquals(1, encontrada.getDetalleVentas().size());

        DetalleVenta detalle = encontrada.getDetalleVentas().getFirst();
        assertEquals(producto.getId(), detalle.getProducto().getId());
        assertEquals(0, BigDecimal.valueOf(2).compareTo(detalle.getCantidad()));
    }

    @Test
    void findById_ventaInexistente_deberiaLanzarExcepcion() {
        assertThrows(VentaNotFoundException.class,
                () -> ventaRepo.findById(999L));
    }

    // ---------------- DELETE ----------------

    @Test
    void delete_deberiaInactivarVenta() {
        Producto producto = crearProductoPersistido();
        Venta venta = ventaBase(producto);

        ventaRepo.save(venta);
        long id = venta.getId();

        ventaRepo.delete(id);

        List<Venta> ventas = ventaRepo.findAll();

        assertTrue(ventas.isEmpty());
    }

    @Test
    void delete_deberiaOcultarVentaYDetalles() throws Exception {
        Producto producto = crearProductoPersistido();
        Venta venta = ventaBase(producto);

        ventaRepo.save(venta);
        long id = venta.getId();

        ventaRepo.delete(id);

        assertThrows(VentaNotFoundException.class,
                () -> ventaRepo.findById(id));

        assertTrue(ventaRepo.findAll().isEmpty());
    }

    // ---------------- FIND ALL ----------------

    @Test
    void findAll_deberiaRetornarVentasOrdenadasPorFechaDesc() {
        Producto producto = crearProductoPersistido();

        Venta venta1 = ventaConFecha(producto, LocalDateTime.now().minusDays(1));
        Venta venta2 = ventaConFecha(producto, LocalDateTime.now());

        ventaRepo.save(venta1);
        ventaRepo.save(venta2);

        List<Venta> ventas = ventaRepo.findAll();

        assertEquals(2, ventas.size());
        assertEquals(venta2.getId(), ventas.getFirst().getId()); // más reciente primero
    }

    @Test
    void findAll_sinVentas_deberiaRetornarListaVacia() {
        List<Venta> ventas = ventaRepo.findAll();
        assertTrue(ventas.isEmpty());
    }

    @Test
    void findAll_deberiaReconstruirMultiplesVentasCorrectamente() {
        Producto producto = crearProductoPersistido();

        Venta v1 = ventaBase(producto);
        Venta v2 = ventaBase(producto);

        ventaRepo.save(v1);
        ventaRepo.save(v2);

        List<Venta> ventas = ventaRepo.findAll();

        assertEquals(2, ventas.size());

        for (Venta v : ventas) {
            assertEquals(1, v.getDetalleVentas().size());
        }
    }

    // ---------------- FILTRO POR FECHA ----------------

    @Test
    void getAllVentasByFecha_deberiaFiltrarCorrectamente() {
        Producto producto = crearProductoPersistido();

        LocalDateTime fecha = LocalDateTime.of(2025, 1, 15, 10, 0);

        Venta venta = ventaConFecha(producto, fecha);
        ventaRepo.save(venta);

        List<Venta> resultado = ventaRepo.getAllVentasByFecha("2025-01-15");

        assertEquals(1, resultado.size());
        assertEquals(venta.getId(), resultado.getFirst().getId());
    }

    @Test
    void getAllVentasByFecha_sinResultados_deberiaRetornarListaVacia() {
        List<Venta> resultado = ventaRepo.getAllVentasByFecha("2099-01-01");
        assertTrue(resultado.isEmpty());
    }

    // ---------------- FACTURACIÓN ----------------

    @Test
    void getFacturacionMes_deberiaAgruparPorDia() {
        Producto producto = crearProductoPersistido();

        LocalDateTime fecha1 = LocalDateTime.of(2025, 1, 10, 10, 0);
        LocalDateTime fecha2 = LocalDateTime.of(2025, 1, 10, 12, 0);

        Venta v1 = ventaConFecha(producto, fecha1);
        Venta v2 = ventaConFecha(producto, fecha2);

        ventaRepo.save(v1);
        ventaRepo.save(v2);

        Map<LocalDate, BigDecimal> map =
                ventaRepo.getFacturacionMes(YearMonth.of(2025, 1));

        assertEquals(1, map.size());

        BigDecimal esperado =
                v1.getTotal().add(v2.getTotal());

        assertEquals(0,
                esperado.compareTo(map.get(LocalDate.of(2025, 1, 10))));
    }

    @Test
    void getFacturacionMes_sinVentas_deberiaRetornarMapaVacio() {

        Map<LocalDate, BigDecimal> map = ventaRepo.getFacturacionMes(YearMonth.of(2030, 1));

        assertTrue(map.isEmpty());
    }


    // ---------------- HELPERS ----------------

    private Producto crearProductoPersistido() {
        Producto p = new Producto();
        p.setNombre("Queso");
        p.setMarca("Marca");
        p.setPeso(1.0);
        p.setPesoActual(1.0);
        p.setUnidadMedida(UnidadMedida.UNIDAD);
        p.setCantidad(10);
        p.setCosto(BigDecimal.valueOf(100));
        p.setGanancia(BigDecimal.valueOf(20));
        p.setPrecio(BigDecimal.valueOf(120));
        p.setEsDivisible(true);

        productoRepo.save(p);
        return p;
    }

    private Venta ventaBase(Producto producto) {
        return ventaConFecha(producto, LocalDateTime.now());
    }

    private Venta ventaConFecha(Producto producto, LocalDateTime fecha) {
        DetalleVenta detalle = new DetalleVenta(
                0,
                producto,
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(120),
                BigDecimal.ZERO,
                BigDecimal.valueOf(240)
        );

        return new Venta(
                0,
                FormaDePago.EFECTIVO,
                fecha,
                BigDecimal.valueOf(240),
                List.of(detalle)
        );
    }
}

