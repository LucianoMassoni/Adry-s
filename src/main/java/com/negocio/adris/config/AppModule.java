package com.negocio.adris.config;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.negocio.adris.model.exporter.BalanceExcelExporter;
import com.negocio.adris.model.exporter.BalanceExcelExporterImpl;
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
        bind(VentaRepository.class).to(VentaRepositoryImpl.class).in(Singleton.class);
        bind(ProveedorRepository.class).to(ProveedorRepositoryImpl.class).in(Singleton.class);
        bind(GastoRepository.class).to(GastoRepositoryImpl.class).in(Singleton.class);
        bind(PagoRepository.class).to(PagoRepositoryImpl.class).in(Singleton.class);

        // Configuración de servicios
        bind(ProductoService.class).to(ProductoServiceImpl.class);
        bind(VentaService.class).to(VentaServiceImpl.class);
        bind(ProveedorService.class).to(ProveedorServiceImpl.class);
        bind(GastoService.class).to(GastoServiceImpl.class);
        bind(PagoService.class).to(PagoServiceImpl.class);

        // Configuración de Validator
        bind(Validator.class).toProvider(() -> Validation.buildDefaultValidatorFactory().getValidator());

        // Configuración de base de datos   
        bind(Connection.class).toProvider(DBConnection.class);

        // config balalnce
        bind(BalanceService.class).to(BalanceServiceImpl.class);
        bind(BalanceExcelExporter.class).to(BalanceExcelExporterImpl.class);
    }
}
