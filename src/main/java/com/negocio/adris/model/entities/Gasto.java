package com.negocio.adris.model.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Gasto {
    private long id;
    private Proveedor proveedor;
    private LocalDateTime fechaDeudaContraida;
    private LocalDateTime fechaVencimiento;
    private BigDecimal monto;
    private String nota;
    private boolean saldado;
    private List<Pago> pagos;

    public Gasto() {
    }

    public Gasto(long id,  Proveedor proveedor, LocalDateTime fechaDeudaContraida, LocalDateTime fechaVencimiento, BigDecimal monto, String nota, boolean saldado) {
        this.id = id;
        this.proveedor = proveedor;
        this.fechaDeudaContraida = fechaDeudaContraida;
        this.fechaVencimiento = fechaVencimiento;
        this.monto = monto;
        this.nota = nota;
        this.saldado = saldado;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public LocalDateTime getFechaDeudaContraida() {
        return fechaDeudaContraida;
    }

    public void setFechaDeudaContraida(LocalDateTime fechaDeudaContraida) {
        this.fechaDeudaContraida = fechaDeudaContraida;
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

    public boolean isSaldado() {
        return saldado;
    }

    public void setSaldado(boolean saldado) {
        this.saldado = saldado;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    @Override
    public String toString() {
        return "Gasto{" +
                "id=" + id +
                ", fechaDeudaContraida=" + fechaDeudaContraida +
                ", fechaVencimiento=" + fechaVencimiento +
                ", monto=" + monto +
                ", nota='" + nota + '\'' +
                ", saldado=" + saldado +
                ", pagos=" + pagos +
                '}';
    }
}
