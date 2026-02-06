package com.negocio.adris.view;

import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.utils.AdrysAlert;
import com.negocio.adris.utils.BotonAfirmar;
import com.negocio.adris.utils.BotonCancelar;
import com.negocio.adris.utils.Formatters;
import com.negocio.adris.viewmodel.ProveedorViewModel;
import javafx.beans.binding.Bindings;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import javafx.util.converter.LongStringConverter;

public class ProveedorForm extends VBox {
    private final ProveedorViewModel proveedorViewModel;
    private final Runnable onClose;

    public ProveedorForm(Proveedor proveedor, ProveedorViewModel proveedorViewModel, Runnable onClose){
        this.proveedorViewModel = proveedorViewModel;
        this.onClose = onClose;
        getStyleClass().add("ProveedorForm");

        TextField idField = new TextField();
        TextField nombreField = new TextField();
        TextField telefonoField = new TextField();
        Button botonAgregar = new BotonAfirmar("Agregar");
        Button botonModificar = new BotonAfirmar("Modificar");
        Button botonCancelar = new BotonCancelar();

        // id
        StringConverter<? extends Number> longConverter = new LongStringConverter();
        Bindings.bindBidirectional(idField.textProperty(), proveedorViewModel.idProperty(), (StringConverter<Number>) longConverter);
        idField.setVisible(false);

        // nombre
        nombreField.textProperty().bindBidirectional(proveedorViewModel.nombreProperty());

        // telefono
        TextFormatter<String> telefonoFormatter = Formatters.telefonoFormatter();
        telefonoField.setTextFormatter(telefonoFormatter);
        proveedorViewModel.telefonoProperty().bindBidirectional(telefonoFormatter.valueProperty());


        telefonoField.textProperty().bindBidirectional(proveedorViewModel.telefonoProperty());


        Region r = new Region();
        StackPane botonesAfirmativosHolder = new StackPane(botonAgregar, botonModificar);
        botonAgregar.visibleProperty().bind(idField.textProperty().isEqualTo("0"));
        botonAgregar.managedProperty().bind(botonAgregar.visibleProperty());

        botonModificar.visibleProperty().bind(idField.textProperty().isNotEqualTo("0"));
        botonModificar.managedProperty().bind(botonModificar.visibleProperty());

        HBox buttonHolder = new HBox(botonesAfirmativosHolder, r, botonCancelar);
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

        botonModificar.setOnAction(actionEvent -> {
            try {
                proveedorViewModel.modificarProveedor();
                proveedorViewModel.limpiarFormulario();
                onClose.run();
            } catch (ProveedorNotFoundException e) {
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

        setMaxWidth(Region.USE_PREF_SIZE);
        setPrefWidth(Region.USE_COMPUTED_SIZE);
        setMaxHeight(Region.USE_PREF_SIZE);
        setPrefHeight(Region.USE_COMPUTED_SIZE);
    }
}
