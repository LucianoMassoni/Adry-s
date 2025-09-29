package com.negocio.adris.view;


import com.negocio.adris.model.enums.TipoProducto;
import com.negocio.adris.model.enums.UnidadMedida;
import com.negocio.adris.model.exceptions.ProductoNotFoundException;
import com.negocio.adris.utils.AdrysAlert;
import com.negocio.adris.utils.BotonAfirmar;
import com.negocio.adris.utils.BotonCancelar;
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
        nombreField.setPromptText("nombre");

        // marca
        marcaField.textProperty().bindBidirectional(viewModel.marcaProperty());
        marcaField.setPromptText("marca");

        // peso
        StringConverter<? extends Number> doubleConverter = new DoubleStringConverter();
        Bindings.bindBidirectional(pesoField.textProperty(), viewModel.pesoProperty(), (StringConverter<Number>) doubleConverter);
//        pesoField.setMaxWidth(50);
        pesoField.setText(null);
        pesoField.setPromptText("0,0");


        // unidad medida
        unidadMedidaComboBox.setItems(FXCollections.observableArrayList(UnidadMedida.values()));
        unidadMedidaComboBox.valueProperty().bindBidirectional(viewModel.unidadMedidaProperty());

        // cantidad
        StringConverter<? extends Number> integerConverter = new IntegerStringConverter();
        Bindings.bindBidirectional(cantidadField.textProperty(), viewModel.cantidadProperty(), (StringConverter<Number>) integerConverter);
        cantidadField.setText(null);

        // costo
        TextFormatter<BigDecimal> costoFormatter = Formatters.bigDecimalFormatter();
        costoField.setTextFormatter(costoFormatter);
        viewModel.costoProperty().bindBidirectional(costoFormatter.valueProperty());
        costoField.setPromptText("0");

        // ganancia
        TextFormatter<BigDecimal> gananciaFormatter = Formatters.bigDecimalFormatter();
        gananciaField.setTextFormatter(gananciaFormatter);
        viewModel.gananciaProperty().bindBidirectional(gananciaFormatter.valueProperty());
        gananciaField.setPromptText("0");

        // precio
        TextFormatter<BigDecimal> precioFormatter = Formatters.bigDecimalFormatter();
        precioField.setTextFormatter(precioFormatter);
        viewModel.precioProperty().bindBidirectional(precioFormatter.valueProperty());
        precioField.setPromptText("0");

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

        esDivisibleBox.selectedProperty().addListener(var -> {
                pesoField.setDisable(esDivisibleBox.isSelected());
                unidadMedidaComboBox.setDisable(esDivisibleBox.isSelected());
                cantidadField.setDisable(esDivisibleBox.isSelected());
                costoField.setDisable(esDivisibleBox.isSelected());
                gananciaField.setDisable(esDivisibleBox.isSelected());
                precioField.setDisable(esDivisibleBox.isSelected());
                tipoProductoComboBox.setDisable(esDivisibleBox.isSelected());
        });


        Button botonCrear = new BotonAfirmar("Crear");
        Button botonModificar = new BotonAfirmar("Modificar");
        Button botonCancelar = new BotonCancelar();

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
        });

        this.getStyleClass().add("productoForm");

        getChildren().addAll(
                idField,
                new Label("Nombre:"), nombreField,
                new Label("Marca:"), marcaField,
                esDivisibleBox,
                new Label("Peso:"), pesoField,
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