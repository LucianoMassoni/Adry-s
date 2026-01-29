package com.negocio.adris.model.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PagoDto {
    @NotNull
    private long gastoId;
    @PositiveOrZero
    private BigDecimal montoPagado;
    @PastOrPresent
    private LocalDate fechaDePago;

    public PagoDto() {
    }

    public PagoDto(long gastoId, BigDecimal montoPagado, LocalDate fechaDePago) {
        this.gastoId = gastoId;
        this.montoPagado = montoPagado;
        this.fechaDePago = fechaDePago;
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

    public LocalDate getFechaDePago() {
        return fechaDePago;
    }

    public void setFechaDePago(LocalDate fechaDePago) {
        this.fechaDePago = fechaDePago;
    }
}
