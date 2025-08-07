package com.negocio.adris.viewmodel;

import com.google.inject.Inject;
import com.negocio.adris.model.dtos.ProductoDto;
import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.enums.TipoProducto;
import com.negocio.adris.model.enums.UnidadMedida;
import com.negocio.adris.model.exceptions.ProductoNotFoundException;
import com.negocio.adris.model.service.ProductoService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProductoViewModel {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty marca = new SimpleStringProperty();
    private final DoubleProperty peso = new SimpleDoubleProperty();
    private final DoubleProperty pesoActual = new SimpleDoubleProperty();
    private final ObjectProperty<UnidadMedida> unidadMedida = new SimpleObjectProperty<>();
    private final IntegerProperty cantidad = new SimpleIntegerProperty();
    private final ObjectProperty<BigDecimal> costo = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> ganancia = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> precioSugerido = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> precio = new SimpleObjectProperty<>();
    private final ObjectProperty<TipoProducto> tipo = new SimpleObjectProperty<>();
    private final BooleanProperty esDivisible = new SimpleBooleanProperty();

    private final ObservableList<Producto> productos = FXCollections.observableArrayList();
    private final FilteredList<Producto> productosFiltrados = new FilteredList<>(productos, p -> true);

    private final StringProperty filtroBusqueda = new SimpleStringProperty();

    private final ProductoService productoService;

    @Inject
    ProductoViewModel(ProductoService service) throws ProductoNotFoundException {
        this.productoService = service;

        // Vincula el precio automáticamente cuando se cambia el costo o la ganancia
        precioSugerido.bind(Bindings.createObjectBinding(() -> {
            if (costo.get() != null && ganancia.get() != null) {
                try {
                    BigDecimal multiplicador = BigDecimal.ONE.add(ganancia.get().divide(BigDecimal.valueOf(100)));
                    return costo.get().multiply(multiplicador).setScale(2, RoundingMode.HALF_UP);
                } catch (ArithmeticException e) {
                    return BigDecimal.ZERO;
                }
            }
            return BigDecimal.ZERO;
        }, costo, ganancia));

        filtroBusqueda.addListener((obs, oldVal, newVal) -> {
            try {
                productos.setAll(service.obtenerProductosFiltrados(newVal));
            } catch (ProductoNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        cargarProductos();
    }

    public void cargarProductos() throws ProductoNotFoundException {
       productos.setAll(productoService.obtenerTodosProductos());
    }

    public void limpiarFormulario() {
        id.set(0);
        nombre.set("");
        marca.set("");
        peso.set(0);
        pesoActual.set(0);
        unidadMedida.set(null);
        cantidad.set(0);
        costo.set(BigDecimal.ZERO);
        ganancia.set(BigDecimal.ZERO);
        precio.set(BigDecimal.ZERO);
        tipo.set(null);
        esDivisible.set(false);
    }

    public void cargarProducto(Producto p) throws ProductoNotFoundException {
        id.set(p.getId());
        nombre.set(p.getNombre());
        marca.set(p.getMarca());
        peso.set(p.getPeso());
        pesoActual.set(p.getPesoActual());
        unidadMedida.set(p.getUnidadMedida());
        cantidad.set(p.getCantidad());
        costo.set(p.getCosto());
        ganancia.set(p.getGanancia());
        precio.set(p.getPrecio());
        tipo.set(p.getTipo());
        esDivisible.set(p.esDivisible());
    }

    public void guardarProducto() throws ProductoNotFoundException {
        ProductoDto dto = new ProductoDto(
                nombre.get(),
                marca.get(),
                peso.get(),
                pesoActual.get(),
                unidadMedida.get(),
                cantidad.get(),
                costo.get(),
                ganancia.get(),
                precio.get(),
                tipo.get(),
                esDivisible.get()
        );

        productoService.crearProducto(dto);
        limpiarFormulario();
        cargarProductos();
    }

    public void modificarProducto() throws ProductoNotFoundException {
        ProductoDto dto = new ProductoDto(
                nombre.get(),
                marca.get(),
                peso.get(),
                pesoActual.get(),
                unidadMedida.get(),
                cantidad.get(),
                costo.get(),
                ganancia.get(),
                precio.get(),
                tipo.get(),
                esDivisible.get()
        );

        productoService.modificarProducto(id.get(), dto);
        limpiarFormulario();
        cargarProductos();
    }

    public void eliminarProducto(Long id) throws ProductoNotFoundException {
        productoService.eliminarProducto(id);

        limpiarFormulario();
        cargarProductos();
    }


    public LongProperty idProperty() { return id; }
    public StringProperty nombreProperty() {return nombre; }
    public StringProperty marcaProperty() { return marca; }
    public DoubleProperty pesoProperty() { return peso; }
    public DoubleProperty pesoACtualProperty() { return pesoActual; }
    public ObjectProperty<UnidadMedida> unidadMedidaProperty() { return unidadMedida; }
    public IntegerProperty cantidadProperty() { return cantidad; }
    public ObjectProperty<BigDecimal> costoProperty() { return costo; }
    public ObjectProperty<BigDecimal> gananciaProperty() { return ganancia; }
    public ObjectProperty<BigDecimal> precioSugeridoProperty() { return precioSugerido; }
    public ObjectProperty<BigDecimal> precioProperty() { return precio; }
    public ObjectProperty<TipoProducto> tipoProperty() { return tipo; }
    public BooleanProperty esDivisibleProperty() { return esDivisible; }

    public StringProperty filtroBusquedaProperty() { return filtroBusqueda; }
    public ObservableList<Producto> getProductosFiltrados() { return productosFiltrados; }
}
