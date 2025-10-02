package com.negocio.adris.model.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pago {
    private long id;
    private Gasto gasto;
    private LocalDateTime fechaPago;
    private BigDecimal montoPagado;

    public Pago() {}

    public Pago(long id, Gasto gasto, LocalDateTime fechaPago, BigDecimal montoPagado) {
        this.id = id;
        this.gasto = gasto;
        this.fechaPago = fechaPago;
        this.montoPagado = montoPagado;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Gasto getGasto() {
        return gasto;
    }

    public void setGasto(Gasto gasto) {
        this.gasto = gasto;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    @Override
    public String toString() {
        return "Pago{" +
                "id=" + id +
                ", gasto=" + gasto +
                ", fechaPago=" + fechaPago +
                ", montoPagado=" + montoPagado +
                '}';
    }
}
