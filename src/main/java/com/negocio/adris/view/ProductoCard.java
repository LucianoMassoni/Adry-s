package com.negocio.adris.view;


import com.negocio.adris.model.entities.Producto;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.function.Consumer;


public class ProductoCard extends HBox {
    private Consumer<Producto> onBorrar;
    private Consumer<Producto> onEditar;
    private boolean seleccionado = false;
    private final HBox buttonHolder;

    public void setOnBorrar(Consumer<Producto> onBorrar){
        this.onBorrar = onBorrar;
    }

    public void setOnEditar(Consumer<Producto> onEditar){
        this.onEditar = onEditar;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
        if (seleccionado) {
            if (!getStyleClass().contains("producto-card-seleccionado")) {
                getStyleClass().add("producto-card-seleccionado");
                fadeNode(buttonHolder, true);
            }
        } else {
            getStyleClass().remove("producto-card-seleccionado");
            fadeNode(buttonHolder, false);
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

    public ProductoCard(Producto producto) throws InstantiationException, IllegalAccessException {
        this.getStyleClass().add("producto-card");

        VBox infoHolder = new VBox();
        HBox infoPrincipal = new HBox();
        HBox infoSecundaria = new HBox();
        infoHolder.getChildren().addAll(infoPrincipal, infoSecundaria);

        Label nombre = new Label(producto.getNombre());
        Label marca = new Label(producto.getMarca());
        Label peso = new Label(producto.getPesoActual() + producto.getUnidadMedida().getSimbolo());
        Label precio = new Label("$" + producto.getPrecio().toString());
        Label cantidad = new Label("cantidad: " + producto.getCantidad());
        Label costo = new Label("costo: " + producto.getCosto().toString());
        Label ganancia = new Label("% ganacia: " + producto.getGanancia().toString());
        Label tipoProducto = new Label("tipo: " + producto.getTipo().toString());

        HBox.setHgrow(infoHolder, Priority.ALWAYS);
        VBox.setVgrow(infoPrincipal, Priority.ALWAYS);
        VBox.setVgrow(infoSecundaria, Priority.ALWAYS);

        infoPrincipal.setMaxWidth(Double.MAX_VALUE);
        infoSecundaria.setMaxWidth(Double.MAX_VALUE);

        infoPrincipal.getChildren().addAll( nombre,  marca,  peso,  precio);

        HBox.setHgrow(nombre, Priority.ALWAYS);
        HBox.setHgrow(marca, Priority.ALWAYS);
        HBox.setHgrow(peso, Priority.ALWAYS);
        HBox.setHgrow(precio, Priority.ALWAYS);

        nombre.setMaxWidth(Double.MAX_VALUE);
        marca.setMaxWidth(Double.MAX_VALUE);
        peso.setMaxWidth(Double.MAX_VALUE);
        precio.setMaxWidth(Double.MAX_VALUE);

        infoSecundaria.getChildren().addAll(cantidad, costo, ganancia, tipoProducto);
        HBox.setHgrow(cantidad, Priority.ALWAYS);
        HBox.setHgrow(costo, Priority.ALWAYS);
        HBox.setHgrow(ganancia, Priority.ALWAYS);
        HBox.setHgrow(tipoProducto, Priority.ALWAYS);

        cantidad.setMaxWidth(Double.MAX_VALUE);
        costo.setMaxWidth(Double.MAX_VALUE);
        ganancia.setMaxWidth(Double.MAX_VALUE);
        tipoProducto.setMaxWidth(Double.MAX_VALUE);

        infoPrincipal.getStyleClass().add("PC-info-principal");
        infoSecundaria.getStyleClass().add("PC-info-secundaria");


        Button botonEditar = new Button("Editar");
        Button botonBorrar = new Button("Borrar");

        botonEditar.setOnAction(actionEvent -> {
            if (onEditar != null) onEditar.accept(producto);
        });

        botonBorrar.setOnAction(actionEvent -> {
            if (onBorrar != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Quieres borrar el Producto?");
                alert.showAndWait()
                        .filter( response -> response == ButtonType.OK)
                        .ifPresent(response -> onBorrar.accept(producto));
            }
        });

        buttonHolder = new HBox();
        buttonHolder.getChildren().addAll(botonEditar, botonBorrar);
        buttonHolder.getStyleClass().add("PC-button-holder");
        buttonHolder.setVisible(false);
        buttonHolder.setOpacity(0);


        botonEditar.getStyleClass().add("PC-boton-editar");
        botonBorrar.getStyleClass().add("PC-boton-eliminar");

        this.getChildren().addAll(infoHolder, buttonHolder);
    }
}