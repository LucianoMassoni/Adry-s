package com.negocio.adris.view;

import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.utils.AdrysAlert;
import com.negocio.adris.utils.BotonAfirmar;
import com.negocio.adris.utils.BotonCancelar;
import com.negocio.adris.utils.Formatters;
import com.negocio.adris.viewmodel.DetalleVentaItem;
import com.negocio.adris.viewmodel.ProductoViewModel;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class DetalleVentaForm extends VBox {
    private final ProductoViewModel productoViewModel;
    private final Consumer<DetalleVentaItem> onDetalleAgregado;
    private boolean cambioProgramatico = false;

    private final DetalleVentaItem detalleVentaItem = new DetalleVentaItem();

    public DetalleVentaForm(ProductoViewModel productoViewModel, Consumer<DetalleVentaItem> ConsumerItem){
        this.productoViewModel = productoViewModel;
        this.onDetalleAgregado = ConsumerItem;

        getStyleClass().add("DetalleVentaForm");

        ComboBox<Producto> productoCardComboBox = new ComboBox<>();
        TextField cantidadField = new TextField();
        TextField precioField = new TextField();
        TextField descuentoField = new TextField();

        HBox buttonHolder = new HBox(15);
        Region botonRegion = new Region();
        botonRegion.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(botonRegion, Priority.ALWAYS);
        Button botonAgregar = new BotonAfirmar("Agregar");
        Button botonCancelar = new BotonCancelar();
        buttonHolder.getChildren().addAll(botonAgregar, botonRegion, botonCancelar);


        // cantidad
        TextFormatter<BigDecimal> cantidadFormatter = Formatters.bigDecimalFormatter();
        cantidadFormatter.setValue(detalleVentaItem.cantidadProperty().getValue());
        cantidadField.setTextFormatter(cantidadFormatter);
        detalleVentaItem.cantidadProperty().bindBidirectional(cantidadFormatter.valueProperty());


        // descuento
        TextFormatter<BigDecimal> descuentoFormatter = Formatters.bigDecimalFormatter(BigDecimal.ZERO);
        descuentoField.setTextFormatter(descuentoFormatter);
        detalleVentaItem.descuentoProperty().bindBidirectional(descuentoFormatter.valueProperty());


        // precio
        TextFormatter<BigDecimal> precioFormatter = Formatters.bigDecimalFormatter();
        precioFormatter.setValue(null);
        precioField.setTextFormatter(precioFormatter);
        detalleVentaItem.precioProperty().bindBidirectional(precioFormatter.valueProperty());
        precioField.setPromptText("$");


        // productoComboBox
        FilteredList<Producto> productosFiltrados = new FilteredList<>(productoViewModel.getProductos(), p -> true);

        productoCardComboBox.getEditor().setPromptText("Buscar");
        productoCardComboBox.setEditable(true);
        productoCardComboBox.setItems(productosFiltrados);
        productoCardComboBox.setMaxWidth(Double.MAX_VALUE);

        productoCardComboBox.getEditor().setOnMouseClicked(event -> {
            if (!productoCardComboBox.isShowing()){
                productoCardComboBox.show();
            }
        });

        productoCardComboBox.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (cambioProgramatico) return ;
            // Solo ejecutar el filtro si no se está seleccionando un ítem del combo
            if (!productoCardComboBox.isFocused() || productoCardComboBox.getSelectionModel().getSelectedItem() == null) {
                Platform.runLater(() -> {
//                    productoViewModel.filtroBusquedaProperty().set(newText);
                    productosFiltrados.setPredicate(p ->
                                p.getNombre().toLowerCase().concat(" ").concat(p.getMarca()).contains(newText.toLowerCase()) ||
                                p.getMarca().toLowerCase().concat(" ").concat(p.getNombre()).contains(newText.toLowerCase())
                            );
                    productoCardComboBox.hide();
//                    if (!productoViewModel.getProductosFiltrados().isEmpty()) {
                    if (!productosFiltrados.isEmpty()) {
                        productoCardComboBox.show();
                    }
                });
            }


            // des-seleccionar item si textProperty está vacío.
            if (newText.trim().isEmpty()){
                productoCardComboBox.setValue(null);
            }
        });

        productoCardComboBox.setConverter(new StringConverter<Producto>() {
            @Override
            public String toString(Producto producto) {
                return producto != null ? producto.getNombre() + " " + producto.getMarca() + " - " + producto.getPeso()+(producto.getUnidadMedida() == null ? "" : producto.getUnidadMedida().getSimbolo()) : "";
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
                    ProductoCard card = new ProductoCard(producto);

                    setGraphic(card);
                }
            }
        });

        productoCardComboBox.valueProperty().bindBidirectional(detalleVentaItem.productoProperty());

        // botonAgregar
        botonAgregar.getStyleClass().add("DVF-botonAgregar");
        botonAgregar.setOnAction(actionEvent -> {
            cambioProgramatico = true;
            try {
                DetalleVentaItem dvItem = detalleVentaItem.getItemActual();
                onDetalleAgregado.accept(dvItem);
            } catch (IllegalArgumentException ex) {
                Alert a = new AdrysAlert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
                a.show();
            }
            detalleVentaItem.limpiarFormulario();
            cambioProgramatico = false;
        });

        // botonCancelar
        botonCancelar.setOnAction(actionEvent -> {
            cambioProgramatico = true;
            detalleVentaItem.limpiarFormulario();
            cambioProgramatico = false;
        });

        VBox cantidadVBox = new VBox(
                new Label("cantidad:"),
                cantidadField
        );

        VBox precioVBox = new VBox(
                new Label("precio:"),
                precioField
        );
        precioVBox.setVisible(false);

        StackPane stackPane = new StackPane(cantidadVBox, precioVBox);

        productoCardComboBox.valueProperty().addListener((obs, oldv, newv) -> {
            if (newv == null || !newv.esDivisible()){
                precioVBox.setVisible(false);
                cantidadVBox.setVisible(true);
            } else {
                precioVBox.setVisible(true);
                cantidadVBox.setVisible(false);
            }
        });

        this.getChildren().addAll(
                new Label("producto:"), productoCardComboBox,
                stackPane,
                new Label("descuento: %"), descuentoField,
                new Label(),
                buttonHolder
        );
    }
}
