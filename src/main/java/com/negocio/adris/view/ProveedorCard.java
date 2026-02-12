package com.negocio.adris.view;

import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.utils.BotonAfirmar;
import com.negocio.adris.utils.LabelNegrita;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.function.Consumer;

public class ProveedorCard extends HBox {
    private Proveedor proveedor;
    private Consumer<Proveedor> onEditar;
    private Consumer<Proveedor> onEliminar;
    private boolean seleccionado = false;

    private HBox buttonHolder = new HBox();

    public void setOnEditar(Consumer<Proveedor> onEditar) {
        this.onEditar = onEditar;
    }

    public void setOnEliminar(Consumer<Proveedor> onEliminar){
        this.onEliminar = onEliminar;
    }

    public void setSeleccionado(boolean seleccionado){
        if (this.seleccionado == seleccionado && seleccionado) return;

        this.seleccionado = seleccionado;

        if (seleccionado){
            if (!getStyleClass().contains("card-seleccionado")){
                getStyleClass().add("card-seleccionado");
                fadeNode(buttonHolder, true);
            }
        }else {
            getStyleClass().remove("card-seleccionado");
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

    private Label nombre = new LabelNegrita();
    private Label telefono = new Label();

    public ProveedorCard(Proveedor proveedor){
        this.proveedor = proveedor;
        this.getStyleClass().add("card");

        VBox infoBox = new VBox(nombre, telefono);

        Button botonEditar = new BotonAfirmar("editar");
        Button botonEliminar = new BotonAfirmar("Eliminar");
        Region r = new Region();
        r.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(r, Priority.ALWAYS);
        buttonHolder.getChildren().addAll(botonEditar, r, botonEliminar);

        botonEditar.setOnAction(e -> {
            if (e != null){
                onEditar.accept(proveedor);
            }
        });

        botonEliminar.setOnAction(e->{
            if (e != null){
                onEliminar.accept(proveedor);
            }
        });

        Region r1 = new Region();
        r1.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(r1, Priority.ALWAYS);
        this.getChildren().addAll(
                infoBox,
                r1,
                buttonHolder
        );
        buttonHolder.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(buttonHolder, Priority.ALWAYS);
    }

    public void actualizarProveedor(Proveedor p){
        this.proveedor = p;

        nombre.setText(p.getNombre());
        telefono.setText(p.getTelefono());
    }
}
