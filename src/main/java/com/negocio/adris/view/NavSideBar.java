package com.negocio.adris.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class NavSideBar extends VBox {
    private final List<StackPane> botones = new ArrayList<>();
    private Consumer<String> onSectionSelected;

    public void setOnSectionSelected(Consumer<String> consumer){
        this.onSectionSelected = consumer;
    }

    private StackPane buttonPane(String nombre, String identificador){
        Text text = new Text(nombre);
        StackPane pane = new StackPane(text);
        pane.getStyleClass().add("button-pane");


        pane.setOnMouseClicked(e -> {
            // Remover la clase de todos los botones
            botones.forEach(b -> b.getStyleClass().remove("activo"));

            // Agregarla solo al seleccionado
            pane.getStyleClass().add("activo");

            if (onSectionSelected != null){
                onSectionSelected.accept(identificador);
            }
        });

        return pane;
    }

    public NavSideBar(){
        this.getStyleClass().add("nav-side-bar");
        Image logo = new Image(getClass().getResource("/Logo-Test.png").toExternalForm());
        ImageView logoHolder = new ImageView(logo);
        logoHolder.setFitWidth(120);
        logoHolder.setFitHeight(50);

        Pane ventasPane = buttonPane("Ventas", "ventas");
        Pane productosPane = buttonPane("Productos", "productos");
        Pane cuentasPane = buttonPane("Cuentas", "cuentas");
        Pane dashboardPane = buttonPane("Dashboard", "dashboard");

        botones.add((StackPane) ventasPane);
        botones.add((StackPane) productosPane);
        botones.add((StackPane) cuentasPane);
        botones.add((StackPane) dashboardPane);

        getChildren().setAll(
                logoHolder,
                ventasPane,
                productosPane,
                cuentasPane,
                dashboardPane
        );
    }
}
