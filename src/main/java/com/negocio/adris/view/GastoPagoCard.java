package com.negocio.adris.view;

import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.entities.Pago;
import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class GastoPagoCard extends VBox {
    private boolean seleccionado = false;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");


    public void setSeleccionado(boolean seleccionado) {
        if (this.seleccionado == seleccionado && seleccionado) return;

        this.seleccionado = seleccionado;

        if (seleccionado) {
            if (!getStyleClass().contains("gasto-card-seleccionado")) {
                getStyleClass().add("gasto-card-seleccionado");
            }
        } else {
            getStyleClass().remove("gasto-card-seleccionado");
        }
    }

    private FadeTransition fadeActual;



    public boolean isSeleccionado() {
        return seleccionado;
    }

    private Gasto gasto;

    private Label fechaContraida = new Label();
    private  Label fechaSaldar = new Label();
    private  Label proveedorNombre = new Label();
    private  Label proveedorTelefono = new Label();
    private  Label nota = new Label();
    private  Label monto = new Label();

    public GastoPagoCard(Gasto gasto){
        this.gasto = gasto;
        this.getStyleClass().add("gasto-card");

        // fecha
        VBox fechaBox = new VBox();
        Label fechaTitulo = new Label("Fechas");
        HBox fechaTituloHolder = new HBox(fechaTitulo);
        fechaTituloHolder.getStyleClass().add("GC-tituloHolder");
        fechaTitulo.getStyleClass().add("GC-titulo");
        HBox fechasHolder = new HBox();



        VBox fechaContraidaBox = new VBox();
        Label fechaContraidaLabel = new Label("Fecha contraida");
        fechaContraidaBox.getChildren().addAll(fechaContraidaLabel, fechaContraida);

        Region rFechas = new Region();
        rFechas.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(rFechas, Priority.ALWAYS);

        VBox fechaSaladarBox = new VBox();
        Label fechaSaldarLabel = new Label("Fecha a saldar");
        fechaSaladarBox.getChildren().addAll(fechaSaldarLabel, fechaSaldar);

        fechasHolder.getChildren().addAll(fechaContraidaBox, rFechas, fechaSaladarBox);
        fechaBox.getChildren().addAll(fechaTituloHolder, fechasHolder);

        // proveedor
        VBox proveedorBox = new VBox();
        Label proveedorTitulo = new Label("Proveedor");
        HBox proveedorTituloHolder = new HBox(proveedorTitulo);
        proveedorTituloHolder.getStyleClass().add("GC-tituloHolder");
        proveedorTitulo.getStyleClass().add("GC-titulo");
        proveedorTituloHolder.setMaxWidth(Double.MAX_VALUE);
        VBox proveedorHolder = new VBox();
        proveedorHolder.getChildren().addAll(proveedorNombre, proveedorTelefono);
        proveedorHolder.getStyleClass().add("GC-contentHolder");
        proveedorHolder.setMaxWidth(Double.MAX_VALUE);
        proveedorBox.getChildren().addAll(proveedorTituloHolder, proveedorHolder);

        // nota
        VBox notaBox = new VBox();
        Label notaTitulo = new Label("Nota");
        HBox notaTituloHolder = new HBox(notaTitulo);
        notaTituloHolder.getStyleClass().add("GC-tituloHolder");
        notaTitulo.setMaxWidth(Double.MAX_VALUE);
        notaTitulo.getStyleClass().add("GC-titulo");
        HBox notaHolder = new HBox(nota);
        notaHolder.getStyleClass().add("GC-contentHolder");
        notaHolder.setMaxWidth(Double.MAX_VALUE);
        notaBox.getChildren().addAll(notaTituloHolder, notaHolder);

        // monto
        VBox montoBox = new VBox();
        Label montoTitulo = new Label("Monto");
        HBox montoTituloHolder = new HBox(montoTitulo);
        montoTituloHolder.getStyleClass().add("GC-tituloHolder");
        montoTituloHolder.setMaxWidth(Double.MAX_VALUE);
        montoTitulo.getStyleClass().add("GC-titulo");
        HBox montoHolder = new HBox(monto);
        montoHolder.getStyleClass().add("GC-contentHolder");
        montoHolder.setMaxWidth(Double.MAX_VALUE);
        montoBox.getChildren().setAll(montoTituloHolder, montoHolder);

        actualizarDatos(gasto);


        // pagoFecha
        VBox pagoFechaBox = new VBox();
        Label pagoFechaTitulo = new Label("Fecha");
        HBox pagoFechaTitulHolder = new HBox(pagoFechaTitulo);
        pagoFechaTitulHolder.getStyleClass().add("GC-tituloHolder");
        pagoFechaTitulHolder.setMaxWidth(Double.MAX_VALUE);
        pagoFechaTitulo.getStyleClass().add("GC-titulo");

        pagoFechaBox.getChildren().add(pagoFechaTitulHolder);

        // pagoMonto
        VBox pagoMontoBox = new VBox();
        Label pagoMontoTitulo = new Label("Monto pagado");
        HBox pagoMontoTituloHolder = new HBox(pagoMontoTitulo);
        pagoMontoTituloHolder.getStyleClass().add("GC-tituloHolder");
        pagoMontoTituloHolder.setMaxWidth(Double.MAX_VALUE);
        pagoMontoTitulo.getStyleClass().add("GC-titulo");

        pagoMontoBox.getChildren().add(pagoMontoTituloHolder);

        // pagoMontoRestante
        VBox pagoMontoRestanteBox = new VBox();
        Label pagoMontoRestanteTitulo = new Label("Monto pagado");
        HBox pagoMontoRestanteTituloHolder = new HBox(pagoMontoRestanteTitulo);
        pagoMontoRestanteTituloHolder.getStyleClass().add("GC-tituloHolder");
        pagoMontoRestanteTituloHolder.setMaxWidth(Double.MAX_VALUE);
        pagoMontoRestanteTitulo.getStyleClass().add("GC-titulo");

        pagoMontoRestanteBox.getChildren().add(pagoMontoRestanteTituloHolder);


        //pagobox
        Region rp1 = new Region();
        Region rp2 = new Region();
        Region rp3 = new Region();
        Region rp4 = new Region();

        Label pagoBoxTitulo = new Label("Pagos");
        HBox pagoBoxTituloHolder = new HBox(pagoBoxTitulo);
        pagoBoxTituloHolder.getStyleClass().add("GC-tituloHolder");
        pagoBoxTituloHolder.setMaxWidth(Double.MAX_VALUE);
        pagoBoxTitulo.getStyleClass().add("GC-titulo");
        HBox pagoBox = new HBox(
                rp1,
                pagoFechaBox,
                rp2,
                pagoMontoBox,
                rp3,
                pagoMontoRestanteBox,
                rp4
        );

        pagoBox.getChildren().forEach(chil ->{
            if (chil instanceof Region r){
                r.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(r, Priority.ALWAYS);
            }
        });


        // espacios
        Region r1 = new Region();
        Region r2 = new Region();
        Region r3 = new Region();

        HBox gastoBox = new HBox(
                fechaBox,
                r1,
                proveedorBox,
                r2,
                notaBox,
                r3,
                montoBox
        );

        gastoBox.getChildren().forEach(chil ->{
            if (chil instanceof Region r){
                r.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(r, Priority.ALWAYS);
            }
        });

        this.getChildren().addAll(
                gastoBox,
                pagoBoxTituloHolder,
                pagoBox
        );

        BigDecimal montoRestante = gasto.getMonto();

        for (Pago pago : gasto.getPagos()){
            // pagoFecha
            Label fecha = new Label(pago.getFechaPago().format(formatter));
            HBox pagoFechaHolder = new HBox(fecha);
            pagoFechaHolder.getStyleClass().add("GC-contentHolder");
            pagoFechaHolder.setMaxWidth(Double.MAX_VALUE);

            pagoFechaBox.getChildren().add(pagoFechaHolder);

            // pagoMonto
            Label pagoMonto = new Label("$" + pago.getMontoPagado());
            HBox pagoMontoHolder = new HBox(pagoMonto);
            pagoMontoHolder.getStyleClass().add("GC-contentHolder");
            pagoMontoHolder.setMaxWidth(Double.MAX_VALUE);

            pagoMontoBox.getChildren().add(pagoMontoHolder);

            montoRestante = montoRestante.subtract(pago.getMontoPagado());
            // pagoMontoRestante
            Label pagoMontoRestante = new Label("$" + montoRestante);
            HBox pagoMontoRestanteHolder = new HBox(pagoMontoRestante);
            pagoMontoRestanteHolder.getStyleClass().add("GC-contentHolder");
            pagoMontoRestanteHolder.setMaxWidth(Double.MAX_VALUE);

            pagoMontoRestanteBox.getChildren().add(pagoMontoRestanteHolder);
        }
    }

    public void actualizarDatos(Gasto gasto) {
        this.gasto = gasto;

        fechaContraida.setText(gasto.getFechaDeudaContraida().format(formatter));
        fechaSaldar.setText(gasto.getFechaVencimiento().format(formatter));
        proveedorNombre.setText(gasto.getProveedor().getNombre());
        proveedorTelefono.setText("Tel. " + gasto.getProveedor().getTelefono());
        nota.setText(gasto.getNota());
        monto.setText("$" + gasto.getMontoRestante());
    }
}
