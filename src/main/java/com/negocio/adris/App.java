package com.negocio.adris;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.negocio.adris.config.AppModule;
import com.negocio.adris.config.DBInitializer;
import com.negocio.adris.view.*;
import com.negocio.adris.viewmodel.DetalleVentaViewModel;
import com.negocio.adris.viewmodel.ProductoViewModel;
import com.negocio.adris.viewmodel.VentaViewModel;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Objects;


public class App extends Application {
    private Injector injector;
    @Override
    public void init(){
        injector = Guice.createInjector(new AppModule());
        injector.getInstance(DBInitializer.class);
    }

    @Override
    public void start(Stage stage) {
        ProductoViewModel productoViewModel = injector.getInstance(ProductoViewModel.class);
        VentaViewModel ventaViewModel = injector.getInstance(VentaViewModel.class);
        DetalleVentaViewModel detalleVentaViewModel = injector.getInstance(DetalleVentaViewModel.class);

        BorderPane borderPane = new BorderPane();
        NavSideBar navSideBar = new NavSideBar();
        HBox navContainer = new HBox(navSideBar);
        navContainer.getStyleClass().add("nav-container");

        ProductoCenter productoCenter = new ProductoCenter(productoViewModel);
        BorderPane.setMargin(productoCenter, new Insets(0, 40, 0, 40));

        borderPane.setLeft(navContainer);

        VentaView ventaView = new VentaView(ventaViewModel, detalleVentaViewModel, productoViewModel, ventaViewModel::agregarItem);

        navSideBar.setOnSectionSelected(nombre -> {
            switch (nombre){
                case "ventas" -> {
                    borderPane.setRight(null);
                    borderPane.setCenter(ventaView);
                }
                case "productos" -> {
                    borderPane.setCenter(productoCenter);
                    borderPane.setRight(new ProductoForm(productoViewModel));
                }
                case "cuentas" -> {
                    borderPane.setCenter(new Text("cuentas"));
                    borderPane.setRight(null);
                }
                case "dashboard" -> {
                    borderPane.setCenter(new Text("dashboard"));
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