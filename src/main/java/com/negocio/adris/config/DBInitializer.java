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
                        tipo TEXT NOT NULL,
                        peso DOUBLE NOT NULL,
                        cantidad INTEGER NOT NULL,
                        costo DOUBLE NOT NULL,
                        ganancia DOUBLE NOT NULL,
                        precio DOUBLE NOT NULL,
                        fecha_vencimiento DATE NOT NULL
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
