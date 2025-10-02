package com.negocio.adris.config;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBInitializer {
    private final Provider<Connection> connectionProvider;

    @Inject
    public DBInitializer(Provider<Connection> connectionProvider){
        this.connectionProvider = connectionProvider;
        initialize();
    }

    public void initialize(){
        String sql = """
                    CREATE TABLE IF NOT EXISTS Producto(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre TEXT NOT NULL,
                        marca TEXT NOT NULL,
                        tipo TEXT,
                        peso DOUBLE(10, 2),
                        peso_actual DOUBLE(10, 2),
                        unidad_medida TEXT,
                        cantidad INTEGER,
                        costo DOUBLE(10, 2),
                        ganancia DOUBLE(10, 2),
                        precio DOUBLE(10, 2),
                        es_divisible INTEGER,
                        activo INTEGER NOT NULL DEFAULT 1
                    );
                
                    CREATE TABLE IF NOT EXISTS Venta(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        forma_de_pago TEXT NOT NULL,
                        total DOUBLE(10, 2) NOT NULL,
                        fecha TEXT NOT NULL
                    );
                
                    CREATE TABLE IF NOT EXISTS DetalleVenta(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        id_venta INTEGER NOT NULL,
                        id_producto INTEGER NOT NULL,
                        cantidad DOUBLE(10, 2),
                        precio_unitario DOUBLE ,
                        descuento DOUBLE(10, 2) DEFAULT 0.0,
                        subtotal DOUBLE(10, 2) NOT NULL,
                        FOREIGN KEY(id_producto) REFERENCES Producto(id),
                        FOREIGN KEY(id_venta) REFERENCES Venta(id)
                    );
                
                    CREATE TABLE IF NOT EXISTS Proveedor(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre TEXT NOT NULL,
                        telefono TEXT,
                        activo INTEGER DEFAULT 1
                    );
                
                    CREATE TABLE IF NOT EXISTS Gasto(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        id_proveedor INTEGER NOT NULL,
                        fecha_deuda_contraida TEXT,
                        fecha_vencimiento TEXT,
                        monto DOUBLE(10, 2),
                        nota TEXT,
                        saldado INTEGER,
                        activo INTEGER DEFAULT 1,
                        FOREIGN KEY(id_proveedor) REFERENCES Proveedor(id)
                    );
                
                    CREATE TABLE IF NOT EXISTS Pago(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        id_gasto INTEGER,
                        fecha_pago TEXT,
                        monto_pagado DOUBLE(10, 2),
                        activo INTEGER DEFAULT 1,
                        FOREIGN KEY(id_gasto) REFERENCES Gasto(id)
                    );
                """;

        try(Connection connection = connectionProvider.get();
            Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar la base de datos" + e);
        }
    }
}
