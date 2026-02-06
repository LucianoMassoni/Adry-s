package com.negocio.adris.view;

import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.exceptions.ProductoNotFoundException;
import com.negocio.adris.utils.BotonAfirmar;
import com.negocio.adris.utils.LabelTitulo;
import com.negocio.adris.viewmodel.ProductoViewModel;
import com.negocio.adris.viewmodel.ProveedorViewModel;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class ProductoCenter extends StackPane {
    private final ProductoViewModel viewModel;
    private final StackPane overlay = new StackPane();

    public ProductoCenter(ProductoViewModel viewModel) {
        this.viewModel = viewModel;

        overlay.getStyleClass().add("overlay");
        overlay.setAlignment(Pos.CENTER);
        overlay.setVisible(false);

        Label titulo = new LabelTitulo("Productos", true);
        HBox tituloHolder = new HBox(titulo);
        tituloHolder.getStyleClass().add("tituloHolder");

        TextField busquedaField = new TextField();

        busquedaField.setPromptText("Busque un producto...");

        FilteredList<Producto> productosFiltrados = new FilteredList<>(viewModel.getProductos(), p -> true);

        busquedaField.textProperty().addListener((obs, oldv, newv) ->{
            String filtro = newv == null ? "" : newv.toLowerCase();
            productosFiltrados.setPredicate(p ->
                        p.getNombre().toLowerCase().contains(filtro) ||
                        p.getMarca().toLowerCase().contains(filtro)
                    );
        });

        ListView<Producto> listView = new ListView<>();
        listView.setItems(productosFiltrados);

        // Custom CellFactory para que use ProductoCard
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Producto producto, boolean empty) {
                super.updateItem(producto, empty);

                if (empty || producto == null) {
                    setGraphic(null);
                } else {
                    ProductoCard card = new ProductoCard(producto);

                    card.setSeleccionado(isSelected());

                    card.setOnEditar(p -> {
                        //                                viewModel.cargarProducto(p);
                        try {
                            abrirFormularioEdicion(p, viewModel);
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

                }
            }
        });

        listView.setMaxHeight(Double.MAX_VALUE);
        listView.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(listView, Priority.ALWAYS);
        listView.getItems().reversed();

        Button botonAgregar = new BotonAfirmar("+");
        botonAgregar.setOnAction(e ->{
            abrirFormularioCreacion(viewModel);
        });

        VBox contenedor = new VBox(15, tituloHolder, busquedaField, listView, botonAgregar);
        contenedor.getStyleClass().add("productoCenter-contenedor");

        getStyleClass().add("productoCenter");
        getChildren().addAll(contenedor, overlay);

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

    private void abrirFormularioEdicion(Producto p, ProductoViewModel viewModel) throws ProductoNotFoundException {
        mostrarFormulario(new ProductoForm(p, viewModel, () -> {
//            actualizarDatosProveedorCard(p.getId());

            cerrarOverlay();
        }));
        viewModel.cargarProducto(p);
    }

    private void abrirFormularioCreacion(ProductoViewModel viewModel){
        mostrarFormulario(new ProductoForm(new Producto(), viewModel, this::cerrarOverlay));
    }

    private void mostrarFormulario(Node form) {
        overlay.getChildren().setAll(form);
        overlay.setVisible(true);
    }

    private void cerrarOverlay() {
        overlay.setVisible(false);
        overlay.getChildren().clear();
    }
}
