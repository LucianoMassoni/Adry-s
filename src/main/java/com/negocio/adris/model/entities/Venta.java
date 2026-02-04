package com.negocio.adris.model.entities;

import com.negocio.adris.model.enums.FormaDePago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Venta {
    private long id;
    private FormaDePago formaDePago;
    private LocalDateTime fecha;
    private BigDecimal total;
    private List<DetalleVenta> detalleVentas;

    public Venta(){}

    public Venta(long id, FormaDePago formaDePago, LocalDateTime fecha, BigDecimal total, List<DetalleVenta> detalleVentas){
        this.id = id;
        this.formaDePago = formaDePago;
        this.fecha = fecha;
        this.total = total;
        this.detalleVentas = detalleVentas;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public FormaDePago getFormaDePago() {
        return formaDePago;
    }

    public void setFormaDePago(FormaDePago formaDePago) {
        this.formaDePago = formaDePago;
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

    public List<DetalleVenta> getDetalleVentas() {
        return detalleVentas;
    }

    public void setDetalleVentas(List<DetalleVenta> detalleVentas) {
        this.detalleVentas = detalleVentas;
    }

    @Override
    public String toString() {
        return "Venta{" +
                "id=" + id +
                ", formaDePago=" + formaDePago +
                ", fecha=" + fecha +
                ", total=" + total +
                ", detalleVentas=" + detalleVentas +
                '}';
    }
}
