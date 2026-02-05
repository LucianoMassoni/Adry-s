package com.negocio.adris.view;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

public class BalanceCard extends VBox {

    // aca le pasa los valores, pero no estarian bindeados, por lo que no se actualizarian
    public BalanceCard(String titulo, ObservableValue<BigDecimal> ganancia, ObservableValue<BigDecimal> perdida){
        getStyleClass().add("balanceCard");

        Label tituloLabel = new Label(titulo);
        tituloLabel.getStyleClass().add("balanceCard-titulo");
        Label gananciaLabel = new Label();
        Label perdidaLabel = new Label();
        Region r = new Region();
        HBox balanceHolder = new HBox(gananciaLabel, r, perdidaLabel);
        Label totalLabel = new Label();

        gananciaLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> "$ " + ganancia.getValue(),
                        ganancia
                )
        );

        perdidaLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> "$ " + perdida.getValue(),
                        perdida
                )
        );

        totalLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> "$ " +
                                ganancia.getValue().subtract(perdida.getValue()),
                        ganancia, perdida
                )
        );

        getChildren().addAll(
                tituloLabel,
                balanceHolder,
                totalLabel
        );
    }
}
