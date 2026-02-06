package com.negocio.adris.view;

import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.exceptions.GastoNotFoundException;
import com.negocio.adris.model.exceptions.PagoNotFoundException;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;
import com.negocio.adris.utils.*;
import com.negocio.adris.viewmodel.PagoViewModel;
import javafx.beans.binding.Bindings;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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


        GridPane gastoInfo = new GridPane();
        gastoInfo.setHgap(20);
        gastoInfo.setVgap(10);
        gastoInfo.setMaxWidth(Double.MAX_VALUE);
        gastoInfo.getStyleClass().add("PF-gastoInfo");


        // COLUMNAS
        for (int i = 0; i < 5; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.ALWAYS);
            col.setFillWidth(true);
            gastoInfo.getColumnConstraints().add(col);
        }


        // FECHAS
        Label fechaTitulo = new Label("Fechas");
        fechaTitulo.getStyleClass().add("PF-titulo");
        HBox fechaTituloHolder = new HBox(fechaTitulo);
        fechaTituloHolder.getStyleClass().add("PF-tituloHolder");

        Label fechaContraidaLabel = new Label("Fecha contraida");
        Label fechaContraida = new Label(
                Utils.dateTimeFormatter(gasto.getFechaDeudaContraida().toLocalDate())
        );

        Label fechaSaldarLabel = new Label("Fecha a saldar");
        Label fechaSaldar = new Label(
                Utils.dateTimeFormatter(gasto.getFechaVencimiento().toLocalDate())
        );

        GridPane fechasGrid = new GridPane();
        fechasGrid.setHgap(10);
        fechasGrid.add(
                new VBox(fechaContraidaLabel, fechaContraida), 0, 0
        );
        fechasGrid.add(
                new VBox(fechaSaldarLabel, fechaSaldar), 1, 0
        );

        VBox fechaBox = new VBox(fechaTituloHolder, fechasGrid);
        fechaBox.setMaxWidth(Double.MAX_VALUE);


        // PROVEEDOR
        Label proveedorTitulo = new Label("Proveedor");
        proveedorTitulo.getStyleClass().add("PF-titulo");
        HBox proveedorTituloHolder = new HBox(proveedorTitulo);
        proveedorTituloHolder.getStyleClass().add("PF-tituloHolder");

        Label proveedorNombre = new Label(gasto.getProveedor().getNombre());
        Label proveedorTelefono = new Label(gasto.getProveedor().getTelefono());

        VBox proveedorHolder = new VBox(proveedorNombre, proveedorTelefono);
        proveedorHolder.getStyleClass().add("PF-contentHolder");

        VBox proveedorBox = new VBox(proveedorTituloHolder, proveedorHolder);
        proveedorBox.setMaxWidth(Double.MAX_VALUE);


        // NOTA
        Label notaTitulo = new Label("Nota");
        notaTitulo.getStyleClass().add("PF-titulo");
        HBox notaTituloHolder = new HBox(notaTitulo);
        notaTituloHolder.getStyleClass().add("PF-tituloHolder");

        Label nota = new Label(gasto.getNota());
        nota.setWrapText(true);
        nota.setMaxWidth(180);

        HBox notaHolder = new HBox(nota);
        notaHolder.getStyleClass().add("PF-contentHolder");

        VBox notaBox = new VBox(notaTituloHolder, notaHolder);
        notaBox.setMaxWidth(Double.MAX_VALUE);


        // MONTO
        Label montoTitulo = new Label("Monto");
        montoTitulo.getStyleClass().add("PF-titulo");
        HBox montoTituloHolder = new HBox(montoTitulo);
        montoTituloHolder.getStyleClass().add("PF-tituloHolder");

        Label monto = new Label("$" + Utils.bigDecimalFormatter(gasto.getMonto()));
        HBox montoHolder = new HBox(monto);
        montoHolder.getStyleClass().add("PF-contentHolder");

        VBox montoBox = new VBox(montoTituloHolder, montoHolder);
        montoBox.setMaxWidth(Double.MAX_VALUE);


        // MONTO RESTANTE
        Label montoRestanteTitulo = new Label("Monto restante");
        montoRestanteTitulo.getStyleClass().add("PF-titulo");
        HBox montoRestanteTituloHolder = new HBox(montoRestanteTitulo);
        montoRestanteTituloHolder.getStyleClass().add("PF-tituloHolder");

        Label montoRestante = new Label(
                "$" + Utils.bigDecimalFormatter(gasto.getMontoRestante())
        );
        HBox montoRestanteHolder = new HBox(montoRestante);
        montoRestanteHolder.getStyleClass().add("PF-contentHolder");

        VBox montoRestanteBox = new VBox(
                montoRestanteTituloHolder,
                montoRestanteHolder
        );
        montoRestanteBox.setMaxWidth(Double.MAX_VALUE);


        // AGREGAR AL GRID
        gastoInfo.add(fechaBox, 0, 0);
        gastoInfo.add(proveedorBox, 1, 0);
        gastoInfo.add(notaBox, 2, 0);
        gastoInfo.add(montoBox, 3, 0);
        gastoInfo.add(montoRestanteBox, 4, 0);

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


        GridPane pagoGrid = new GridPane();
        pagoGrid.add(new Label("Fecha:"), 0, 0);
        pagoGrid.add(fechaField, 1, 0);
        pagoGrid.add(new Label("Monto:"), 0, 1);
        pagoGrid.add(montoField, 1, 1);

        pagoGrid.setHgap(10);
        pagoGrid.setVgap(15);

        this.getChildren().addAll(
                new Region(),
                pagoFormTitulo,
                new Region(),
                gastoInfo,
                new Region(),
                pagoGrid,
                new Region(),
                buttonHolder
        );

        this.getChildren().forEach(n -> {
            if (n instanceof Region r){
                r.setMaxWidth(Double.MAX_VALUE);
                VBox.setVgrow(r, Priority.ALWAYS);
            }
        });

        setMaxWidth(Region.USE_PREF_SIZE);
        setPrefWidth(Region.USE_COMPUTED_SIZE);
        setMaxHeight(Region.USE_PREF_SIZE);
        setPrefHeight(Region.USE_COMPUTED_SIZE);
    }
}
