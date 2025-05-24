package com.negocio.adris.config;

import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection implements Provider<Connection> {
    private static final String URL = "jdbc:sqlite:fiambreria.db";

    @Override
    public Connection get() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar a la BD" + e.getMessage() + e);
        }
    }
}
