package com.negocio.adris.model.repository;

import com.google.inject.Provider;
import com.negocio.adris.config.DBInitializer;
import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.enums.UnidadMedida;
import com.negocio.adris.model.exceptions.ProductoNotFoundException;
import com.negocio.adris.model.repositories.ProductoRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductoRepositoryImplTest {
    private Provider<Connection> testProvider;
    private Connection keepAliveConn;

    private ProductoRepositoryImpl repo;

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

        repo = new ProductoRepositoryImpl(testProvider);
    }

    @AfterEach
    void tearDown() throws Exception {
        keepAliveConn.close();
    }

    // ------------------------- SAVE
    @Test
    void save_deberiaGuardarProductoCorrectamente() {
        Producto producto = productoBase();

        repo.save(producto);

        List<Producto> productos = repo.findAll();

        assertEquals(1, productos.size());
        assertEquals("Queso", productos.getFirst().getNombre());
        assertEquals("Marca", productos.getFirst().getMarca());
        assertEquals(1.0, productos.getFirst().getPeso());
        assertEquals(1.0, productos.getFirst().getPesoActual());
        assertEquals(UnidadMedida.UNIDAD, productos.getFirst().getUnidadMedida());
        assertEquals(10, productos.getFirst().getCantidad());
        assertEquals(0, BigDecimal.valueOf(100).compareTo(productos.getFirst().getCosto()));
        assertEquals(0, BigDecimal.valueOf(20).compareTo(productos.getFirst().getGanancia()));
        assertEquals(0, BigDecimal.valueOf(120).compareTo(productos.getFirst().getPrecio()));
        assertTrue(productos.getFirst().esDivisible());
    }

    @Test
    void save_deberiaAsignarId() {
        Producto producto = productoBase();

        repo.save(producto);

        List<Producto> productos = repo.findAll();

        assertTrue(productos.getFirst().getId() > 0);
    }

    @Test
    void save_variosProductos_deberianTenerIdsIncrementales() {
        Producto producto = productoBase();

        repo.save(producto);
        repo.save(producto);
        repo.save(producto);

        List<Producto> productos = repo.findAll();

        assertEquals(3, productos.size());
        assertEquals(1, productos.getFirst().getId());
        assertEquals(2, productos.get(1).getId());
        assertEquals(3, productos.getLast().getId());
    }

    @Test
    void save_deberiaPermitirUnidadMedidaNull() {
        Producto producto = productoBase();
        producto.setUnidadMedida(null);

        repo.save(producto);

        Producto guardado = repo.findAll().getFirst();

        assertNull(guardado.getUnidadMedida());
    }

    // ------------------------- FIND BY ID
    @Test
    void findById_deberiaRetornarProductoCorrecto() throws ProductoNotFoundException {
        Producto producto = productoBase();
        repo.save(producto);

        long id = producto.getId();

        Producto recibido = repo.findById(id);

        assertEquals(id, recibido.getId());
        assertEquals(producto.getNombre(), recibido.getNombre());
        assertEquals(producto.getCantidad(), recibido.getCantidad());

    }

    @Test
    void findById_DeberiaSerIgualAlSegundoProducto() throws ProductoNotFoundException {
        Producto producto1 = productoBase();
        Producto producto2 = productoBase();

        // le cambio el nombre para corroborar que es distinto al producto1
        producto2.setNombre("Nombre distinto");

        repo.save(producto1);
        repo.save(producto2);

        // busco al segundo producto
        long id = 2;
        Producto recibido = repo.findById(id);

        assertEquals(id, recibido.getId());
        assertEquals(producto2.getNombre(), recibido.getNombre());
        assertNotEquals(producto1.getNombre(), recibido.getNombre());
    }

    @Test
    void findById_productoInexistente_deberiaLanzarExcepcion(){
        assertThrows(ProductoNotFoundException.class, () -> repo.findById(999L));
    }

    @Test
    void findById_noDeberiaEncontrarProductoInactivo() {
        Producto producto = productoBase();
        repo.save(producto);

        long id = producto.getId();

        repo.delete(id);

        assertThrows(ProductoNotFoundException.class, () -> repo.findById(id));
    }

    // ------------------------- FIND ALL
    @Test
    void findAll_VacioDeberiaDevolverListaVacia() {
        List<Producto> lista = assertDoesNotThrow(()-> repo.findAll());
        assertTrue(lista.isEmpty());
    }

    // ------------------------- UPDATE
    @Test
    void update_deberiaModificarProducto() throws ProductoNotFoundException {
        Producto producto = productoBase();
        repo.save(producto);

        Long id = producto.getId();

        producto.setNombre("nombreDistinto");
        producto.setCantidad(99);

        repo.update(producto);

        Producto actualizado = repo.findById(id);

        assertEquals("nombreDistinto", actualizado.getNombre());
        assertEquals(99, actualizado.getCantidad());
    }

    @Test
    void delete_deberiaEliminarProductoLogicamente(){
        Producto producto = productoBase();

        repo.save(producto);

        assertEquals(1, repo.findAll().size());

        repo.delete(1);

        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void delete_EnElMedio(){
        // creo los productos
        Producto producto1 = productoBase();
        Producto producto2 = productoBase();
        Producto producto3 = productoBase();

        // les cambio el nombre para reconocerlos
        producto1.setNombre("producto 1");
        producto2.setNombre("producto 2");
        producto3.setNombre("producto 3");

        // los guardo
        repo.save(producto1);
        repo.save(producto2);
        repo.save(producto3);

        // traigo la lista de productos
        List<Producto> productos = repo.findAll();

        // asserts del estado actual
        assertEquals(3, productos.size());
        assertEquals("producto 1", productos.getFirst().getNombre());
        assertEquals(1, productos.getFirst().getId());
        assertEquals("producto 2", productos.get(1).getNombre());
        assertEquals(2, productos.get(1).getId());
        assertEquals("producto 3", productos.getLast().getNombre());
        assertEquals(3, productos.getLast().getId());

        // delete al del medio
        repo.delete(2);

        // actualizo la lista
        productos = repo.findAll();

        // asserts finales
        assertEquals(2, productos.size());
        assertEquals("producto 1", productos.getFirst().getNombre());
        assertEquals(1, productos.getFirst().getId());
        assertEquals("producto 3", productos.get(1).getNombre());
        assertEquals(3, productos.getLast().getId());
    }

    @Test
    void delete_productoInexistente_noDeberiaLanzarError(){
        assertDoesNotThrow( ()-> repo.delete(999L));

    }

    private Producto productoBase() {
        Producto producto = new Producto();
        producto.setNombre("Queso");
        producto.setMarca("Marca");
        producto.setPeso(1.0);
        producto.setPesoActual(1.0);
        producto.setUnidadMedida(UnidadMedida.UNIDAD);
        producto.setCantidad(10);
        producto.setCosto(BigDecimal.valueOf(100));
        producto.setGanancia(BigDecimal.valueOf(20));
        producto.setPrecio(BigDecimal.valueOf(120));
        producto.setEsDivisible(true);
        return producto;
    }
}
