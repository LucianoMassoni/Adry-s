package com.negocio.adris.model.service;

import com.negocio.adris.model.dtos.VentaDto;
import com.negocio.adris.model.entities.Venta;
import com.negocio.adris.model.exceptions.VentaNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaService {
    void crearVenta(VentaDto dto);
    void modificarVenta(VentaDto dto, long id) throws VentaNotFoundException;
    void eliminarVenta(long id) throws VentaNotFoundException;
    Venta obtenerVenta(long id) throws VentaNotFoundException;
    List<Venta> obtenerTodasLasVentas() throws VentaNotFoundException;
    List<Venta> obtenerVentasPorDia(LocalDateTime fecha) throws VentaNotFoundException;
    BigDecimal obtenerGananciaPorDia(LocalDateTime fecha) throws VentaNotFoundException;
    List<Venta> obtenerVentasPorMes(LocalDateTime fecha) throws VentaNotFoundException;
    BigDecimal obtenerGananciaPorMes(LocalDateTime fecha) throws VentaNotFoundException;
}
