package com.negocio.adris.model.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class VentaDto {
    @NotEmpty(message = "Para realizarse una compra se necesitan productos")
    private List<DetalleVentaDto> detalleVentaDtos;
    @NotNull(message = "Para realizar una compra se necesita la fecha y hora")
    private LocalDateTime fecha;

    public VentaDto() {}

    public VentaDto(List<DetalleVentaDto> detalleVentaDtos, LocalDateTime fecha) {
        this.detalleVentaDtos = detalleVentaDtos;
        this.fecha = fecha;
    }

    public List<DetalleVentaDto> getDetalleVentaDtos() {
        return detalleVentaDtos;
    }

    public void setDetalleVentaDtos(List<DetalleVentaDto> detalleVentaDtos) {
        this.detalleVentaDtos = detalleVentaDtos;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
