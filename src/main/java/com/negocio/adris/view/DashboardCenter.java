package com.negocio.adris.view;

import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.entities.Venta;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.model.exceptions.VentaNotFoundException;
import com.negocio.adris.utils.BotonAfirmar;
import com.negocio.adris.utils.LabelNegrita;
import com.negocio.adris.utils.Utils;
import com.negocio.adris.viewmodel.PagoViewModel;
import com.negocio.adris.viewmodel.ProveedorViewModel;
import com.negocio.adris.viewmodel.VentaViewModel;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DashboardCenter extends StackPane {
    private final PagoViewModel pagoViewModel;
    private final ProveedorViewModel proveedorViewModel;
    private final VentaViewModel ventaViewModel;
    private final StackPane overlay = new StackPane();

    public DashboardCenter(PagoViewModel pagoViewModel, ProveedorViewModel proveedorViewModel, VentaViewModel ventaViewModel) throws VentaNotFoundException {
        this.pagoViewModel = pagoViewModel;
        this.proveedorViewModel = proveedorViewModel;
        this.ventaViewModel = ventaViewModel;

        getStyleClass().add("dashboardCenter");

        overlay.getStyleClass().add("overlay");
        overlay.setAlignment(Pos.CENTER);
        overlay.setVisible(false);

        Label titulo = new Label("Dashboard");
        titulo.getStyleClass().add("dashboardCenter-titulo");
        HBox tituloHolder = new HBox(titulo);
        tituloHolder.getStyleClass().add("dashboardCenter-tituloHolder");
        HBox balanceHolder = new HBox();
        balanceHolder.getStyleClass().add("dashboardCenter-balanceHolder");
        balanceHolder.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(balanceHolder, Priority.ALWAYS);

        VBox hoyHolder = new BalanceCard("Hoy", ventaViewModel.gananciaDiariaProperty(), pagoViewModel.montoPagadoDiarioProperty());
        VBox mesHolder = new BalanceCard("Mes", ventaViewModel.gananciaMensualProperty(), pagoViewModel.montoPagadoMensualProperty());

        balanceHolder.getChildren().addAll(new Region(), hoyHolder, new Region(),mesHolder, new Region());
        balanceHolder.getChildren().forEach(node -> {
            if (node instanceof Region r){
                r.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(r, Priority.ALWAYS);
            }
        });

        // Ventas y proveedores
        // Proveedores
        Label proveedoresTitle = new LabelNegrita("Proveedores");
        Button agregarProveedor = new BotonAfirmar("+");
        ListView<Proveedor> proveedorListView = new ListView<>();
        VBox proveedorBox = new VBox(10, proveedoresTitle, proveedorListView, agregarProveedor);
        proveedorBox.setPadding(new Insets(10));
        proveedorBox.getStyleClass().add("dashboardCenter-listViewBox");

        agregarProveedor.setOnAction(actionEvent -> {
            abrirFormularioCreacion(proveedorViewModel);
        });

        proveedorListView.setItems(proveedorViewModel.getProveedores());

        proveedorListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Proveedor proveedor, boolean empty){
                super.updateItem(proveedor, empty);

                if (empty || proveedor == null){
                    setGraphic(null);
                    return;
                }

                ProveedorCard proveedorCard = new ProveedorCard(proveedor);
                proveedorCard.setOnEditar(p -> abrirFormularioEdicion(proveedor, proveedorViewModel));

                proveedorCard.actualizarProveedor(proveedor);
                proveedorCard.setSeleccionado(isSelected());
                setGraphic(proveedorCard);
            }
        });



        // Ventas
        Label ventaTitulo = new LabelNegrita("Ventas");
        DatePicker fechaPicker = new DatePicker(LocalDate.now());
        fechaPicker.setMaxWidth(Double.MAX_VALUE);
        VBox ventaBox = new VBox(10); // 10 px de spacing entre DatePicker y ListView
        ventaBox.setPadding(new Insets(10));
        ventaBox.getStyleClass().add("dashboardCenter-listViewBox");


        ListView<Venta> ventaListView = new ListView<>();
        FilteredList<Venta> listaFiltrada = new FilteredList<>(ventaViewModel.getVentas());
        listaFiltrada.setPredicate(venta -> venta.getFecha().toLocalDate().equals(LocalDate.now()));

        fechaPicker.valueProperty().addListener((obs, oldFecha, newFecha) -> {
            if (newFecha != null){
                listaFiltrada.setPredicate(venta -> venta.getFecha().toLocalDate().equals(newFecha));
            }
        });

        ventaListView.setItems(listaFiltrada);
        ventaListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Venta venta, boolean empty){
                super.updateItem(venta, empty);

                if (empty || venta == null){
                    setGraphic(null);
                    return;
                }

                VentaCard ventaCard = new VentaCard(venta);
                ventaCard.setSeleccionado(isSelected());
                setGraphic(ventaCard);
            }
        });
        ventaBox.getChildren().addAll(ventaTitulo, fechaPicker, ventaListView);

        // saca la selección de un card cuando se toca otro
        proveedorListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        ventaListView.getSelectionModel().clearSelection();
                    }
                });

        ventaListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        proveedorListView.getSelectionModel().clearSelection();
                    }
                });


        //  GridPane
        GridPane cuentasGrid = new GridPane();
        cuentasGrid.setHgap(15);
        cuentasGrid.setVgap(10);
        cuentasGrid.getStyleClass().add("dashboardCenter-cuentasGrid");

        ColumnConstraints ventasCol = new ColumnConstraints();
        ventasCol.setPercentWidth(70);
        ColumnConstraints proveedoresCol = new ColumnConstraints();
        proveedoresCol.setPercentWidth(30);

        cuentasGrid.getColumnConstraints().addAll(ventasCol, proveedoresCol);

        // Añadimos los wrappers
        cuentasGrid.add(ventaBox, 0, 0);
        cuentasGrid.add(proveedorBox, 1, 0);

        // Permitir que los ListView se expandan
        GridPane.setHgrow(ventaBox, Priority.ALWAYS);
        GridPane.setHgrow(proveedorBox, Priority.ALWAYS);
        GridPane.setVgrow(ventaBox, Priority.ALWAYS);
        GridPane.setVgrow(proveedorBox, Priority.ALWAYS);

        ventaListView.setMaxWidth(Double.MAX_VALUE);
        proveedorListView.setMaxWidth(Double.MAX_VALUE);

        cuentasGrid.setMaxWidth(Double.MAX_VALUE);
        GridPane.setVgrow(cuentasGrid, Priority.ALWAYS);

        Region spacer = new Region();

        //boton exportar data
        Button exportar = new BotonAfirmar("Exporta data");
        HBox buttonHolder = new HBox(15, exportar);
        buttonHolder.setAlignment(Pos.CENTER);

        exportar.setOnAction(e -> abrirFormularioExportarDatos());

        // contenedor general
        VBox contenedorGeneral = new VBox(
                tituloHolder,
                balanceHolder,
                spacer,
                cuentasGrid,
                buttonHolder
        );

        contenedorGeneral.getStyleClass().add("dashboardCenter-Contenedor");

        spacer.setMaxWidth(Double.MAX_VALUE);
        spacer.prefHeightProperty().bind(
                contenedorGeneral.heightProperty().subtract(800).divide(4)
        );
        spacer.setMinHeight(10);

        VBox.setVgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(
                contenedorGeneral,
                overlay
        );

        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    Node target = event.getPickResult().getIntersectedNode();

                    // Sube en la jerarquía hasta ver si el clic fue dentro del ListView
                    while (target != null) {
                        if (target instanceof VentaCard) {
                            return;
                        }
                        if (target instanceof ProveedorCard){
                            return;
                        }
                        target = target.getParent();
                    }

                    ventaListView.getSelectionModel().clearSelection();
                    proveedorListView.getSelectionModel().clearSelection();
                });
            }
        });
    }

    private void actualizarDatosProveedorCard(long id){
        try {
            Proveedor nuevo = proveedorViewModel.getProveedor(id);

            ObservableList<Proveedor> lista = proveedorViewModel.getProveedores();

            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getId() == id){
                    lista.set(i, nuevo);
                    break;
                }
            }

        } catch (ProveedorNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void abrirFormularioEdicion(Proveedor p, ProveedorViewModel proveedorVM){
        mostrarFormulario(new ProveedorForm(p, proveedorVM, () -> {
            actualizarDatosProveedorCard(p.getId());
            cerrarOverlay();
        }));
        proveedorVM.cargarProveedor(p);
    }

    private void abrirFormularioCreacion(ProveedorViewModel proveedorVM){
        mostrarFormulario(new ProveedorForm(new Proveedor(), proveedorVM, this::cerrarOverlay));
    }

    private void mostrarFormulario(Node form) {
        overlay.getChildren().setAll(form);
        overlay.setVisible(true);
    }

    private void cerrarOverlay() {
        overlay.setVisible(false);
        overlay.getChildren().clear();
    }

    private void abrirFormularioExportarDatos(){
        mostrarFormulario(new ExportarDatosForm(this::cerrarOverlay));
    }
}
