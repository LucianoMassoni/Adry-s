package com.negocio.adris.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AdrysAlert extends Alert {
    public AdrysAlert(AlertType alertType) {
        super(alertType);
        initDefault();
    }

    public AdrysAlert(AlertType alertType, String s, ButtonType... buttonTypes) {
        super(alertType, s, buttonTypes);
        initDefault();
    }

    void initDefault(){
        this.setTitle("Adry's");
        this.setHeaderText(null);
    }
}
