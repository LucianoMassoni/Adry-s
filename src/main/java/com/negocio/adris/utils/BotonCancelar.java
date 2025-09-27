package com.negocio.adris.utils;

import javafx.scene.control.Button;

public class BotonCancelar extends Button {
    public BotonCancelar(){
        super();
        this.setText("Cancelar");
        this.getStyleClass().add("boton-cancelar");
    }
}
