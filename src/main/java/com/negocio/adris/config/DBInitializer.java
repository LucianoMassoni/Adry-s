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
                        peso DOUBLE(10, 2) NOT NULL,
                        peso_actual DOUBLE(10, 2) NOT NULL,
                        unidad_medida TEXT NOT NULL,
                        cantidad INTEGER NOT NULL,
                        costo DOUBLE(10, 2) NOT NULL,
                        ganancia DOUBLE(10, 2) NOT NULL,
                        precio DOUBLE(10, 2) NOT NULL,
                        es_divisible INTEGER
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
                        cantidad INTEGER NOT NULL,
                        precio_unitario DOUBLE NOT NULL,
                        descuento DOUBLE(10, 2) DEFAULT 0.0,
                        subtotal DOUBLE(10, 2) NOT NULL,
                        FOREIGN KEY(id_producto) REFERENCES Producto(id),
                        FOREIGN KEY(id_venta) REFERENCES Venta(id)
                    );
                
                    CREATE TABLE IF NOT EXISTS Cuenta(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        tipo_cuenta TEXT NOT NULL,
                        detalle TEXT NOT NULL,
                        debe DOUBLE(10, 2) NOT NULL,
                        haber DOUBLE(10, 2) NOT NULL,
                        fecha TEXT NOT NULL
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
