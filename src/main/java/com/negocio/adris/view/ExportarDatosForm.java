package com.negocio.adris.view;

import com.google.inject.Inject;
import com.negocio.adris.utils.*;
import com.negocio.adris.viewmodel.BalanceVeiwModel;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;


public class ExportarDatosForm extends VBox {
    private final BalanceVeiwModel balanceVeiwModel;
    private final Runnable onClose;

    @Inject
    public ExportarDatosForm(BalanceVeiwModel balanceVeiwModel, Runnable onClose){
        this.balanceVeiwModel = balanceVeiwModel;
        this.onClose = onClose;

        getStyleClass().add("exportarDatosForm");

        Label titulo = new LabelTitulo("Exportar datos");
        HBox tituloHolder = new HBox(titulo);


        // mes
        Label mesLabel = new Label("Mes");
        ChoiceBox<Month> mesChoiceBox = new ChoiceBox<>();

        mesChoiceBox.getItems().addAll(Month.values());
        mesChoiceBox.setConverter(Formatters.mesFormatter());
        mesChoiceBox.setValue(LocalDate.now().getMonth().minus(1));

        // año
        Label anioLabel = new Label("Año");
        ChoiceBox<Integer> anioChoiceBox = new ChoiceBox<>();
        int anioActual = LocalDate.now().getYear();

        for( int i = 0; i < 10; i++){
            anioChoiceBox.getItems().add(anioActual + i);
        }

        anioChoiceBox.setValue(anioActual);

        GridPane gridPane = new GridPane();
        gridPane.add(mesLabel, 0, 0);
        gridPane.add(mesChoiceBox, 1, 0);
        gridPane.add(anioLabel, 0, 1);
        gridPane.add(anioChoiceBox, 1, 1);

        // Botones
        Region reg = new Region();
        reg.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(reg, Priority.ALWAYS);
        Button botonAceptar = new BotonAfirmar("Aceptar");
        Button botonCancelar = new BotonCancelar();
        HBox buttonHolder = new HBox(botonAceptar, reg, botonCancelar);

        botonAceptar.setOnAction(e ->{
            Month mes = mesChoiceBox.getValue();
            Integer anio = anioChoiceBox.getValue();

            YearMonth yearMonth = YearMonth.of(anio, mes);

            // task para ejecutar en segundo plano.
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    // llamo a exportar datos
                    balanceVeiwModel.exportarDatos(yearMonth);

                    return null;
                }
            };

            task.setOnFailed(event -> {
                Alert alert = new AdrysAlert(Alert.AlertType.ERROR, task.getException().getMessage(), ButtonType.OK);
                alert.showAndWait();
            });

            // el task necesita un Thread para funcionar.
            Thread thread = new Thread(task);
            thread.setDaemon(true); // Daemon: si se cierra la app el proceso muere incompleto. No deja la JVM corriendo.
            thread.start();

            onClose.run();
        });

        botonCancelar.setOnAction(e -> onClose.run());

        getChildren().addAll(
                tituloHolder,
                gridPane,
                buttonHolder
        );

        setMaxWidth(Region.USE_PREF_SIZE);
        setPrefWidth(Region.USE_COMPUTED_SIZE);
        setMaxHeight(Region.USE_PREF_SIZE);
        setPrefHeight(Region.USE_COMPUTED_SIZE);
    }
}
