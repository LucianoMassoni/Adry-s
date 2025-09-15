package com.negocio.adris.config;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.negocio.adris.model.repositories.*;
import com.negocio.adris.model.service.*;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.sql.Connection;

public class AppModule extends AbstractModule {
    @Override
    protected void configure(){
        // Configuración de repositorios
        bind(ProductoRepository.class).to(ProductoRepositoryImpl.class).in(Singleton.class);
        bind(DetalleVentaRepository.class).to(DetalleVentaRepositoryImpl.class).in(Singleton.class);
        bind(VentaRepository.class).to(VentaRepositoryImpl.class).in(Singleton.class);
        //bind(CuentaRepository.class).to(CuentaRepositoryImpl.class).in(Singleton.class);

        // Configuración de servicios
        bind(ProductoService.class).to(ProductoServiceImpl.class);
        bind(DetalleVentaService.class).to(DetalleVentaServiceImpl.class);
        bind(VentaService.class).to(VentaServiceImpl.class);
        //bind(CuentaService.class).to(CuentaServiceImpl.class);

        // Configuración de Validator
        bind(Validator.class).toProvider(() -> Validation.buildDefaultValidatorFactory().getValidator());

        // Configuración de base de datos   
        bind(Connection.class).toProvider(DBConnection.class);
    }
}
