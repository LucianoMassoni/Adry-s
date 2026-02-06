package com.negocio.adris.utils;

import javafx.scene.control.Label;

public class LabelNegrita extends Label {

    public LabelNegrita(String s){
        super(s);
        this.setStyle("-fx-font-weight: bold;");
    }
}
