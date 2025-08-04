package com.negocio.adris.model.dtos;

import com.negocio.adris.model.enums.FormaDePago;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class VentaDto {
    @NotEmpty(message = "Para realizarse una compra se necesitan productos")
    private List<DetalleVentaDto> detalleVentaDtos;
    @NotNull(message = "Para realizar una compra se necesita la fecha y hora")
    private LocalDateTime fecha;
    @NotNull
    private FormaDePago formaDePago;

    public VentaDto() {}

    public VentaDto(List<DetalleVentaDto> detalleVentaDtos, LocalDateTime fecha, FormaDePago formaDePago) {
        this.detalleVentaDtos = detalleVentaDtos;
        this.fecha = fecha;
        this.formaDePago = formaDePago;
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

    public FormaDePago getFormaDePago() {
        return formaDePago;
    }

    public void setFormaDePago(FormaDePago formaDePago) {
        this.formaDePago = formaDePago;
    }
}
