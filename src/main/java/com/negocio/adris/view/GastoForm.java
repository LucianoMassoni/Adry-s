package com.negocio.adris.view;

import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.utils.AdrysAlert;
import com.negocio.adris.utils.BotonAfirmar;
import com.negocio.adris.utils.BotonCancelar;
import com.negocio.adris.utils.Formatters;
import com.negocio.adris.viewmodel.GastoViewModel;
import com.negocio.adris.viewmodel.ProveedorViewModel;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import javafx.util.converter.LongStringConverter;

import java.math.BigDecimal;

public class GastoForm extends VBox {
    private final GastoViewModel gastoViewModel;
    private final ProveedorViewModel proveedorViewModel;
    private final Runnable onClose;

    public GastoForm(GastoViewModel gastoViewModel, ProveedorViewModel proveedorViewModel, Runnable onClose){
        this.gastoViewModel = gastoViewModel;
        this.proveedorViewModel = proveedorViewModel;
        this.onClose = onClose;

        this.getStyleClass().add("gastoForm");

        TextField idField = new TextField();
        ComboBox<Proveedor> proveedorComboBox = new ComboBox<>();
        DatePicker fechaVencimientoPicker = new DatePicker();
        TextField notaField = new TextField();
        TextField montoField = new TextField();
        Button botonAgregar = new BotonAfirmar("Agregar");
        Button botonModificar = new BotonAfirmar("modificar");
        Button botonCancelar = new BotonCancelar();
        Button botonAgregarProveedor = new BotonAfirmar("+");


        // id
        StringConverter<? extends Number> longConverter = new LongStringConverter();
        Bindings.bindBidirectional(idField.textProperty(), gastoViewModel.idProperty(), (StringConverter<Number>) longConverter);
        idField.setVisible(false);

        // proveedor
        HBox proveedorBox = new HBox(proveedorComboBox, botonAgregarProveedor);
        proveedorComboBox.setItems(proveedorViewModel.getProveedores());
        proveedorComboBox.valueProperty().bindBidirectional(gastoViewModel.proveedorProperty());

        // fecha vencimiento
        fechaVencimientoPicker.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null)
                gastoViewModel.fechaVencimientoProperty().set(newV.atTime(20, 0));
        });
        gastoViewModel.fechaVencimientoProperty().addListener((obs, oldV, newV) -> {
            fechaVencimientoPicker.setValue(newV != null ? newV.toLocalDate() : null);
        });


        // nota
        notaField.textProperty().bindBidirectional(gastoViewModel.notaProperty());

        // monto
        TextFormatter<BigDecimal> montoFormatter = Formatters.bigDecimalFormatter();
        montoField.setTextFormatter(montoFormatter);
        gastoViewModel.montoProperty().bindBidirectional(montoFormatter.valueProperty());

        // botones
        botonAgregar.visibleProperty().bind(idField.textProperty().isEqualTo("0"));
        botonModificar.visibleProperty().bind(idField.textProperty().isNotEqualTo("0"));

        botonAgregar.setOnAction(actionEvent -> {
            try {
                gastoViewModel.guardarGasto();
                gastoViewModel.limpiarFormulario();
                onClose.run();
            } catch (ProveedorNotFoundException | IllegalArgumentException e) {
                Alert a = new AdrysAlert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                a.show();
            }
        });

        botonModificar.setOnAction(actionEvent -> {
            try {
                gastoViewModel.modificarGasto();
                gastoViewModel.limpiarFormulario();
                onClose.run();
            } catch (GastoNotFoundException | ProveedorNotFoundException e) {
                Alert a = new AdrysAlert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                a.show();
            }
        });

        botonCancelar.setOnAction(actionEvent -> {
            gastoViewModel.limpiarFormulario();
            onClose.run();
        });

        botonAgregarProveedor.setOnAction(actionEvent -> {
                StackPane overlay = new StackPane();
                overlay.getStyleClass().add("overlay");
                overlay.setAlignment(Pos.CENTER);

                ProveedorForm proveedorForm = new ProveedorForm(proveedorViewModel, ()-> this.getChildren().remove(overlay));

                overlay.getChildren().add(proveedorForm);
                this.getChildren().add(overlay);
        });

        Region r = new Region();
        r.setMaxWidth(Double.MAX_VALUE);
        StackPane guardarStack = new StackPane(botonAgregar, botonModificar);
        HBox buttonHolder = new HBox(guardarStack, r, botonCancelar);
        HBox.setHgrow(r, Priority.ALWAYS);

        double w = proveedorBox.getWidth();
        fechaVencimientoPicker.setMinWidth(w);
        montoField.setMinWidth(w);
        notaField.setMinWidth(w);

        this.setMaxSize(500, 300);

        getChildren().addAll(
                new Label("Proveedor"), proveedorBox,
                new Label("Fecha vencimiento"), fechaVencimientoPicker,
                new Label("Monto"), montoField,
                new Label("Nota: "), notaField,
                buttonHolder
        );
    }
}
