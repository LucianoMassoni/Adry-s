package com.negocio.adris.view;


import com.negocio.adris.model.enums.TipoProducto;
import com.negocio.adris.model.enums.UnidadMedida;
import com.negocio.adris.model.exceptions.ProductoNotFoundException;
import com.negocio.adris.utils.AdrysAlert;
import com.negocio.adris.utils.Formatters;
import com.negocio.adris.viewmodel.ProductoViewModel;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;

import java.math.BigDecimal;

public class ProductoForm extends VBox {
    private final ProductoViewModel viewModel;

    public ProductoForm(ProductoViewModel viewModel){
        this.viewModel = viewModel;

        TextField idField = new TextField();
        TextField nombreField = new TextField();
        TextField marcaField = new TextField();
        TextField pesoField = new TextField();
        TextField pesoActualField = new TextField();
        ComboBox<UnidadMedida> unidadMedidaComboBox = new ComboBox<>();
        TextField cantidadField = new TextField();
        TextField costoField = new TextField();
        TextField gananciaField = new TextField();
        TextField precioSugeridoField = new TextField();
        TextField precioField = new TextField();
        ComboBox<TipoProducto> tipoProductoComboBox = new ComboBox<>();
        CheckBox esDivisibleBox = new CheckBox("es divisible");

        // id
        StringConverter<? extends Number> longConverter = new LongStringConverter();
        Bindings.bindBidirectional(idField.textProperty(), viewModel.idProperty(), (StringConverter<Number>) longConverter);
        idField.setVisible(false);

        // nombre
        nombreField.textProperty().bindBidirectional(viewModel.nombreProperty());

        // marca
        marcaField.textProperty().bindBidirectional(viewModel.marcaProperty());

        // peso
        StringConverter<? extends Number> doubleConverter = new DoubleStringConverter();
        Bindings.bindBidirectional(pesoField.textProperty(), viewModel.pesoProperty(), (StringConverter<Number>) doubleConverter);
        pesoField.setMaxWidth(50);

        // peso actual
        StringConverter<? extends Number> doubleConverter1 = new DoubleStringConverter();
        Bindings.bindBidirectional(pesoActualField.textProperty(), viewModel.pesoACtualProperty(), (StringConverter<Number>) doubleConverter1);
        pesoActualField.setMaxWidth(50);

        // unidad medida
        unidadMedidaComboBox.setItems(FXCollections.observableArrayList(UnidadMedida.values()));
        unidadMedidaComboBox.valueProperty().bindBidirectional(viewModel.unidadMedidaProperty());

        // cantidad
        StringConverter<? extends Number> integerConverter = new IntegerStringConverter();
        Bindings.bindBidirectional(cantidadField.textProperty(), viewModel.cantidadProperty(), (StringConverter<Number>) integerConverter);

        // costo
        TextFormatter<BigDecimal> costoFormatter = Formatters.bigDecimalFormatter();
        costoField.setTextFormatter(costoFormatter);
        viewModel.costoProperty().bindBidirectional(costoFormatter.valueProperty());

        // ganancia
        TextFormatter<BigDecimal> gananciaFormatter = Formatters.bigDecimalFormatter();
        gananciaField.setTextFormatter(gananciaFormatter);
        viewModel.gananciaProperty().bindBidirectional(gananciaFormatter.valueProperty());

        // precio
        TextFormatter<BigDecimal> precioFormatter = Formatters.bigDecimalFormatter();
        precioField.setTextFormatter(precioFormatter);
        viewModel.precioProperty().bindBidirectional(precioFormatter.valueProperty());

        // es divisible
        esDivisibleBox.selectedProperty().bindBidirectional(viewModel.esDivisibleProperty());

        // PrecioSugerido
        TextFormatter<BigDecimal> precioSugeridoFormatter = Formatters.bigDecimalFormatter();
        precioSugeridoField.setTextFormatter(precioSugeridoFormatter);
        precioSugeridoFormatter.valueProperty().bind(viewModel.precioSugeridoProperty());
        precioSugeridoField.setDisable(true);
        precioSugeridoField.setVisible(false);

        // tipo producto
        tipoProductoComboBox.setItems(FXCollections.observableArrayList(TipoProducto.values()));
        tipoProductoComboBox.valueProperty().bindBidirectional(viewModel.tipoProperty());

        pesoActualField.setDisable(true);
        esDivisibleBox.selectedProperty().addListener(var -> {
                pesoActualField.setDisable(!esDivisibleBox.isSelected());
        });


        Button botonCrear = new Button("crear producto");
        Button botonModificar = new Button("Modificar");
        Button botonCancelar = new Button("cancelar");

        Region region = new Region();

        StackPane guardarStack = new StackPane(botonCrear, botonModificar);

        botonCrear.visibleProperty().bind(idField.textProperty().isEqualTo("0"));
        botonCrear.managedProperty().bind(botonCrear.visibleProperty());

        botonModificar.visibleProperty().bind(idField.textProperty().isNotEqualTo("0"));
        botonModificar.managedProperty().bind(botonModificar.visibleProperty());

        HBox buttonContainer = new HBox(guardarStack, region, botonCancelar);

        HBox.setHgrow(region, Priority.ALWAYS);
        region.setMaxWidth(Double.MAX_VALUE);

        botonCrear.setOnAction(e -> {
            try {
                viewModel.guardarProducto();
            } catch (ProductoNotFoundException | IllegalArgumentException ex) {
                Alert a = new AdrysAlert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
                a.show();
            }
        });

        botonModificar.setOnAction(e -> {
            try {
                viewModel.modificarProducto();
            } catch (ProductoNotFoundException | IllegalArgumentException ex) {
                Alert a = new AdrysAlert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
                a.show();
            }
        });

        pesoField.textProperty().addListener( (obs, oldValue, newValue) -> {
            if (newValue != null) pesoActualField.textProperty().set(pesoField.textProperty().get());
        });

        precioSugeridoField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null){
                precioField.textProperty().set(precioSugeridoField.textProperty().get());
            }
        });

        tipoProductoComboBox.valueProperty().addListener((obs, oldTipo, newTipo) -> {
            if (newTipo != null) {
                unidadMedidaComboBox.setItems(
                        FXCollections.observableArrayList(newTipo.getUnidadesValidas())
                );
            }
        });

        botonCancelar.setOnAction(e -> {
            viewModel.limpiarFormulario();
            pesoActualField.setDisable(true);
        });

        Region separador = new Region();
        HBox pesoContainer = new HBox(new Label("Peso:"), pesoField, separador, new Label("Peso actual:"), pesoActualField);
        pesoContainer.getStyleClass().add("PF-pesoContainer");
        HBox.setHgrow(separador, Priority.ALWAYS);
        separador.setMaxWidth(Double.MAX_VALUE);

        this.getStyleClass().add("productoForm");

        getChildren().addAll(
                idField,
                new Label("Nombre:"), nombreField,
                new Label("Marca:"), marcaField,
                esDivisibleBox,
                pesoContainer,
                new Label("Tipo de producto:"), tipoProductoComboBox,
                new Label("Unidad de medida:"), unidadMedidaComboBox,
                new Label("Cantidad:"), cantidadField,
                new Label("Costo:"), costoField,
                new Label("Ganancia:"), gananciaField,
                new Label("Precio:"), precioField,
                precioSugeridoField,
                buttonContainer
        );
    }
}