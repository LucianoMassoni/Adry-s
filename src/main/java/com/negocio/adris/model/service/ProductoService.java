package com.negocio.adris.model.service;

import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.dtos.ProductoDto;
import com.negocio.adris.model.exceptions.ProductoNotFoundException;

import java.util.List;

public interface ProductoService {
    void crearProducto(ProductoDto productoDto);
    void modificarProducto(long id, ProductoDto productoDto) throws ProductoNotFoundException;
    List<Producto> obtenerTodosProductos() throws ProductoNotFoundException;
    void eliminarProducto(long id) throws ProductoNotFoundException;
    Producto obtenerProductoPorId(long id) throws ProductoNotFoundException;
}
