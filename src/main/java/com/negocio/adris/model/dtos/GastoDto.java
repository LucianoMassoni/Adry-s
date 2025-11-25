package com.negocio.adris.model.dtos;

import com.negocio.adris.utils.onCreate;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GastoDto {
    @NotNull
    long proveedorId;
    @Future(message = "La deuda debe caducar en el futuro", groups = onCreate.class)
    private LocalDateTime fechaVencimiento;
    @PositiveOrZero
    private BigDecimal monto;
    @Size(max = 255)
    private String nota;

    public GastoDto() {
    }

    public GastoDto(long proveedorId,LocalDateTime fechaVencimiento, BigDecimal monto, String nota) {
        this.proveedorId = proveedorId;
        this.fechaVencimiento = fechaVencimiento;
        this.monto = monto;
        this.nota = nota;
    }

    public long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }
}
