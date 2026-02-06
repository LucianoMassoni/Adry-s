package com.negocio.adris.view;

import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.utils.BotonAfirmar;
import com.negocio.adris.utils.LabelTitulo;
import com.negocio.adris.utils.VistaGastos;
import com.negocio.adris.viewmodel.GastoViewModel;
import com.negocio.adris.viewmodel.PagoViewModel;
import com.negocio.adris.viewmodel.ProveedorViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public class GastoCenter extends StackPane {
    private final StackPane overlay = new StackPane();
    private final ListView<Gasto> listView = new ListView<>();
    private ObjectProperty<VistaGastos> vistaActual = new SimpleObjectProperty<>();

    private GastoViewModel gastoViewModel;
    private PagoViewModel pagoViewModel;
    private ProveedorViewModel proveedorViewModel;

    public GastoCenter(GastoViewModel gastoViewModel, ProveedorViewModel proveedorViewModel, PagoViewModel pagoViewModel){
        this.getStyleClass().add("gastoCenter");
        this.gastoViewModel = gastoViewModel;
        this.pagoViewModel = pagoViewModel;
        this.proveedorViewModel = proveedorViewModel;

        overlay.getStyleClass().add("overlay");
        overlay.setAlignment(Pos.CENTER);
        overlay.setVisible(false);

        vistaActual.set(VistaGastos.GASTOS_ACTIVOS);

        Button botonGastosActivos = new BotonAfirmar("Gastos activos");
        botonGastosActivos.setMaxWidth(Double.MAX_VALUE);
        Button botonHistoriaGastos = new BotonAfirmar("Historial de gastos");
        botonHistoriaGastos.setMaxWidth(Double.MAX_VALUE);

        HBox selectHolder = new HBox(
                botonGastosActivos,
                botonHistoriaGastos
        );
        HBox.setHgrow(selectHolder, Priority.ALWAYS);
        HBox.setHgrow(botonGastosActivos, Priority.ALWAYS);
        HBox.setHgrow(botonHistoriaGastos, Priority.ALWAYS);
        selectHolder.setMaxWidth(Double.MAX_VALUE);

        FilteredList<Gasto> gastosFiltrados = new FilteredList<>(gastoViewModel.getGastos());

        gastosFiltrados.setPredicate(gasto -> !gasto.isSaldado());
        listView.setItems(gastosFiltrados);

        botonGastosActivos.setOnAction(e -> {
                gastosFiltrados.setPredicate(gasto -> !gasto.isSaldado());
                vistaActual.set(VistaGastos.GASTOS_ACTIVOS);
        });

        botonHistoriaGastos.setOnAction(actionEvent -> {
                gastosFiltrados.setPredicate(Gasto::isSaldado);
                vistaActual.set(VistaGastos.GASTOS_SALDADOS);
        });


        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Gasto gasto, boolean empty) {
                super.updateItem(gasto, empty);

                if (empty || gasto == null) {
                    setGraphic(null);
                    return;
                }

                GastoCard card = creaCard(gasto);
                configurarCelda(this, card, gasto);

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

        Label titulo = new LabelTitulo("Cuentas");
        HBox tituloHolder = new HBox(titulo);
        tituloHolder.getStyleClass().add("tituloHolder");

        listView.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(listView, Priority.ALWAYS);

        VBox vbox = new VBox(tituloHolder, selectHolder, listView, botonHolder);
        vbox.getStyleClass().add("gastoCenter-contenedor");

        // para des-seleccionar el card cuando se toca fuera.
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    Node target = event.getPickResult().getIntersectedNode();

                    // Sube en la jerarquía hasta ver si el clic fue dentro del ListView
                    while (target != null) {
                        if (target instanceof GastoCard) {
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
                    Node n = (Node) event.getTarget();
                    while (n != null) {
                        if (n instanceof Button) {
                            return; // dejar pasar el evento
                        }
                        n = n.getParent();
                    }
                    event.consume(); // ← esto evita el “toggle off” en clic repetido
                }
            }
        });


        this.getChildren().addAll(
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

    private void configurarCelda(ListCell<Gasto> cell, GastoCard card, Gasto gasto){
        cell.selectedProperty().addListener((obs, oldSel, newSel) ->
                card.setSeleccionado(newSel)
        );

        card.actualizarDatos(gasto);
        cell.setGraphic(card);
        card.setSeleccionado(cell.isSelected());
    }

    private GastoCard creaCard(Gasto gasto){
        return switch (vistaActual.get()){
            case GASTOS_ACTIVOS -> {
                GastoCard card = new GastoCard(gasto, false);
                card.setOnEditar(g -> abrirFormularioEdicion(gasto, gastoViewModel, proveedorViewModel));
                card.setOnPagar(g -> abrirFormularioPago(gasto, pagoViewModel));
                card.actualizarDatos(gasto);
                yield card;
            }
            case GASTOS_SALDADOS -> {
                GastoCard card = new GastoCard(gasto, true);
                card.actualizarDatos(gasto);
                yield card;
            }
        };
    }
}
