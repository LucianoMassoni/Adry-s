package com.negocio.adris.model.repositories;

import com.negocio.adris.config.DBConnection;
import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.entities.TipoProducto;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepositoryImpl implements ProductoRepository {
    @Override
    public void save(Producto p) {
        String sql = """
                    INSERT INTO Producto(
                        nombre, marca, peso, cantidad, costo, ganancia, precio, tipo, fecha_vencimiento
                    )
                    VALUES(
                        ?, ?, ?, ?, ?, ?, ?, ?, ?
                    )
                """;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
          preparedStatement.setString(1, p.getNombre());
          preparedStatement.setString(2, p.getMarca());
          preparedStatement.setDouble(3, p.getPeso());
          preparedStatement.setInt(4, p.getCantidad());
          preparedStatement.setDouble(5, p.getCosto());
          preparedStatement.setDouble(6, p.getGanancia());
          preparedStatement.setDouble(7, p.getPrecio());
          preparedStatement.setString(8,p.getTipo().toString());
          preparedStatement.setDate(9, Date.valueOf(p.getFechaVencimiento()));

          preparedStatement.executeUpdate();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    p.setId(rs.getInt(1)); // Asigna el ID generado
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar producto" + e.getMessage(), e);
        }
    }

    @Override
    public void update(Producto p) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public Producto findById(int id) {
        return null;
    }

    @Override
    public List<Producto> findAll() {
        List<Producto> productos = new ArrayList<>();

        String sql = """
                    SELECT * FROM Producto
                """;

        try(Connection conn = DBConnection.getConnection();
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql))
        {

            TipoProducto tipo = null;
            while (resultSet.next()){
                for (TipoProducto tipoProducto : TipoProducto.values()) {
                    if (tipoProducto.toString().equals(resultSet.getString("tipo"))){
                         tipo = tipoProducto;
                    }
                }

                Date sqlDate = resultSet.getDate("fecha_vencimiento");
                LocalDate localDate = sqlDate.toLocalDate();

                Producto p = new Producto(
                        resultSet.getInt("id"),
                        resultSet.getString("nombre"),
                        resultSet.getString("marca"),
                        resultSet.getDouble("peso"),
                        resultSet.getInt("cantidad"),
                        resultSet.getDouble("costo"),
                        resultSet.getDouble("ganancia"),
                        resultSet.getDouble("precio"),
                        tipo,
                        localDate
                );
                productos.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todos los productos" + e);
        }

        return productos;
    }
}
