package com.negocio.adris.view;

import com.negocio.adris.utils.LabelTitulo;
import com.negocio.adris.viewmodel.DetalleVentaItem;
import com.negocio.adris.viewmodel.ProductoViewModel;
import com.negocio.adris.viewmodel.VentaViewModel;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class VentaView extends VBox {
    public VentaView(VentaViewModel ventaViewModel,  ProductoViewModel productoViewModel, Consumer<DetalleVentaItem> item){
        Label titulo = new LabelTitulo("Ventas");
        titulo.getStyleClass().add("VentaView-titulo");
        HBox tituloHolder = new HBox(titulo);
        tituloHolder.getStyleClass().add("VentaView-tituloHolder");
        tituloHolder.setMaxWidth(Double.MAX_VALUE);

        VentaCenter ventaCenter = new VentaCenter(ventaViewModel);
        DetalleVentaForm detalleVentaForm = new DetalleVentaForm(productoViewModel, item);

        Region region = new Region();

        region.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(region, Priority.ALWAYS);

        getChildren().addAll(tituloHolder, detalleVentaForm, region, ventaCenter);
        getStyleClass().add("VentaView");
    }
}
