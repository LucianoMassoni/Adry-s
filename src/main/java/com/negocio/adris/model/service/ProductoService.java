package com.negocio.adris.model.service;

import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.dtos.ProductoDto;
import com.negocio.adris.model.exceptions.ProductoNotFoundException;

public interface ProductoService {
    void crearProducto(ProductoDto productoDto) throws ProductoNotFoundException;
}
