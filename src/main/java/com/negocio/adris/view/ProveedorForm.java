package com.negocio.adris.view;

import com.negocio.adris.utils.AdrysAlert;
import com.negocio.adris.utils.BotonAfirmar;
import com.negocio.adris.utils.BotonCancelar;
import com.negocio.adris.viewmodel.ProveedorViewModel;
import javafx.beans.binding.Bindings;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.util.converter.LongStringConverter;

public class ProveedorForm extends VBox {
    private final ProveedorViewModel proveedorViewModel;
    private final Runnable onClose;

    public ProveedorForm(ProveedorViewModel proveedorViewModel, Runnable onClose){
        this.proveedorViewModel = proveedorViewModel;
        this.onClose = onClose;
        getStyleClass().add("ProveedorForm");

        TextField idField = new TextField();
        TextField nombreField = new TextField();
        TextField telefonoField = new TextField();
        Button botonAgregar = new BotonAfirmar("Agregar");
        Button botonCancelar = new BotonCancelar();

        // id
        StringConverter<? extends Number> longConverter = new LongStringConverter();
        Bindings.bindBidirectional(idField.textProperty(), proveedorViewModel.idProperty(), (StringConverter<Number>) longConverter);
        idField.setVisible(false);

        // nombre
        nombreField.textProperty().bindBidirectional(proveedorViewModel.nombreProperty());

        // telefono
        telefonoField.textProperty().bindBidirectional(proveedorViewModel.telefonoProperty());
        telefonoField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            } else {
                return null; // ignora el cambio
            }
        }));

        Region r = new Region();
        HBox buttonHolder = new HBox(botonAgregar, r, botonCancelar);
        r.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(r, Priority.ALWAYS);

        botonAgregar.setOnAction(actionEvent -> {
            try{
                proveedorViewModel.crearProveedor();
                proveedorViewModel.limpiarFormulario();
                onClose.run();
            } catch (RuntimeException e){
                Alert a = new AdrysAlert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                a.show();
            }
        });

        botonCancelar.setOnAction(actionEvent -> {
            proveedorViewModel.limpiarFormulario();
            onClose.run();
        });

        Label titulo = new Label("Proveedor");
        titulo.getStyleClass().add("PF-titulo");

        getChildren().addAll(
                titulo,
                new Label("Nombre"), nombreField,
                new Label("Teléfono"), telefonoField,
                idField,
                buttonHolder
        );
    }
}
