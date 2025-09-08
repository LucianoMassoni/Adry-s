package com.negocio.adris.model.service;

import com.google.inject.Inject;
import com.negocio.adris.model.dtos.ProductoDto;
import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.enums.UnidadMedida;
import com.negocio.adris.model.exceptions.ProductoNotFoundException;
import com.negocio.adris.model.repositories.ProductoRepository;
import com.negocio.adris.utils.Utils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class ProductoServiceImpl implements ProductoService {
    private final Validator validator;
    private final ProductoRepository repo;

    @Inject
    public ProductoServiceImpl(ProductoRepository repo, Validator validator){
        this.repo = repo;
        this.validator = validator;
    }

    private void validarProducto(ProductoDto dto){
        Set<ConstraintViolation<ProductoDto>> violations = validator.validate(dto);
        if(!violations.isEmpty()){
            String errores = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));
            throw new IllegalArgumentException(errores);
        }

        if (dto.getTipo() != null && dto.getUnidadMedida() != null &&
                !dto.getTipo().admiteUnidad(dto.getUnidadMedida())) {
            throw new IllegalArgumentException(
                    "La unidad de medida " + dto.getUnidadMedida().getSimbolo() +
                            " no es válida para el tipo de producto " + dto.getTipo()
            );
        }
    }

    private Producto convertirDtoAProducto(ProductoDto dto){
        return new Producto(
                0, //Id temporal (la DB le da el ID real cuando lo guarda)
                dto.getNombre(),
                dto.getMarca(),
                dto.getPeso(),
                dto.getPeso(),  // cuando se crea el producto, el peso actual es el mismo que el peso.
                dto.getUnidadMedida(),
                dto.getCantidad(),
                dto.getCosto(),
                dto.getGanancia(),
                dto.getPrecio(),
                dto.getTipo(),
                dto.esDivisible()
        );
    }

    private void verificarCostoGananciaPrecio(BigDecimal costo, BigDecimal ganancia, BigDecimal precio){
        BigDecimal precioMinimo = costo.multiply(BigDecimal.ONE.add(ganancia.divide(new BigDecimal(100)))).subtract(new BigDecimal(50));
        if (precio.compareTo(precioMinimo) < 0) {
            throw new IllegalArgumentException(String.format(
                    "El precio $%.2f no puede ser menor al costo $%.2f más %s%% de ganancia ($%.2f)",
                    precio, costo, ganancia, precioMinimo
            ));
        }
    }

    @Override
    public void crearProducto(ProductoDto dto){
        validarProducto(dto);

        verificarCostoGananciaPrecio(dto.getCosto(), dto.getGanancia(), dto.getPrecio());

        dto.setNombre(Utils.capitalize(dto.getNombre()));
        dto.setMarca(Utils.capitalize(dto.getMarca()));

        Producto producto = convertirDtoAProducto(dto);

        repo.save(producto);
    }

    @Override
    public void modificarProducto(long id, ProductoDto dto) throws ProductoNotFoundException {
        validarProducto(dto);

        verificarCostoGananciaPrecio(dto.getCosto(), dto.getGanancia(), dto.getPrecio());

        dto.setNombre(Utils.capitalize(dto.getNombre()));
        dto.setMarca(Utils.capitalize(dto.getMarca()));

        Producto p = repo.findById(id);
        p.setNombre(dto.getNombre());
        p.setMarca(dto.getMarca());
        p.setPeso(dto.getPeso());
        p.setPesoActual(dto.getPesoActual());
        p.setCantidad(dto.getCantidad());
        p.setCosto(dto.getCosto());
        p.setGanancia(dto.getGanancia());
        p.setPrecio(dto.getPrecio());
        p.setTipo(dto.getTipo());
        p.setEsDivisible(dto.esDivisible());

        repo.update(p);
    }

    @Override
    public List<Producto> obtenerTodosProductos() throws ProductoNotFoundException {
        return repo.findAll();
    }

    @Override
    public void eliminarProducto(long id) throws ProductoNotFoundException {
        // Busca el producto para que, en caso de que no exista tire la exception.
        Producto p = repo.findById(id);
        repo.delete(id);
    }

    @Override
    public Producto obtenerProductoPorId(long id) throws ProductoNotFoundException {
        return repo.findById(id);
    }

    @Override
    public List<Producto> obtenerProductosFiltrados(String busqueda) throws ProductoNotFoundException {
        List<Producto> productos = repo.findAll();
        if (busqueda == null || busqueda.isEmpty())
            return productos;
        return productos.stream().
                filter(p -> (p.getNombre().toLowerCase() + " " + p.getMarca().toLowerCase()).contains(busqueda.toLowerCase()))
                .collect(Collectors.toList());
    }

    private Producto compraProductoDivisible(Producto producto, BigDecimal cantidad, UnidadMedida unidadMedida){
        if (unidadMedida.equals(UnidadMedida.KILOS)){
            if (producto.getUnidadMedida().equals(UnidadMedida.KILOS) && producto.getPesoActual() < cantidad.intValue()) throw new IllegalArgumentException("No hay suficiente " + producto.getNombre());
            producto.setPesoActual(producto.getPesoActual() - cantidad.intValue());
        } else {
            if (producto.getUnidadMedida().equals(UnidadMedida.GRAMOS) && producto.getPesoActual() < cantidad.intValue()) throw new IllegalArgumentException("No hay suficiente " + producto.getNombre());

            producto.setPesoActual(producto.getPesoActual() - (double) cantidad.intValue() /1000);
        }
        return producto;
    }

    @Override
    public void comprarProducto(Producto producto, BigDecimal cantidad, UnidadMedida unidadMedida) {
        if (producto.esDivisible()){
            producto = compraProductoDivisible(producto, cantidad, unidadMedida);
        } else {
            producto.setCantidad(producto.getCantidad() - cantidad.intValue());
            if (producto.getCantidad() < 0) throw new IllegalArgumentException("No hay " + cantidad + " de " + producto.getNombre());
        }

        repo.update(producto);
    }

    @Override
    public BigDecimal getPrecioPorGramosComprados(Producto p, UnidadMedida medida, BigDecimal cantidad){
        if (medida.equals(UnidadMedida.KILOS) && !medida.equals(p.getUnidadMedida())) throw new IllegalArgumentException("El producto no se mide en kilos");

        double pesoActualProductoEnGramos = p.getUnidadMedida().equals(UnidadMedida.KILOS) ? p.getPesoActual() * 1000 : p.getPesoActual();
        double pesoProductoEnGramos = p.getUnidadMedida().equals(UnidadMedida.KILOS) ? p.getPeso() * 1000 : p.getPeso();
        BigDecimal pesoEnGramos = medida.equals(UnidadMedida.KILOS) ? cantidad.multiply(BigDecimal.valueOf(1000)) : cantidad;

        if (pesoEnGramos.compareTo(BigDecimal.valueOf(pesoActualProductoEnGramos)) > 0) throw new IllegalArgumentException("no hay " + cantidad + "  "+ medida.getSimbolo() + " de " + p.getNombre() +" hay: " + p.getPesoActual() + " " + p.getUnidadMedida().getSimbolo());

        BigDecimal precioProductoPorGramos = p.getPrecio().divide(BigDecimal.valueOf(pesoProductoEnGramos), 8, RoundingMode.HALF_UP);

        return precioProductoPorGramos.multiply(pesoEnGramos).setScale(2, RoundingMode.HALF_UP);
    }
}
