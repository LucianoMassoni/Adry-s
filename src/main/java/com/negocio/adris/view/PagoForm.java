package com.negocio.adris.view;

import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.PagoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.utils.BotonAfirmar;
import com.negocio.adris.utils.BotonCancelar;
import com.negocio.adris.utils.Formatters;
import com.negocio.adris.viewmodel.PagoViewModel;
import javafx.beans.binding.Bindings;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.util.converter.LongStringConverter;

import java.math.BigDecimal;
import java.time.LocalTime;

public class PagoForm extends VBox {
    private final PagoViewModel pagoViewModel;
    private final Runnable onClose;

    public PagoForm(PagoViewModel pagoViewModel, Gasto gasto, Runnable onClose){
        this.pagoViewModel = pagoViewModel;
        this.onClose = onClose;

        this.getStyleClass().add("pagoForm");

        TextField idField = new TextField();
        DatePicker fechaField = new DatePicker();
        TextField montoField = new TextField();
        Button botonAgregar = new BotonAfirmar("Aceptar");
        Button botonModificar = new BotonAfirmar("Modificar");
        Button botonCancelar = new BotonCancelar();

        // id
        StringConverter<? extends Number> longConverter = new LongStringConverter();
        Bindings.bindBidirectional(idField.textProperty(), pagoViewModel.idProperty(), (StringConverter<Number>) longConverter);
        idField.setVisible(false);

        // gasto
        pagoViewModel.gastoProperty().set(gasto);

        // fecha
        fechaField.valueProperty().addListener((obs, oldV, newV)->{
            if (newV != null)
                pagoViewModel.fechaPagoProperty().set(newV.atTime(LocalTime.now()));
        });
        pagoViewModel.fechaPagoProperty().addListener((obs, oldV, newV)->{
            fechaField.setValue(newV != null ? newV.toLocalDate() : null);
        });

        // monto
        TextFormatter<BigDecimal> montoFormatter = Formatters.bigDecimalFormatter();
        montoField.setTextFormatter(montoFormatter);
        pagoViewModel.montoPagadoProperty().bindBidirectional(montoFormatter.valueProperty());

        // Botones
        botonAgregar.visibleProperty().bind(idField.textProperty().isEqualTo("0"));
        botonModificar.visibleProperty().bind(idField.textProperty().isNotEqualTo("0"));

        botonAgregar.setOnAction(actionEvent -> {
            try {
                pagoViewModel.crearPago();
                pagoViewModel.limpiarFormulario();
                onClose.run();
            } catch (GastoNotFoundException | ProveedorNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        botonModificar.setOnAction(actionEvent -> {
            try {
                pagoViewModel.modificarPago();
                pagoViewModel.limpiarFormulario();
                onClose.run();
            } catch (PagoNotFoundException | GastoNotFoundException | ProveedorNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        botonCancelar.setOnAction(actionEvent -> {
            pagoViewModel.limpiarFormulario();
            onClose.run();
        });

        Region region = new Region();
        HBox buttonHolder = new HBox(botonAgregar, botonModificar, region, botonCancelar);

        this.setMaxSize(500, 300);

        getChildren().addAll(
                idField,
                new Label("fecha:"), fechaField,
                new Label("Monto"), montoField,
                buttonHolder
                );
    }
}
