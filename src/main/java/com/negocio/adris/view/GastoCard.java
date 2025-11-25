package com.negocio.adris.view;

import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.utils.BotonAfirmar;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class GastoCard extends HBox {
    private Consumer<Gasto> onEditar;
    private Consumer<Gasto> onPagar;
    private boolean seleccionado = false;
    private final HBox buttonHolder;

    public void setOnEditar(Consumer<Gasto> onEditar) { this.onEditar = onEditar; }

    public void setOnPagar(Consumer<Gasto> onPagar) { this.onPagar = onPagar; }

    public void setSeleccionado(boolean seleccionado) {
        if (this.seleccionado == seleccionado && seleccionado) return;

        this.seleccionado = seleccionado;

        if (seleccionado) {
            if (!getStyleClass().contains("gasto-card-seleccionado")) {
                getStyleClass().add("gasto-card-seleccionado");
                fadeNode(buttonHolder, true);
            }
        } else {
            getStyleClass().remove("gasto-card-seleccionado");
            fadeNode(buttonHolder, false);
        }
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

    private  Label fechaContraida = new Label();
    private  Label fechaSaldar = new Label();
    private  Label proveedorNombre = new Label();
    private  Label proveedorTelefono = new Label();
    private  Label nota = new Label();
    private  Label monto = new Label();

    public GastoCard(Gasto gasto){
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

        // espacios
        Region r1 = new Region();
        Region r2 = new Region();
        Region r3 = new Region();
        Region r4 = new Region();


        this.getChildren().addAll(
                new Label("ID: " + gasto.getId()),
                fechaBox,
                r1,
                proveedorBox,
                r2,
                notaBox,
                r3,
                montoBox,
                r4,
                buttonHolder
        );

        this.getChildren().forEach(chil ->{
            if (chil instanceof Region r){
                r.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(r, Priority.ALWAYS);
            }
        });
    }

    public void actualizarDatos(Gasto gasto) {
        this.gasto = gasto;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

        fechaContraida.setText(gasto.getFechaDeudaContraida().format(formatter));
        fechaSaldar.setText(gasto.getFechaVencimiento().format(formatter));
        proveedorNombre.setText(gasto.getProveedor().getNombre());
        proveedorTelefono.setText("Tel. " + gasto.getProveedor().getTelefono());
        nota.setText(gasto.getNota());
        monto.setText("$" + gasto.getMontoRestante());
    }
}
