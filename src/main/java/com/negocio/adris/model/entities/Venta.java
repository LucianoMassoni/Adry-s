package com.negocio.adris.model.entities;

import com.negocio.adris.model.dtos.DetalleVentaDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Venta {
    private long id;
    private LocalDateTime fecha;
    private BigDecimal total;

    public Venta(){}

    public Venta(long id, LocalDateTime fecha, BigDecimal total){
        this.id = id;
        this.fecha = fecha;
        this.total = total;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
