package com.negocio.adris.model.entities;

import com.negocio.adris.model.dtos.DetalleVentaDto;
import com.negocio.adris.model.enums.FormaDePago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Venta {
    private long id;
    private FormaDePago formaDePago;
    private LocalDateTime fecha;
    private BigDecimal total;

    public Venta(){}

    public Venta(long id, FormaDePago formaDePago, LocalDateTime fecha, BigDecimal total){
        this.id = id;
        this.formaDePago = formaDePago;
        this.fecha = fecha;
        this.total = total;
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
}
