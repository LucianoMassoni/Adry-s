package com.negocio.adris.model.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VentaDto {
    @NotEmpty(message = "Para realizarse una compra se necesitan productos")
    private List<DetalleVentaDto> detalleVentaDtos;
    private LocalDateTime fecha;
    @Positive(message = "El total de una venta no puede ser negativo.")
    private BigDecimal total;

    public VentaDto() {}

    public VentaDto(List<DetalleVentaDto> detalleVentaDtos, LocalDateTime fecha, BigDecimal total) {
        this.detalleVentaDtos = detalleVentaDtos;
        this.fecha = fecha;
        this.total = total;
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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
