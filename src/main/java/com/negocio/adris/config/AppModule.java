package com.negocio.adris.config;

import com.google.inject.AbstractModule;
import com.negocio.adris.model.repositories.ProductoRepository;
import com.negocio.adris.model.repositories.ProductoRepositoryImpl;
import com.negocio.adris.model.service.ProductoService;
import com.negocio.adris.model.service.ProductoServiceImpl;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class AppModule extends AbstractModule {
    @Override
    protected void configure(){
        bind(ProductoRepository.class).to(ProductoRepositoryImpl.class);
        bind(ProductoService.class).to(ProductoServiceImpl.class);
        bind(Validator.class).toProvider(() -> Validation.buildDefaultValidatorFactory().getValidator());
    }
}
