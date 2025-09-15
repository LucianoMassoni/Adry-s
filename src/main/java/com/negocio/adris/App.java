package com.negocio.adris;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.negocio.adris.config.AppModule;
import com.negocio.adris.view.*;
import com.negocio.adris.viewmodel.DetalleVentaViewModel;
import com.negocio.adris.viewmodel.ProductoViewModel;
import com.negocio.adris.viewmodel.VentaViewModel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Objects;


public class App extends Application {
    private Injector injector;
    @Override
    public void init(){
        injector = Guice.createInjector(new AppModule());
    }

    @Override
    public void start(Stage stage) {
        ProductoViewModel productoViewModel = injector.getInstance(ProductoViewModel.class);
        VentaViewModel ventaViewModel = injector.getInstance(VentaViewModel.class);
        DetalleVentaViewModel detalleVentaViewModel = injector.getInstance(DetalleVentaViewModel.class);

        BorderPane borderPane = new BorderPane();
        NavSideBar navSideBar = new NavSideBar();

        VentaCenter ventaCenter = new VentaCenter(ventaViewModel);
        DetalleVentaForm detalleVentaForm = new DetalleVentaForm(detalleVentaViewModel, productoViewModel, ventaViewModel::agregarItem);

        borderPane.setLeft(navSideBar);

        navSideBar.setOnSectionSelected(nombre -> {
            switch (nombre){
                case "ventas" -> {
                    borderPane.setCenter(ventaCenter);
                    borderPane.setRight(detalleVentaForm);
                }
                case "productos" -> {
                    borderPane.setCenter(new ProductoCenter(productoViewModel));
                    borderPane.setRight(new ProductoForm(productoViewModel));
                }
                case "cuentas" -> borderPane.setCenter(new Text("cuentas"));
                case "dashboard" -> borderPane.setCenter(new Text("dashboard"));
            }
        });

        Scene scene = new Scene(borderPane, 1360, 768);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/stylesheet.css")).toExternalForm());

        stage.setTitle("Adry's");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}