package com.negocio.adris.view;


import com.negocio.adris.model.enums.UnidadMedida;
import com.negocio.adris.utils.AdrysAlert;
import com.negocio.adris.viewmodel.DetalleVentaItem;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.util.function.Consumer;

public class DetalleVentaCard extends HBox {
    private Consumer<DetalleVentaItem> onBorrar;
    private Consumer<DetalleVentaItem> onAgregar;
    private Consumer<DetalleVentaItem> onSacar;
    private boolean seleccionado = false;
    private final Button botonBorrar = new Button();
    private final Button botonMenosCantidad = new Button("-");
    private final Button botonMasCantidad = new Button("+");


    public void setOnBorrar(Consumer<DetalleVentaItem> onBorrar) {
        this.onBorrar = onBorrar;
    }

    public void setOnAgregar(Consumer<DetalleVentaItem> onAgregar) {
        this.onAgregar = onAgregar;
    }

    public void setOnSacar(Consumer<DetalleVentaItem> onSacar) {
        this.onSacar = onSacar;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
        if (seleccionado) {
            if (!getStyleClass().contains("detalleVenta-card-seleccionado")) {
                getStyleClass().add("detalleVenta-card-seleccionado");
                fadeNode(botonBorrar, true);
                fadeNode(botonMenosCantidad, true);
                fadeNode(botonMasCantidad, true);
            }
        } else {
            getStyleClass().remove("detalleVenta-card-seleccionado");
            fadeNode(botonBorrar, false);
            fadeNode(botonMenosCantidad, false);
            fadeNode(botonMasCantidad, false);
        }
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    private void fadeNode(Node node, boolean fadeIn) {
        FadeTransition fade = new FadeTransition(Duration.millis(200), node);
        if (fadeIn) {
            node.setVisible(true);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
        } else {
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            // Ocultar al terminar la animación
            fade.setOnFinished(e -> node.setVisible(false));
        }
        fade.play();
    }

    public DetalleVentaCard(DetalleVentaItem item){
        getStyleClass().add("detalleVenta-card");

        HBox info = new HBox();

        botonMenosCantidad.setVisible(false);
        botonMasCantidad.setVisible(false);
        botonBorrar.setVisible(false);
        botonMenosCantidad.setOpacity(0);
        botonMasCantidad.setOpacity(0);
        botonBorrar.setOpacity(0);

        Label nombre = new Label();
        Label marca = new Label();
        Label peso = new Label();
        Label cantidad = new Label();
        Label descuento = new Label();
        Label precio = new Label();
        Label subtotal =  new Label();

        nombre.textProperty().bind(
                Bindings.selectString(item.productoProperty(), "nombre")
        );

        marca.textProperty().bind(
                Bindings.selectString(item.productoProperty(), "marca")
        );

        peso.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            var producto = item.productoProperty().get();
                            if (producto == null) return "";
                            return producto.getPeso() + " " + (producto.getUnidadMedida() == null ? "" : producto.getUnidadMedida().getSimbolo());
                        },
                        item.productoProperty()
                )
        );

        cantidad.textProperty().bind(item.cantidadProperty().asString());

        precio.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            var p = item.precioProperty().get();
                            if (p == null) return "";
                            return p.toString();
                        },
                        item.precioProperty()
                )
        );

        descuento.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            var d = item.descuentoProperty().get();
                            return d != null ? "%" + d.toPlainString() : "0.00";
                        },
                        item.descuentoProperty()
                )
        );

        subtotal.textProperty().bind(
                Bindings.createStringBinding(
                        () -> {
                            var s = item.subtotalProperty().get();
                            return s != null ? "$" + s.toPlainString() : "$0.00";
                        },
                        item.subtotalProperty()
                )
        );

        HBox cantidadHolder = new HBox();
        cantidadHolder.getChildren().addAll(botonMenosCantidad, cantidad, botonMasCantidad);

        HBox.setHgrow(info, Priority.ALWAYS);
        info.setMaxWidth(Double.MAX_VALUE);

        Region region = new Region();
        region.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(region, Priority.ALWAYS);

        info.getChildren().addAll(
                nombre,
                marca,
                peso,
                item.productoProperty().get().esDivisible() ? region : cantidadHolder,
                descuento,
                subtotal,
                botonBorrar
        );

        HBox.setHgrow(nombre, Priority.ALWAYS);
        HBox.setHgrow(marca, Priority.ALWAYS);
        HBox.setHgrow(peso, Priority.ALWAYS);
        HBox.setHgrow(cantidadHolder, Priority.ALWAYS);

        HBox.setHgrow(descuento, Priority.ALWAYS);
        HBox.setHgrow(subtotal, Priority.ALWAYS);

        nombre.setMaxWidth(Double.MAX_VALUE);
        marca.setMaxWidth(Double.MAX_VALUE);
        peso.setMaxWidth(Double.MAX_VALUE);
        cantidad.setMaxWidth(Double.MAX_VALUE);
        descuento.setMaxWidth(Double.MAX_VALUE);
        subtotal.setMaxWidth(Double.MAX_VALUE);

        Image borrarIcon = new Image(getClass().getResource("/basura.png").toExternalForm());
        ImageView borrarIconIV = new ImageView(borrarIcon);

        borrarIconIV.setFitWidth(24);
        borrarIconIV.setFitHeight(24);

        // Mantener proporción
        borrarIconIV.setPreserveRatio(true);

        botonBorrar.setGraphic(borrarIconIV);
        botonBorrar.getStyleClass().add("DVC-botonBorrar");
        botonBorrar.setOnAction(actionEvent -> {
            if (onBorrar != null){
                Alert a = new AdrysAlert(Alert.AlertType.CONFIRMATION, "quiere borrar el producto?");
                a.showAndWait()
                        .filter(response -> response == ButtonType.OK)
                        .ifPresent(response -> onBorrar.accept(item));
            }
        });

        botonMenosCantidad.setOnAction(actionEvent -> {
            if (onSacar != null){
                onSacar.accept(item);
            }
        });

        botonMasCantidad.setOnAction(actionEvent -> {
            if (onAgregar != null){
                onAgregar.accept(item);
            }
        });

        getChildren().add(info);
    }
}
