package com.negocio.adris.utils;

import javafx.scene.control.Button;

public class BotonAfirmar extends Button {
    public BotonAfirmar(){
        super();
        this.setText("Aceptar");
        this.getStyleClass().add("boton-aceptar");
    }

    public BotonAfirmar(String txt){
        super(txt);
        this.getStyleClass().add("boton-aceptar");
    }
}
