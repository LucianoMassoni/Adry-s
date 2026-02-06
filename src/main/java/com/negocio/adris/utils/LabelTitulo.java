package com.negocio.adris.utils;

import javafx.scene.control.Label;

public class LabelTitulo extends Label {

    public LabelTitulo(String s){
        super(s);
        this.getStyleClass().add("label-titulo");
    }

    public LabelTitulo(String s, boolean centered){
        super(s);
        this.getStyleClass().add("label-titulo");
        this.setStyle("""
                -fx-alignment: center;
                -fx-text-alignment: center;
                """);
    }
}
