module com.negocio.adris {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.negocio.adris to javafx.fxml;
    exports com.negocio.adris;
}