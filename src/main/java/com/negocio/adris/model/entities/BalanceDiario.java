package com.negocio.adris.model.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BalanceDiario {
    private LocalDate fecha;
    private BigDecimal facturacion;
    private BigDecimal egresos;

    public BalanceDiario(LocalDate fecha, BigDecimal facturacion, BigDecimal egresos){
        this.fecha = fecha;
        this.facturacion = facturacion;
        this.egresos = egresos;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getFacturacion() {
        return facturacion;
    }

    public void setFacturacion(BigDecimal facturacion) {
        this.facturacion = facturacion;
    }

    public BigDecimal getEgresos() {
        return egresos;
    }

    public void setEgresos(BigDecimal egresos) {
        this.egresos = egresos;
    }

    public BigDecimal getDiferencia() {
        return facturacion.subtract(egresos);
    }
}
