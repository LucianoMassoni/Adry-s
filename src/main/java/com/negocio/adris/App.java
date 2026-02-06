package com.negocio.adris;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.negocio.adris.config.AppModule;
import com.negocio.adris.config.DBInitializer;
import com.negocio.adris.model.exceptions.VentaNotFoundException;
import com.negocio.adris.view.*;
import com.negocio.adris.viewmodel.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Objects;


public class App extends Application {
    private Injector injector;
    @Override
    public void init(){
        injector = Guice.createInjector(new AppModule());
        injector.getInstance(DBInitializer.class);

        Locale.setDefault(new Locale("es", "AR"));
    }

    @Override
    public void start(Stage stage) {
        ProductoViewModel productoViewModel = injector.getInstance(ProductoViewModel.class);
        VentaViewModel ventaViewModel = injector.getInstance(VentaViewModel.class);
        ProveedorViewModel proveedorViewModel = injector.getInstance(ProveedorViewModel.class);
        GastoViewModel gastoViewModel =  injector.getInstance(GastoViewModel.class);
        PagoViewModel pagoViewModel = injector.getInstance(PagoViewModel.class);

        BorderPane borderPane = new BorderPane();
        NavSideBar navSideBar = new NavSideBar();
        HBox navContainer = new HBox(navSideBar);
        navContainer.getStyleClass().add("nav-container");

        ProductoCenter productoCenter = new ProductoCenter(productoViewModel);
        BorderPane.setMargin(productoCenter, new Insets(0, 40, 0, 40));

        borderPane.setLeft(navContainer);

        VentaView ventaView = new VentaView(ventaViewModel, productoViewModel, ventaViewModel::agregarItem);

        navSideBar.setOnSectionSelected(nombre -> {
            switch (nombre){
                case "ventas" -> {
                    borderPane.setRight(null);
                    borderPane.setCenter(ventaView);
                }
                case "productos" -> {
                    borderPane.setCenter(productoCenter);
                }
                case "cuentas" -> {
                    borderPane.setCenter(new GastoCenter(gastoViewModel, proveedorViewModel, pagoViewModel));
                    borderPane.setRight(null);
                }
                case "dashboard" -> {
                    try {
                        borderPane.setCenter(new DashboardCenter(pagoViewModel, proveedorViewModel, ventaViewModel));
                    } catch (VentaNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    borderPane.setRight(null);
                }
            }
        });

        Scene scene = new Scene(borderPane, 1360, 768);
        borderPane.getStyleClass().add("app");

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/stylesheet.css")).toExternalForm());

        stage.setTitle("Adry's");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}