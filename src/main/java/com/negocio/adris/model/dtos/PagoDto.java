package com.negocio.adris.model.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class PagoDto {
    @NotNull
    private long gastoId;
    @PositiveOrZero
    private BigDecimal montoPagado;

    public PagoDto() {
    }

    public PagoDto(long gastoId, BigDecimal montoPagado) {
        this.gastoId = gastoId;
        this.montoPagado = montoPagado;
    }

    public long getGastoId() {
        return gastoId;
    }

    public void setGastoId(long gastoId) {
        this.gastoId = gastoId;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }
}
