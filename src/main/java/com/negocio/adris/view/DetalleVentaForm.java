package com.negocio.adris.view;

import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.enums.UnidadMedida;
import com.negocio.adris.utils.Formatters;
import com.negocio.adris.viewmodel.DetalleVentaItem;
import com.negocio.adris.viewmodel.DetalleVentaViewModel;
import com.negocio.adris.viewmodel.ProductoViewModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.util.converter.LongStringConverter;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class DetalleVentaForm extends VBox {
    private final DetalleVentaViewModel detalleVentaViewModel;
    private final ProductoViewModel productoViewModel;
    private final Consumer<DetalleVentaItem> onDetalleAgregado;
    private boolean cambioProgramatico = false;

    public DetalleVentaForm(DetalleVentaViewModel detalleVentaViewModel, ProductoViewModel productoViewModel, Consumer<DetalleVentaItem> item){
        this.detalleVentaViewModel = detalleVentaViewModel;
        this.productoViewModel = productoViewModel;
        this.onDetalleAgregado = item;

        TextField idField = new TextField();
        ComboBox<Producto> productoCardComboBox = new ComboBox<>();
        TextField cantidadField = new TextField();
        ComboBox<UnidadMedida> unidadMedidaComboBox = new ComboBox<>();
        TextField descuentoField = new TextField();

        HBox buttonHolder = new HBox();
        Button botonAgregar = new Button("Agregar");
        Button botonCancelar = new Button("cancelar");
        buttonHolder.getChildren().addAll(botonAgregar, botonCancelar);

        // id
        StringConverter<? extends Number> longConverter = new LongStringConverter();
        Bindings.bindBidirectional(idField.textProperty(), detalleVentaViewModel.idProperty(), (StringConverter<Number>) longConverter);
        idField.setVisible(false);


        // cantidad
        TextFormatter<BigDecimal> cantidadFormatter = Formatters.bigDecimalFormatter();
        cantidadFormatter.setValue(detalleVentaViewModel.cantidadProperty().getValue());
        cantidadField.setTextFormatter(cantidadFormatter);
        detalleVentaViewModel.cantidadProperty().bindBidirectional(cantidadFormatter.valueProperty());

        // unidadMedida
        unidadMedidaComboBox.valueProperty().bindBidirectional(detalleVentaViewModel.unidadMedidaProperty());
        final ObservableList<UnidadMedida> unidades = FXCollections.observableArrayList();
        unidadMedidaComboBox.setItems(unidades);

        detalleVentaViewModel.productoProperty().addListener((obs, oldProducto, newProducto) -> {
            if (newProducto == null){
                unidades.setAll();
                unidadMedidaComboBox.setDisable(true);
                detalleVentaViewModel.unidadMedidaProperty().set(null);
                return;
            }

            unidades.setAll(newProducto.getTipo().getUnidadesValidas());
            unidadMedidaComboBox.setDisable(!newProducto.esDivisible());

            if (!newProducto.esDivisible()){
                detalleVentaViewModel.unidadMedidaProperty().set(newProducto.getUnidadMedida());
            } else {
                detalleVentaViewModel.unidadMedidaProperty().set(null);
            }

        });


        // descuento
        TextFormatter<BigDecimal> descuentoFormatter = Formatters.bigDecimalFormatter();
        descuentoField.setTextFormatter(descuentoFormatter);
        detalleVentaViewModel.descuentoProperty().bindBidirectional(descuentoFormatter.valueProperty());


        // productoComboBox
        productoCardComboBox.setEditable(true);
        productoCardComboBox.setItems(productoViewModel.getProductosFiltrados());

        productoCardComboBox.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (cambioProgramatico) return ;
            // Solo ejecutar el filtro si no se está seleccionando un ítem del combo
            if (!productoCardComboBox.isFocused() || productoCardComboBox.getSelectionModel().getSelectedItem() == null) {
                Platform.runLater(() -> {
                    productoViewModel.filtroBusquedaProperty().set(newText);
                    productoCardComboBox.hide();
                    if (!productoViewModel.getProductosFiltrados().isEmpty()) {
                        productoCardComboBox.show();
                    }
                });
            }
        });

        productoCardComboBox.setConverter(new StringConverter<Producto>() {
            @Override
            public String toString(Producto producto) {
                return producto != null ? producto.getNombre() + " " + producto.getMarca() + " - " + producto.getPeso()+producto.getUnidadMedida().getSimbolo() : "";
            }

            @Override
            public Producto fromString(String string) {
                // Buscamos por nombre exacto o similar
                return productoViewModel.getProductosFiltrados().stream()
                        .filter(p -> toString(p).equals(string))
                        .findFirst()
                        .orElse(null); // o mantener el actual si querés evitar que se borre
            }
        });


        productoCardComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Producto producto, boolean empty){
                super.updateItem(producto, empty);

                if (empty || producto == null){
                    setGraphic(null);
                } else {
                    try {
                        ProductoCard card = new ProductoCard(producto);

                        setGraphic(card);
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        productoCardComboBox.valueProperty().bindBidirectional(detalleVentaViewModel.productoProperty());

        // botonAgregar
        botonAgregar.setOnAction(actionEvent -> {
            cambioProgramatico = true;
            try {
                DetalleVentaItem detalleVentaItem = detalleVentaViewModel.crearDtoActual();
                onDetalleAgregado.accept(detalleVentaItem);
            } catch (IllegalArgumentException ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
                a.setTitle("Adry's");
                a.setHeaderText("Error");
                a.show();
            }
            detalleVentaViewModel.limpiarFormulario();
            cambioProgramatico = false;
        });

        // botonCancelar
        botonCancelar.setOnAction(actionEvent -> {
            cambioProgramatico = true;
            detalleVentaViewModel.limpiarFormulario();
            cambioProgramatico = false;
        });

        this.getChildren().addAll(
                idField,
                new Label("producto:"), productoCardComboBox,
                new Label("cantidad:"), cantidadField,
                new Label("unidad medida:"), unidadMedidaComboBox,
                new Label("descuento: %"), descuentoField,
                buttonHolder
        );
    }
}
