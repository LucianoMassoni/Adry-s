package com.negocio.adris.view;


import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.utils.AdrysAlert;
import com.negocio.adris.utils.LabelNegrita;
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

    public ProductoCard(Producto producto) {
        getStyleClass().add("producto-card");

        // GRID DE INFO
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(10);
        infoGrid.setVgap(5);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.ALWAYS);
            col.setFillWidth(true);
            infoGrid.getColumnConstraints().add(col);
        }

        Label nombre = new LabelNegrita(producto.getNombre());
        Label marca = new LabelNegrita(producto.getMarca());
        Label peso = new LabelNegrita(
                (producto.getPesoActual() == 0 ? "" : producto.getPesoActual()) +
                        (producto.getUnidadMedida() == null ? "" : producto.getUnidadMedida().getSimbolo())
        );
        Label precio = new LabelNegrita("$" + (producto.getPrecio() == null ? "" : producto.getPrecio()));

        Label cantidad = new Label("cant: " + producto.getCantidad());
        Label costo = new Label("costo: " + (producto.getCosto() == null ? "" : producto.getCosto()));
        Label ganancia = new Label("% gan: " + (producto.getGanancia() == null ? "" : producto.getGanancia()));
        Label tipoProducto = new Label("tipo: " + (producto.getTipo() == null ? "" : producto.getTipo()));

        // Fila 0
        infoGrid.add(nombre, 0, 0);
        infoGrid.add(marca, 1, 0);
        infoGrid.add(peso, 2, 0);
        infoGrid.add(precio, 3, 0);

        // Fila 1
        infoGrid.add(cantidad, 0, 1);
        infoGrid.add(costo, 1, 1);
        infoGrid.add(ganancia, 2, 1);
        infoGrid.add(tipoProducto, 3, 1);

        infoGrid.getStyleClass().add("PC-info-grid");

        HBox.setHgrow(infoGrid, Priority.ALWAYS);

        // BOTONES
        Button botonEditar = new Button("Editar");
        Button botonBorrar = new Button("Borrar");

        botonEditar.setOnAction(e -> {
            if (onEditar != null) onEditar.accept(producto);
        });

        botonBorrar.setOnAction(e -> {
            if (onBorrar != null) {
                Alert alert = new AdrysAlert(
                        Alert.AlertType.CONFIRMATION,
                        "¿Quieres borrar el producto?"
                );
                alert.showAndWait()
                        .filter(r -> r == ButtonType.OK)
                        .ifPresent(r -> onBorrar.accept(producto));
            }
        });

        buttonHolder = new HBox(10, botonEditar, botonBorrar);
        buttonHolder.getStyleClass().add("PC-button-holder");
        buttonHolder.setVisible(false);
        buttonHolder.setOpacity(0);

        botonEditar.getStyleClass().add("PC-boton-editar");
        botonBorrar.getStyleClass().add("PC-boton-eliminar");

        // ROOT
        getChildren().addAll(infoGrid, buttonHolder);
    }
}