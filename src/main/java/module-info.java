module com.negocio.adris {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires jakarta.validation;
    requires jakarta.inject;
    requires com.google.guice;
    requires org.hibernate.validator;
    requires java.desktop;
    opens com.negocio.adris.model.dtos to org.hibernate.validator;

    exports com.negocio.adris;

    exports com.negocio.adris.model.repositories;
    exports com.negocio.adris.model.service;
    exports com.negocio.adris.model.entities;
    exports com.negocio.adris.viewmodel;

    exports com.negocio.adris.model.dtos;

    exports com.negocio.adris.model.exceptions;

    exports com.negocio.adris.config to com.google.guice;
    exports com.negocio.adris.model.enums;
}