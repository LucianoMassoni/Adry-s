package com.negocio.adris.model.service;

import com.negocio.adris.model.dtos.ProductoDivisibleDto;
import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.dtos.ProductoDto;
import com.negocio.adris.model.exceptions.ProductoNotFoundException;

import java.math.BigDecimal;
import java.util.List;

public interface ProductoService {
    void crearProducto(ProductoDto productoDto);
    void modificarProducto(long id, ProductoDto productoDto) throws ProductoNotFoundException;
    void crearProductoDivisible(ProductoDivisibleDto dto);
    void modificarProductoDivisible(long id, ProductoDivisibleDto dto) throws ProductoNotFoundException;
    List<Producto> obtenerTodosProductos() throws ProductoNotFoundException;
    void eliminarProducto(long id) throws ProductoNotFoundException;
    Producto obtenerProductoPorId(long id) throws ProductoNotFoundException;
    void comprarProducto(Producto producto, BigDecimal cantidad);
}
