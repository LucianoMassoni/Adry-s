package com.negocio.adris.view;

import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.PagoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.utils.*;
import com.negocio.adris.viewmodel.PagoViewModel;
import javafx.beans.binding.Bindings;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.util.converter.LongStringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PagoForm extends VBox {
    private final PagoViewModel pagoViewModel;
    private final Runnable onClose;

    public PagoForm(PagoViewModel pagoViewModel, Gasto gasto, Runnable onClose){
        this.pagoViewModel = pagoViewModel;
        this.onClose = onClose;

        this.getStyleClass().add("pagoForm");

        Label pagoFormTitulo = new LabelTitulo("Pago");
        HBox pagoFormHolder = new HBox(pagoFormTitulo);
        pagoFormHolder.getStyleClass().add("pagoForm-tituloHolder");


        // Gasto
        // fecha
        VBox fechaBox = new VBox();
        Label fechaTitulo = new Label("Fechas");
        HBox fechaTituloHolder = new HBox(fechaTitulo);
        fechaTituloHolder.getStyleClass().add("PF-tituloHolder");
        fechaTitulo.getStyleClass().add("PF-titulo");
        HBox fechasHolder = new HBox();

        VBox fechaContraidaBox = new VBox();
        Label fechaContraidaLabel = new Label("Fecha contraida");
        Label fechaContraida = new Label(Utils.dateTimeFormatter(gasto.getFechaDeudaContraida().toLocalDate()));
        fechaContraidaBox.getChildren().addAll(fechaContraidaLabel, fechaContraida);

        Region rFechas = new Region();
        rFechas.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(rFechas, Priority.ALWAYS);

        VBox fechaSaladarBox = new VBox();
        Label fechaSaldarLabel = new Label("Fecha a saldar");
        Label fechaSaldar = new Label(Utils.dateTimeFormatter(gasto.getFechaVencimiento().toLocalDate()));
        fechaSaladarBox.getChildren().addAll(fechaSaldarLabel, fechaSaldar);

        fechasHolder.getChildren().addAll(fechaContraidaBox, rFechas, fechaSaladarBox);
        fechaBox.getChildren().addAll(fechaTituloHolder, fechasHolder);

        // proveedor
        VBox proveedorBox = new VBox();
        Label proveedorTitulo = new Label("Proveedor");
        HBox proveedorTituloHolder = new HBox(proveedorTitulo);
        proveedorTituloHolder.getStyleClass().add("PF-tituloHolder");
        proveedorTitulo.getStyleClass().add("PF-titulo");
        proveedorTituloHolder.setMaxWidth(Double.MAX_VALUE);
        VBox proveedorHolder = new VBox();
        Label proveedorNombre = new Label(gasto.getProveedor().getNombre());
        Label proveedorTelefono = new Label(gasto.getProveedor().getTelefono());
        proveedorHolder.getChildren().addAll(proveedorNombre, proveedorTelefono);
        proveedorHolder.getStyleClass().add("PF-contentHolder");
        proveedorHolder.setMaxWidth(Double.MAX_VALUE);
        proveedorBox.getChildren().addAll(proveedorTituloHolder, proveedorHolder);

        // nota
        VBox notaBox = new VBox();
        Label notaTitulo = new Label("Nota");
        HBox notaTituloHolder = new HBox(notaTitulo);
        notaTituloHolder.getStyleClass().add("PF-tituloHolder");
        notaTitulo.setMaxWidth(Double.MAX_VALUE);
        notaTitulo.getStyleClass().add("PF-titulo");
        Label nota = new Label(gasto.getNota());
        nota.setWrapText(true);
        nota.setMaxWidth(120);
        HBox notaHolder = new HBox(nota);
        notaHolder.getStyleClass().add("PF-contentHolder");
        notaHolder.setMaxWidth(Double.MAX_VALUE);
        notaBox.getChildren().addAll(notaTituloHolder, notaHolder);

        // monto
        VBox montoBox = new VBox();
        Label montoTitulo = new Label("Monto");
        HBox montoTituloHolder = new HBox(montoTitulo);
        montoTituloHolder.getStyleClass().add("PF-tituloHolder");
        montoTituloHolder.setMaxWidth(Double.MAX_VALUE);
        montoTitulo.getStyleClass().add("PF-titulo");
        Label monto = new Label("$" + Utils.bigDecimalFormatter(gasto.getMonto()));
        HBox montoHolder = new HBox(monto);
        montoHolder.getStyleClass().add("PF-contentHolder");
        montoHolder.setMaxWidth(Double.MAX_VALUE);
        montoBox.getChildren().setAll(montoTituloHolder, montoHolder);

        // montoRestante
        VBox montoRestanteBox = new VBox();
        Label montoRestanteTitulo = new Label("Monto restante");
        HBox montoRestanteTituloHolder = new HBox(montoRestanteTitulo);
        montoRestanteTituloHolder.getStyleClass().add("PF-tituloHolder");
        montoRestanteTituloHolder.setMaxWidth(Double.MAX_VALUE);
        montoRestanteTitulo.getStyleClass().add("PF-titulo");
        Label montoRestante = new Label("$" + Utils.bigDecimalFormatter(gasto.getMontoRestante()));
        HBox montoRestanteHolder = new HBox(montoRestante);
        montoRestanteHolder.getStyleClass().add("PF-contentHolder");
        montoRestanteHolder.setMaxWidth(Double.MAX_VALUE);
        montoRestanteBox.getChildren().setAll(montoRestanteTituloHolder, montoRestanteHolder);

        fechaBox.setMaxWidth(Double.MAX_VALUE);
        proveedorBox.setMaxWidth(Double.MAX_VALUE);
        notaBox.setMaxWidth(Double.MAX_VALUE);
        montoBox.setMaxWidth(Double.MAX_VALUE);
        montoRestanteBox.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(fechaBox, Priority.ALWAYS);
        HBox.setHgrow(proveedorBox, Priority.ALWAYS);
        HBox.setHgrow(notaBox, Priority.ALWAYS);
        HBox.setHgrow(montoBox, Priority.ALWAYS);
        HBox.setHgrow(montoRestanteBox, Priority.ALWAYS);


        HBox gastoInfo = new HBox(20,
                fechaBox,
                proveedorBox,
                notaBox,
                montoBox,
                montoRestanteBox
        );
        gastoInfo.setMaxWidth(Double.MAX_VALUE);
        gastoInfo.getStyleClass().add("PF-gastoInfo");

        TextField idField = new TextField();
        DatePicker fechaField = new DatePicker();
        TextField montoField = new TextField();
        Button botonAgregar = new BotonAfirmar("Aceptar");
        Button botonModificar = new BotonAfirmar("Modificar");
        Button botonCancelar = new BotonCancelar();

        // id
        StringConverter<? extends Number> longConverter = new LongStringConverter();
        Bindings.bindBidirectional(idField.textProperty(), pagoViewModel.idProperty(), (StringConverter<Number>) longConverter);
        idField.setVisible(false);

        // gasto
        pagoViewModel.gastoProperty().set(gasto);

        // fecha
        fechaField.valueProperty().set(LocalDate.now());
        pagoViewModel.fechaPagoProperty().set(LocalDateTime.now());
        fechaField.valueProperty().addListener((obs, oldV, newV)->{
            if (newV != null)
                pagoViewModel.fechaPagoProperty().set(newV.atTime(LocalTime.now()));
        });
        pagoViewModel.fechaPagoProperty().addListener((obs, oldV, newV)->{
            if (newV != null){
                fechaField.setValue(newV.toLocalDate());
            }
        });

        // monto
        TextFormatter<BigDecimal> montoFormatter = Formatters.bigDecimalFormatter();
        montoField.setTextFormatter(montoFormatter);
        pagoViewModel.montoPagadoProperty().bindBidirectional(montoFormatter.valueProperty());

        // Botones
        botonAgregar.visibleProperty().bind(idField.textProperty().isEqualTo("0"));
        botonModificar.visibleProperty().bind(idField.textProperty().isNotEqualTo("0"));

        botonAgregar.setOnAction(actionEvent -> {
            try {
                pagoViewModel.crearPago();
                pagoViewModel.limpiarFormulario();
                onClose.run();
            } catch (GastoNotFoundException | ProveedorNotFoundException | IllegalArgumentException ex) {
                Alert a = new AdrysAlert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
                a.show();
            }
        });

        botonModificar.setOnAction(actionEvent -> {
            try {
                pagoViewModel.modificarPago();
                pagoViewModel.limpiarFormulario();
                onClose.run();
            } catch (PagoNotFoundException | GastoNotFoundException | ProveedorNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        botonCancelar.setOnAction(actionEvent -> {
            pagoViewModel.limpiarFormulario();
            onClose.run();
        });

        Region region = new Region();
        HBox buttonHolder = new HBox(botonAgregar, botonModificar, region, botonCancelar);

        this.setMaxSize(800, 400);
        this.setFillWidth(true);

        Region r0 = new Region();
        Region r1 = new Region();
        Region r2 = new Region();
        Region r3 = new Region();
        Region r4 = new Region();

        this.getChildren().addAll(
                r0,
                pagoFormTitulo,
                r1,
                gastoInfo,
                r2,
                new Label("fecha:"), fechaField,
                r3,
                new Label("Monto:"), montoField,
                r4,
                buttonHolder,
                idField
        );

        this.getChildren().forEach(n -> {
            if (n instanceof Region r){
                r.setMaxWidth(Double.MAX_VALUE);
                VBox.setVgrow(r, Priority.ALWAYS);
            }
        });

    }
}
