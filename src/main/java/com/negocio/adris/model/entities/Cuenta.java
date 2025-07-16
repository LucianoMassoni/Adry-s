package com.negocio.adris.model.entities;

import com.negocio.adris.model.enums.TipoCuenta;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Cuenta {
    private long id;
    private TipoCuenta tipo;
    private String detalle;
    private BigDecimal debe = BigDecimal.ZERO;
    private BigDecimal haber = BigDecimal.ZERO;
    LocalDate fecha;

    public Cuenta(){}

    public Cuenta(long id, TipoCuenta tipo, String detalle, BigDecimal debe, BigDecimal haber, LocalDate fecha) {
        this.id = id;
        this.tipo = tipo;
        this.detalle = detalle;
        this.debe = debe;
        this.haber = haber;
        this.fecha = fecha;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
