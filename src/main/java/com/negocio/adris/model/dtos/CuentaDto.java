package com.negocio.adris.model.dtos;

import com.negocio.adris.model.enums.TipoCuenta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CuentaDto {
    @NotNull(message = "La cuenta necesita un tipo")
    private TipoCuenta tipo;
    @NotBlank(message = "Necesita un detalle")
    private String detalle;
    @PositiveOrZero(message = "El debe no puede ser negativo")
    private BigDecimal debe;
    @PositiveOrZero(message = "El haber no puede ser negativo")
    private BigDecimal haber;
    @NotNull
    private LocalDate fecha;

    public CuentaDto() {
    }

    public CuentaDto(TipoCuenta tipo, String detalle, BigDecimal debe, BigDecimal haber, LocalDate fecha) {
        this.tipo = tipo;
        this.detalle = detalle;
        this.debe = debe;
        this.haber = haber;
        this.fecha = fecha;
    }

    public TipoCuenta getTipo() {
        return tipo;
    }

    public void setTipo(TipoCuenta tipo) {
        this.tipo = tipo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public BigDecimal getDebe() {
        return debe;
    }

    public void setDebe(BigDecimal debe) {
        this.debe = debe;
    }

    public BigDecimal getHaber() {
        return haber;
    }

    public void setHaber(BigDecimal haber) {
        this.haber = haber;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
