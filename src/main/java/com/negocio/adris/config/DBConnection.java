package com.negocio.adris.config;

import com.google.inject.Provider;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection implements Provider<Connection> {
//    private static final String URL = "jdbc:sqlite:fiambreria.db";

    private static final String URL;

    static {
        String userHome = System.getProperty("user.home");
        String appDir = userHome + File.separator + ".fiambreria";
        new File(appDir).mkdirs(); // crea la carpeta si no existe
        URL = "jdbc:sqlite:" + appDir + File.separator + "fiambreria.db";
    }

    @Override
    public Connection get() {
        try {
            Connection conn = DriverManager.getConnection(URL);
            enableForeignKeys(conn);
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar a la BD" + e.getMessage() + e);
        }
    }

    private void enableForeignKeys(Connection conn) throws SQLException {
        try (Statement statement = conn.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON;");
        }
    }
}
