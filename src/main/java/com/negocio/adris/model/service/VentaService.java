package com.negocio.adris.model.service;

import com.negocio.adris.model.dtos.VentaDto;
import com.negocio.adris.model.entities.Venta;
import com.negocio.adris.model.exceptions.VentaNotFoundException;

import java.util.List;

public interface VentaService {
    void CrearVenta(VentaDto dto);
    void modificarVenta(VentaDto dto, long id) throws VentaNotFoundException;
    void eliminarVenta(long id) throws VentaNotFoundException;
    Venta obtenerVenta(long id) throws VentaNotFoundException;
    List<Venta> obtenerTodasLasVentas() throws VentaNotFoundException;
}
