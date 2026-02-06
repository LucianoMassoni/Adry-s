package com.negocio.adris.view;

import com.negocio.adris.model.enums.FormaDePago;
import com.negocio.adris.utils.AdrysAlert;
import com.negocio.adris.utils.BotonAfirmar;
import com.negocio.adris.utils.BotonCancelar;
import com.negocio.adris.viewmodel.DetalleVentaItem;
import com.negocio.adris.viewmodel.VentaViewModel;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import javafx.util.converter.LongStringConverter;

import java.math.BigDecimal;


public class VentaCenter extends VBox {
    public VentaCenter(VentaViewModel ventaViewModel){
        TextField idField = new TextField();
        Label bigTotalField = new Label();
        ListView<DetalleVentaItem> detalleVentaListView = new ListView<>();
        Label totalField = new Label();

        this.getStyleClass().add("venta-center");

        // categoriaContainer
        Label producto = new Label("Producto");
        Label marca = new Label("Marca");
        Label peso = new Label("Peso");
        Label cantidad = new Label("Cantidad");
        Label descuento = new Label("Descuento");
        Label subtotal = new Label("Subtotal");

        Region spacer = new Region();


        HBox categoriaContainer = new HBox(
                new Region(), producto, new Region(), marca, new Region(), peso, new Region(), cantidad, new Region(), descuento, new Region(), subtotal, new Region(), spacer
        );

        categoriaContainer.getChildren().forEach(node -> {
            HBox.setHgrow(node, Priority.ALWAYS);
            HBox.setMargin(node, Insets.EMPTY);
            if (node instanceof Region region) {
                region.setMaxWidth(Double.MAX_VALUE);
            }
        });

        spacer.setPrefWidth(40);
        categoriaContainer.getStyleClass().add("VC-categoriaContainer");

        // id
        StringConverter<? extends Number> longConverter = new LongStringConverter();
        Bindings.bindBidirectional(idField.textProperty(), ventaViewModel.idProperty(), (StringConverter<Number>) longConverter);
        idField.setVisible(false);
        idField.setMaxSize(0, 0);

        // detalleVentaListView
        detalleVentaListView.setItems(ventaViewModel.getDetalleVentas());

        detalleVentaListView.setCellFactory(param -> new ListCell<>(){
            @Override
            protected void updateItem(DetalleVentaItem item, boolean empty){
                super.updateItem(item, empty);

                if (empty || item == null){
                    setGraphic(null);
                } else {
                    try {
                        DetalleVentaCard card = new DetalleVentaCard(item);
                        card.setSeleccionado(isSelected());

                        card.setOnBorrar(ventaViewModel::borrarItem);
                        if (!item.productoProperty().get().esDivisible()){
                            card.setOnAgregar(ventaViewModel::agregarCantidad);
                            card.setOnSacar(ventaViewModel::sacarCantidad);
                        }

                        selectedProperty().addListener((obs, wasSelected, isNowSelected) -> card.setSeleccionado(isNowSelected));

                        setGraphic(card);
                    } catch (RuntimeException  e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

//        detalleVentaListView.setPrefHeight(600);
        detalleVentaListView.getStyleClass().add("VC-ListView");


        // totalHolder
        totalField.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    BigDecimal total = ventaViewModel.totalProperty().get();
                    return total != null ? "$".concat(total.toString()) : "$ --";
                },
                ventaViewModel.totalProperty()
        ));

        Region separadorTotal = new Region();
        HBox totalHolder = new HBox(
                new Label("TOTAL:"),
                separadorTotal,
                totalField
        );

        separadorTotal.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(separadorTotal, Priority.ALWAYS);

        totalHolder.getStyleClass().add("VC-totalHolder");

        // formaDePago
        ToggleGroup formaDePagoToggleGroup = new ToggleGroup();
        RadioButton efectivoRadioButton = new RadioButton(FormaDePago.EFECTIVO.toString());
        RadioButton tarjetaRadioButton = new RadioButton(FormaDePago.TARJETA.toString());
        RadioButton transferenciaRadioButton = new RadioButton(FormaDePago.TRANSFERENCIA.toString());

        efectivoRadioButton.setToggleGroup(formaDePagoToggleGroup);
        tarjetaRadioButton.setToggleGroup(formaDePagoToggleGroup);
        transferenciaRadioButton.setToggleGroup(formaDePagoToggleGroup);

        efectivoRadioButton.setUserData(FormaDePago.EFECTIVO);
        tarjetaRadioButton.setUserData(FormaDePago.TARJETA);
        transferenciaRadioButton.setUserData(FormaDePago.TRANSFERENCIA);

        formaDePagoToggleGroup.selectToggle(efectivoRadioButton);
        ventaViewModel.formaDePagoProperty().set(FormaDePago.EFECTIVO);

        formaDePagoToggleGroup.selectedToggleProperty().addListener((obs, oldv, newv) -> {
            if (newv != null){
                FormaDePago forma = (FormaDePago) newv.getUserData();
                ventaViewModel.formaDePagoProperty().set(forma);
                ventaViewModel.recalcularTotal();
            }
        });

        ventaViewModel.formaDePagoProperty().addListener((obs, oldv, newv) -> {
            for (Toggle toggle : formaDePagoToggleGroup.getToggles()) {
                if (toggle.getUserData() == newv) {
                    formaDePagoToggleGroup.selectToggle(toggle);
                    break;
                }
            }
        });

        // botonCancelar
        Button botonCancelar = new BotonCancelar();
        botonCancelar.setOnAction(actionEvent -> {
            Alert a = new AdrysAlert(Alert.AlertType.CONFIRMATION, "Desea cancelar la compra?");
            a.showAndWait().
                    filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> {
                        ventaViewModel.cancelar();
                        formaDePagoToggleGroup.selectToggle(efectivoRadioButton);
                        ventaViewModel.formaDePagoProperty().set(FormaDePago.EFECTIVO);
                    });
        });

        // botonAceptar
        Button botonAceptar = new BotonAfirmar();
        botonAceptar.setOnAction(actionEvent -> {
            ventaViewModel.guardarVenta();
            formaDePagoToggleGroup.selectToggle(efectivoRadioButton);
            ventaViewModel.formaDePagoProperty().set(FormaDePago.EFECTIVO);
        });



        HBox radioButtonsHolder = new HBox(efectivoRadioButton, tarjetaRadioButton, transferenciaRadioButton);

        radioButtonsHolder.getChildren().forEach( node -> HBox.setHgrow(node, Priority.ALWAYS));
        radioButtonsHolder.getStyleClass().add("VC-radioButtonsHolder");

        VBox formaDePagoHolder = new VBox(
                new Label("Forma de pago:"),
                new Label(),
                radioButtonsHolder
        );

        formaDePagoHolder.getStyleClass().add("VC-formaDePagoHolder");

        HBox botonHolder = new HBox(
                botonAceptar,
                new Region(),
                botonCancelar
        );

        botonHolder.getChildren().forEach(node -> {
            HBox.setHgrow(node, Priority.ALWAYS);
            if (node instanceof HBox hbox){
                hbox.setMaxWidth(Double.MAX_VALUE);
                hbox.setAlignment(Pos.CENTER);
            }
        });


        botonHolder.getStyleClass().add("VC-botonHolder");

        this.getChildren().addAll(
                bigTotalField,
                categoriaContainer,
                detalleVentaListView,
                formaDePagoHolder,
                totalHolder,
                idField,
                botonHolder
        );

        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    Node target = event.getPickResult().getIntersectedNode();

                    // Sube en la jerarquía hasta ver si el clic fue dentro del ListView
                    while (target != null) {
                        if (target instanceof DetalleVentaCard) {
                            return;
                        }
                        target = target.getParent();
                    }

                    detalleVentaListView.getSelectionModel().clearSelection();
                });
            }
        });
    }
}
