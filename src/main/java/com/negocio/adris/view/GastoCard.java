package com.negocio.adris.view;

import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.entities.Pago;
import com.negocio.adris.utils.BotonAfirmar;
import com.negocio.adris.utils.Utils;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class GastoCard extends VBox {
    private Consumer<Gasto> onEditar;
    private Consumer<Gasto> onPagar;
    private boolean seleccionado = false;
    private final HBox buttonHolder;
    private boolean modoLectura;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");


    public void setOnEditar(Consumer<Gasto> onEditar) { this.onEditar = onEditar; }

    public void setOnPagar(Consumer<Gasto> onPagar) { this.onPagar = onPagar; }

    public void setSeleccionado(boolean seleccionado) {
        if (this.seleccionado == seleccionado && seleccionado) return;

        this.seleccionado = seleccionado;

        if (seleccionado) {
            if (!getStyleClass().contains("gasto-card-seleccionado")) {
                getStyleClass().add("gasto-card-seleccionado");
                if (modoLectura) {
                    buttonHolder.setVisible(false);
                } else {
                    fadeNode(buttonHolder, true);
                }
                construirPago();
                expandir();
            }
        } else {
            getStyleClass().remove("gasto-card-seleccionado");
            if (modoLectura) {
                buttonHolder.setVisible(false);
            } else {
                fadeNode(buttonHolder, false);
            }
            colapsar();
        }

    }

    private void expandir() {
        pagoBox.setManaged(true);
        pagoBox.setVisible(true);
        pagoBoxTituloHolder.setManaged(true);
        pagoBoxTituloHolder.setVisible(true);

        FadeTransition ft = new FadeTransition(Duration.millis(200), pagoBox);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void colapsar() {
        FadeTransition ft = new FadeTransition(Duration.millis(150), pagoBox);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            pagoBox.setVisible(false);
            pagoBox.setManaged(false);
            pagoBoxTituloHolder.setManaged(false);
            pagoBoxTituloHolder.setVisible(false);
        });
        ft.play();
    }

    private FadeTransition fadeActual;

    private void fadeNode(Node node, boolean fadeIn) {
        if (fadeActual != null) fadeActual.stop(); // corta el fade anterior
        
        fadeActual = new FadeTransition(Duration.millis(200), node);
        if (fadeIn) {
            node.setVisible(true);
            fadeActual.setFromValue(0.0);
            fadeActual.setToValue(1.0);
            fadeActual.setOnFinished(null);
        } else {
            fadeActual.setFromValue(1.0);
            fadeActual.setToValue(0.0);
            fadeActual.setOnFinished(e -> node.setVisible(false));
        }
        fadeActual.play();
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    private Gasto gasto;

    private Label fechaContraida = new Label();
    private Label fechaSaldar = new Label();
    private Label proveedorNombre = new Label();
    private Label proveedorTelefono = new Label();
    private Label nota = new Label();
    private Label monto = new Label();
    private Label montoRestante = new Label();

    private VBox pagoFechaBox = new VBox();
    private VBox pagoMontoBox = new VBox();
    private VBox pagoMontoRestanteBox = new VBox();
    private HBox pagoBox = new HBox();
    private HBox pagoBoxTituloHolder = new HBox();



    public GastoCard(Gasto gasto, boolean modoLectura){
        this.gasto = gasto;
        this.modoLectura = modoLectura;
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
        nota.setWrapText(true);
        nota.setMaxWidth(120);

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

        // montoRestante
        VBox montoRestanteBox = new VBox();
        Label montoRestanteTitulo = new Label("Monto restante");
        HBox montoRestanteTituloHolder = new HBox(montoRestanteTitulo);
        montoRestanteTituloHolder.getStyleClass().add("GC-tituloHolder");
        montoRestanteTituloHolder.setMaxWidth(Double.MAX_VALUE);
        montoRestanteTitulo.getStyleClass().add("GC-titulo");
        HBox montoRestanteHolder = new HBox(montoRestante);
        montoRestanteHolder.getStyleClass().add("GC-contentHolder");
        montoRestanteHolder.setMaxWidth(Double.MAX_VALUE);
        montoRestanteBox.getChildren().setAll(montoRestanteTituloHolder, montoRestanteHolder);

        actualizarDatos(gasto);

        // botones
        Region rBotones = new Region();
        Button botonEditar = new BotonAfirmar("Editar");
        Button botonPagar = new BotonAfirmar("Pagar");

        buttonHolder = new HBox();
        buttonHolder.getChildren().addAll(botonEditar, rBotones, botonPagar);
        rBotones.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(rBotones, Priority.ALWAYS);

        botonEditar.setOnAction(actionEvent -> {
            if (onEditar != null) {
                onEditar.accept(gasto);
            }
        });

        botonPagar.setOnAction(actionEvent -> {
            if (onPagar != null) {
                onPagar.accept(gasto);
            }
        });

        // pagoFecha
        Label pagoFechaTitulo = new Label("Fecha");
        HBox pagoFechaTitulHolder = new HBox(pagoFechaTitulo);
        pagoFechaTitulHolder.getStyleClass().add("GC-tituloHolder");
        pagoFechaTitulHolder.setMaxWidth(Double.MAX_VALUE);
        pagoFechaTitulo.getStyleClass().add("GC-titulo");

        pagoFechaBox.getChildren().add(pagoFechaTitulHolder);

        // pagoMonto
        Label pagoMontoTitulo = new Label("Monto pagado");
        HBox pagoMontoTituloHolder = new HBox(pagoMontoTitulo);
        pagoMontoTituloHolder.getStyleClass().add("GC-tituloHolder");
        pagoMontoTituloHolder.setMaxWidth(Double.MAX_VALUE);
        pagoMontoTitulo.getStyleClass().add("GC-titulo");

        pagoMontoBox.getChildren().add(pagoMontoTituloHolder);

        // pagoMontoRestante
        Label pagoMontoRestanteTitulo = new Label("Monto restante");
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
        pagoBoxTituloHolder.getChildren().add(pagoBoxTitulo);
        pagoBoxTituloHolder.getStyleClass().add("GC-tituloHolder");
        pagoBoxTituloHolder.setMaxWidth(Double.MAX_VALUE);
        pagoBoxTitulo.getStyleClass().add("GC-titulo");

        pagoBox.getChildren().addAll(
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
        Region r4 = new Region();
        Region r5 = new Region();

        HBox r6 = new HBox();
        r6.setMaxWidth(Double.MAX_VALUE);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(r6, buttonHolder);


        HBox gastoBox = new HBox(
                fechaBox,
                r1,
                proveedorBox,
                r2,
                notaBox,
                r3,
                montoBox,
                r4,
                montoRestanteBox,
                r5,
                stackPane
        );

        gastoBox.getChildren().forEach(chil ->{
            if (chil instanceof Region r){
                r.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(r, Priority.ALWAYS);
            }
        });

        r6.setMinWidth(buttonHolder.getWidth());


        pagoBoxTituloHolder.setVisible(false);
        pagoBoxTituloHolder.setManaged(false);
        pagoBox.setVisible(false);
        pagoBox.setManaged(false);

        this.getChildren().addAll(
                gastoBox,
                pagoBoxTituloHolder,
                pagoBox
        );
    }

    public void actualizarDatos(Gasto gasto) {
        this.gasto = gasto;

        fechaContraida.setText(gasto.getFechaDeudaContraida().format(formatter));
        fechaSaldar.setText(gasto.getFechaVencimiento().format(formatter));
        proveedorNombre.setText(gasto.getProveedor().getNombre());
        proveedorTelefono.setText("Tel. " + gasto.getProveedor().getTelefono());
        nota.setText(gasto.getNota());
        monto.setText("$" + Utils.bigDecimalFormatter(gasto.getMonto()));
        montoRestante.setText("$" + Utils.bigDecimalFormatter(gasto.getMontoRestante()));
    }

    private void construirPago(){
        BigDecimal montoRestante = gasto.getMonto();

        for (Pago pago : gasto.getPagos()){
            // pagoFecha
            Label fecha = new Label(pago.getFechaPago().format(formatter));
            HBox pagoFechaHolder = new HBox(fecha);
            pagoFechaHolder.getStyleClass().add("GC-contentHolder");
            pagoFechaHolder.setMaxWidth(Double.MAX_VALUE);

            pagoFechaBox.getChildren().add(pagoFechaHolder);

            // pagoMonto
            Label pagoMonto = new Label("$" + Utils.bigDecimalFormatter(pago.getMontoPagado()));
            HBox pagoMontoHolder = new HBox(pagoMonto);
            pagoMontoHolder.getStyleClass().add("GC-contentHolder");
            pagoMontoHolder.setMaxWidth(Double.MAX_VALUE);

            pagoMontoBox.getChildren().add(pagoMontoHolder);

            montoRestante = montoRestante.subtract(pago.getMontoPagado());
            // pagoMontoRestante
            Label pagoMontoRestante = new Label("$" + Utils.bigDecimalFormatter(montoRestante));
            HBox pagoMontoRestanteHolder = new HBox(pagoMontoRestante);
            pagoMontoRestanteHolder.getStyleClass().add("GC-contentHolder");
            pagoMontoRestanteHolder.setMaxWidth(Double.MAX_VALUE);

            pagoMontoRestanteBox.getChildren().add(pagoMontoRestanteHolder);
        }
    }
}
