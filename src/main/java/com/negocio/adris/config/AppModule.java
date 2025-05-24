package com.negocio.adris.config;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.negocio.adris.model.repositories.ProductoRepository;
import com.negocio.adris.model.repositories.ProductoRepositoryImpl;
import com.negocio.adris.model.service.ProductoService;
import com.negocio.adris.model.service.ProductoServiceImpl;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.sql.Connection;

public class AppModule extends AbstractModule {
    @Override
    protected void configure(){
        // Configuración de repositorios
        bind(ProductoRepository.class).to(ProductoRepositoryImpl.class).in(Singleton.class);

        // Configuración de servicios
        bind(ProductoService.class).to(ProductoServiceImpl.class);

        // Configuración de Validator
        bind(Validator.class).toProvider(() -> Validation.buildDefaultValidatorFactory().getValidator());

        // Configuración de base de datos   
        bind(Connection.class).toProvider(DBConnection.class);
    }
}
