package com.negocio.adris.view;

import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.exceptions.ProductoNotFoundException;
import com.negocio.adris.viewmodel.ProductoViewModel;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;


public class ProductoCenter extends VBox {
    public ProductoCenter(ProductoViewModel viewModel) {
        TextField busquedaField = new TextField();
        busquedaField.textProperty().bindBidirectional(viewModel.filtroBusquedaProperty());

        ListView<Producto> listView = new ListView<>();
        listView.setItems(viewModel.getProductosFiltrados());

        // Custom CellFactory para que use ProductoCard
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Producto producto, boolean empty) {
                super.updateItem(producto, empty);

                if (empty || producto == null) {
                    setGraphic(null);
                } else {
                    try {
                        ProductoCard card = new ProductoCard(producto);

                        card.setSeleccionado(isSelected());

                        card.setOnEditar(p -> {
                            try {
                                viewModel.cargarProducto(p);
                            } catch (ProductoNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        });

                        card.setOnBorrar(p -> {
                            try {
                                viewModel.eliminarProducto(p.getId());
                            } catch (ProductoNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        });

                        // Escucha los cambios de selección para actualizar el estilo
                        selectedProperty().addListener((obs, wasSelected, isNowSelected) -> card.setSeleccionado(isNowSelected));

                        setGraphic(card);

                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        listView.setPrefHeight(600);
        listView.getItems().reversed();

        this.getStyleClass().add("productoCenter");
        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.getChildren().addAll(busquedaField, listView);

        // para des-seleccionar el card cuando se toca fuera.
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    Node target = event.getPickResult().getIntersectedNode();

                    // Sube en la jerarquía hasta ver si el clic fue dentro del ListView
                    while (target != null) {
                        if (target instanceof ProductoCard) {
                            return;
                        }
                        target = target.getParent();
                    }

                    listView.getSelectionModel().clearSelection();
                });
            }
        });
    }
}
