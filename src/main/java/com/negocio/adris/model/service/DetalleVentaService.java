package com.negocio.adris.model.service;

import com.negocio.adris.model.dtos.DetalleVentaDto;
import com.negocio.adris.model.entities.DetalleVenta;
import com.negocio.adris.model.exceptions.DetalleVentaNotFoundException;

import java.util.List;

public interface DetalleVentaService {
    void validarDetalleVentaDto(DetalleVentaDto dto);
    void crearDetalleVenta(DetalleVentaDto dto, long ventaId);
    void modificarDetalleVenta(DetalleVentaDto dto, long id) throws DetalleVentaNotFoundException;
    void eliminarDetalleVenta(long id) throws DetalleVentaNotFoundException;
    DetalleVenta obtenerDetalleVenta(long id) throws DetalleVentaNotFoundException;
    List<DetalleVenta> obtenerTodosLosDetalleVenta() throws DetalleVentaNotFoundException;
    List<DetalleVenta> obtenerTodosLosDetalleVentaConIdVenta(long ventaId) throws DetalleVentaNotFoundException;
}
