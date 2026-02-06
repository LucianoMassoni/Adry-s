package com.negocio.adris.view;

import com.negocio.adris.utils.LabelNegrita;
import com.negocio.adris.utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

public class BalanceCard extends VBox {

    public BalanceCard(String titulo, ObservableValue<BigDecimal> ganancia, ObservableValue<BigDecimal> perdida){
        getStyleClass().add("balanceCard");

        Label tituloLabel = new LabelNegrita(titulo);
        tituloLabel.getStyleClass().add("balanceCard-titulo");
        Label gananciaLabel = new Label();
        Label perdidaLabel = new Label();
        Region r = new Region();
        r.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(r, Priority.ALWAYS);
        HBox balanceHolder = new HBox(gananciaLabel, r, perdidaLabel);
        Label totalLabel = new Label();

        gananciaLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> "$ " + Utils.bigDecimalFormatter(ganancia.getValue()),
                        ganancia
                )
        );

        perdidaLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> "$ -" + Utils.bigDecimalFormatter(perdida.getValue()),
                        perdida
                )
        );

        totalLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> "$ " +
                                Utils.bigDecimalFormatter(ganancia.getValue().subtract(perdida.getValue())),
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
