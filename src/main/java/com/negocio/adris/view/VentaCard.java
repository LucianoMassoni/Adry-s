package com.negocio.adris.view;

import com.negocio.adris.model.entities.DetalleVenta;
import com.negocio.adris.model.entities.Venta;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class VentaCard extends VBox {

    private final Venta venta;
    private boolean seleccionado = false;

    private final VBox detalleBox = new VBox();
    private final GridPane detalleGrid = new GridPane();

    public VentaCard(Venta venta) {
        this.venta = venta;

        getStyleClass().add("card");
        setSpacing(10);

        // ======= RESUMEN SUPERIOR =======
        GridPane resumenGrid = new GridPane();
        resumenGrid.setHgap(20);
        resumenGrid.setAlignment(Pos.CENTER_LEFT);

        resumenGrid.add(crearBox("Hora",
                String.format("%02d:%02d",
                        venta.getFecha().getHour(),
                        venta.getFecha().getMinute())), 0, 0);

        int cantidad = venta.getDetalleVentas()
                .stream()
                .mapToInt(dv -> dv.getCantidad().intValue())
                .sum();

        resumenGrid.add(crearBox("Cantidad", String.valueOf(cantidad)), 1, 0);
        resumenGrid.add(crearBox("Forma de pago", venta.getFormaDePago().toString()), 2, 0);
        resumenGrid.add(crearBox("TOTAL", "$" + venta.getTotal()), 3, 0);

        // columnas que crecen parejo
        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            resumenGrid.getColumnConstraints().add(cc);
        }

        // ======= DETALLE =======
        detalleBox.setVisible(false);
        detalleBox.setManaged(false);
        detalleBox.setSpacing(5);

        configurarDetalleGrid();
        construirDetalleVenta();

        detalleBox.getChildren().add(detalleGrid);

        getChildren().addAll(resumenGrid, detalleBox);
    }

    // SELECCIÓN
    public void setSeleccionado(boolean seleccionado) {
        if (this.seleccionado == seleccionado) return;

        this.seleccionado = seleccionado;

        if (seleccionado) {
            getStyleClass().add("card-seleccionado");
            expandir();
        } else {
            getStyleClass().remove("card-seleccionado");
            colapsar();
        }
    }

    private void expandir() {
        detalleBox.setManaged(true);
        detalleBox.setVisible(true);

        FadeTransition ft = new FadeTransition(Duration.millis(150), detalleBox);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void colapsar() {
        FadeTransition ft = new FadeTransition(Duration.millis(150), detalleBox);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            detalleBox.setVisible(false);
            detalleBox.setManaged(false);
        });
        ft.play();
    }

    // DETALLE GRID
    private void configurarDetalleGrid() {
        detalleGrid.setHgap(15);
        detalleGrid.setVgap(5);

        String[] titulos = {
                "Nombre", "Marca", "Unidad", "Precio", "cantidad", "Descuento", "Subtotal"
        };

        for (int col = 0; col < titulos.length; col++) {
            Label titulo = new Label(titulos[col]);
            titulo.getStyleClass().add("card-titulo");
            detalleGrid.add(titulo, col, 0);

            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            detalleGrid.getColumnConstraints().add(cc);
        }
    }

    private void construirDetalleVenta() {
        int fila = 1;

        for (DetalleVenta dv : venta.getDetalleVentas()) {
            detalleGrid.add(new Label(dv.getProducto().getNombre()), 0, fila);
            detalleGrid.add(new Label(dv.getProducto().getMarca()), 1, fila);
            detalleGrid.add(new Label(
                    (dv.getProducto().getPeso() == 0.0 ? "-" :
                            (int) dv.getProducto().getPeso()) +
                       (dv.getProducto().getUnidadMedida() == null ? "-" :
                               dv.getProducto().getUnidadMedida().getSimbolo())), 2, fila);
            detalleGrid.add(new Label("$" + (dv.getPrecioUnitario() == null ? dv.getSubtotal() :
                    dv.getPrecioUnitario())), 3, fila);
            detalleGrid.add(new Label(dv.getCantidad().toString()), 4, fila);
            detalleGrid.add(new Label("%" + dv.getDescuento()), 5, fila);
            detalleGrid.add(new Label("$" + dv.getSubtotal()), 6, fila);

            fila++;
        }
    }

    // HELPERS
    private VBox crearBox(String titulo, String valor) {
        Label t = new Label(titulo);
        t.getStyleClass().add("card-titulo");

        Label v = new Label(valor);
        v.getStyleClass().add("card-content");

        VBox box = new VBox(5, t, v);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("card-box");

        return box;
    }
}