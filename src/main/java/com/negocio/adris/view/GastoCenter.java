package com.negocio.adris.view;

import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.utils.BotonAfirmar;
import com.negocio.adris.utils.VistaGastos;
import com.negocio.adris.viewmodel.GastoViewModel;
import com.negocio.adris.viewmodel.PagoViewModel;
import com.negocio.adris.viewmodel.ProveedorViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public class GastoCenter extends StackPane {
    private final StackPane overlay = new StackPane();
    private final ListView<Gasto> listView = new ListView<>();


    private GastoViewModel gastoViewModel;
    private PagoViewModel pagoViewModel;

    public GastoCenter(GastoViewModel gastoViewModel, ProveedorViewModel proveedorViewModel, PagoViewModel pagoViewModel){
        this.getStyleClass().add("gastoCenter");
        this.gastoViewModel = gastoViewModel;
        this.pagoViewModel = pagoViewModel;

        overlay.getStyleClass().add("overlay");
        overlay.setAlignment(Pos.CENTER);
        overlay.setVisible(false);

        ObjectProperty<VistaGastos> vistaActual = new SimpleObjectProperty<>();
        vistaActual.set(VistaGastos.GASTOS_ACTIVOS);

        Button botonGastosActivos = new Button("Gastos activos");
        Button botonHistoriaGastos = new Button("Historial de gastos");
        Button botonHistorialPagos = new Button("Historial de pagos");

        HBox selectHolder = new HBox(
                botonGastosActivos,
                botonHistoriaGastos,
                botonHistorialPagos
        );

        FilteredList<Gasto> gastosFiltrados = new FilteredList<>(gastoViewModel.getGastos());

        listView.setItems(gastosFiltrados);

        botonGastosActivos.setOnAction(e -> {
                gastosFiltrados.setPredicate(gasto -> !gasto.isSaldado());
                vistaActual.set(VistaGastos.GASTOS_ACTIVOS);
        });

        botonHistoriaGastos.setOnAction(actionEvent -> {
                gastosFiltrados.setPredicate(Gasto::isSaldado);
                vistaActual.set(VistaGastos.GASTOS_SALDADOS);
        });

        botonHistorialPagos.setOnAction(actionEvent -> {
            gastosFiltrados.setPredicate(gasto -> !gasto.getPagos().isEmpty());
            vistaActual.set(VistaGastos.HISTORIAL_PAGOS);
        });

        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Gasto gasto, boolean empty) {
                super.updateItem(gasto, empty);

                if (empty || gasto == null) {
                    setGraphic(null);
                    return;
                }

                switch (vistaActual.get()){
                    case GASTOS_ACTIVOS -> {
                        GastoCard card = new GastoCard(gasto, false);
                        card.setOnEditar(g -> abrirFormularioEdicion(gasto, gastoViewModel, proveedorViewModel));
                        card.setOnPagar(g -> abrirFormularioPago(gasto, pagoViewModel));

                        // Escucha una sola vez los cambios de selección
                        selectedProperty().addListener((obs, oldSel, newSel) -> card.setSeleccionado(newSel));

                        card.actualizarDatos(gasto);

                        setGraphic(card);
                        // actualiza visualmente el estado por si se recicló la celda
                        card.setSeleccionado(isSelected());
                    }

                    case GASTOS_SALDADOS -> {
                        GastoCard card = new GastoCard(gasto, true);
                        // Escucha una sola vez los cambios de selección
                        selectedProperty().addListener((obs, oldSel, newSel) -> card.setSeleccionado(newSel));

                        card.actualizarDatos(gasto);

                        setGraphic(card);
                        // actualiza visualmente el estado por si se recicló la celda
                        card.setSeleccionado(isSelected());
                    }

                    case HISTORIAL_PAGOS -> {
                        GastoPagoCard card = new GastoPagoCard(gasto);
                        // Escucha una sola vez los cambios de selección
                        selectedProperty().addListener((obs, oldSel, newSel) -> card.setSeleccionado(newSel));

                        card.actualizarDatos(gasto);

                        setGraphic(card);
                        // actualiza visualmente el estado por si se recicló la celda
                        card.setSeleccionado(isSelected());
                    }
                }

            }
        });


        Button botonAgregar = new BotonAfirmar("+");
        botonAgregar.getStyleClass().add("GCenter-botonAgregar");


        botonAgregar.setOnAction(actionEvent -> {
            abrirFormularioNuevo(gastoViewModel, proveedorViewModel);
        });


        Region r = new Region();
        HBox botonHolder = new HBox(r, botonAgregar);
        r.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(r, Priority.ALWAYS);
        botonHolder.getStyleClass().add("GCenter-botonHolder");

        Label titulo = new Label("Cuentas");
        titulo.getStyleClass().add("GCenter-titulo");

        listView.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(listView, Priority.ALWAYS);

        VBox vbox = new VBox(titulo, selectHolder, listView, botonHolder);


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

        listView.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            Node target = event.getPickResult().getIntersectedNode();
            while (target != null && !(target instanceof ListCell)) {
                target = target.getParent();
            }

            if (target instanceof ListCell<?> cell) {
                if (cell.isEmpty()) return;
                if (cell.isSelected()) {
                    event.consume(); // ← esto evita el “toggle off” en clic repetido
                }
            }
        });

        getChildren().addAll(
                vbox,
                overlay
        );
    }

    private void abrirFormularioNuevo(GastoViewModel gastoVM, ProveedorViewModel proveedorVM) {
        gastoVM.limpiarFormulario();
        mostrarFormulario(new GastoForm(gastoVM, proveedorVM, this::cerrarOverlay));
    }

    private void abrirFormularioEdicion(Gasto gasto, GastoViewModel gastoVM, ProveedorViewModel proveedorVM) {
        mostrarFormulario(new GastoForm(gastoVM, proveedorVM, this::cerrarOverlay));
        gastoVM.cargarGasto(gasto);
    }

    private void mostrarFormulario(Node form) {
        overlay.getChildren().setAll(form);
        overlay.setVisible(true);
    }

    private void cerrarOverlay() {
        overlay.setVisible(false);
        overlay.getChildren().clear();
    }

    private void abrirFormularioPago(Gasto gasto, PagoViewModel pagoViewModel){
        mostrarFormulario(new PagoForm(pagoViewModel, gasto, () -> {
            cerrarOverlay();
            actualizarGasto(gasto.getId());
        }));
    }

    private void actualizarGasto(long id){
        try {
            Gasto nuevo = gastoViewModel.getGasto(id);

            ObservableList<Gasto> lista = gastoViewModel.getGastos();

            for (int i = 0; i < lista.size(); i++){
                if (lista.get(i).getId() == id){
                    lista.set(i, nuevo);
                    break;
                }
            }

        } catch (GastoNotFoundException | ProveedorNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
