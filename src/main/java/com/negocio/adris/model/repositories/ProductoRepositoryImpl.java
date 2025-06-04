package com.negocio.adris.model.repositories;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.enums.TipoProducto;
import com.negocio.adris.model.enums.UnidadMedida;
import com.negocio.adris.model.exceptions.ProductoNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepositoryImpl implements ProductoRepository {
    private final Provider<Connection> connectionProvider;

    @Inject
    public ProductoRepositoryImpl(Provider<Connection> connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void save(Producto p) {
        String sql = """
                    INSERT INTO Producto(
                        nombre, marca, peso, unidad_medida,cantidad, costo, ganancia, precio, tipo, fecha_vencimiento
                    )
                    VALUES(
                        ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
                    )
                """;

        try(Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
          preparedStatement.setString(1, p.getNombre());
          preparedStatement.setString(2, p.getMarca());
          preparedStatement.setDouble(3, p.getPeso());
          preparedStatement.setString(4, p.getUnidadMedida().name());
          preparedStatement.setInt(5, p.getCantidad());
          preparedStatement.setBigDecimal(6, p.getCosto());
          preparedStatement.setBigDecimal(7, p.getGanancia());
          preparedStatement.setBigDecimal(8, p.getPrecio());
          preparedStatement.setString(9,p.getTipo().name());
          preparedStatement.setDate(10, Date.valueOf(p.getFechaVencimiento()));

          preparedStatement.executeUpdate();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    p.setId(rs.getLong(1)); // Asigna el ID generado
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar producto" + e.getMessage(), e);
        }
    }

    @Override
    public void update(Producto p) {
            String sql = """
                    UPDATE Producto SET
                        nombre = ?,
                        marca = ?,
                        peso = ?,
                        unidad_medida = ?,
                        cantidad = ?,
                        costo = ?,
                        ganancia = ?,
                        precio = ?,
                        tipo = ?,
                        fecha_vencimiento = ?
                    WHERE id = ?
                """;

            try(Connection conn = connectionProvider.get();
                PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

                preparedStatement.setString(1, p.getNombre());
                preparedStatement.setString(2, p.getMarca());
                preparedStatement.setDouble(3, p.getPeso());
                preparedStatement.setInt(4, p.getCantidad());
                preparedStatement.setString(4, p.getUnidadMedida().name());
                preparedStatement.setInt(5, p.getCantidad());
                preparedStatement.setBigDecimal(6, p.getCosto());
                preparedStatement.setBigDecimal(7, p.getGanancia());
                preparedStatement.setBigDecimal(8, p.getPrecio());
                preparedStatement.setString(9,p.getTipo().name());
                preparedStatement.setDate(10, Date.valueOf(p.getFechaVencimiento()));
                preparedStatement.setLong(11, p.getId());

                preparedStatement.executeUpdate();


            } catch (SQLException e) {
                throw new RuntimeException("Error al actualizar el producto" + e.getMessage(), e);
            }

    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Producto WHERE id = ?";

        try(Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar un producto por Id" + e.getMessage(), e);
        }
    }

    @Override
    public Producto findById(int id) throws ProductoNotFoundException {
        String sql = "SELECT * FROM Producto WHERE id = ?";

        try(Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()){
                throw new ProductoNotFoundException("Producto con ID " + id + " no encontrado");
            }

             return new Producto(
                 resultSet.getLong("id"),
                 resultSet.getString("nombre"),
                 resultSet.getString("marca"),
                 resultSet.getDouble("peso"),
                 UnidadMedida.valueOf(resultSet.getString("unidad_medida")),
                 resultSet.getInt("cantidad"),
                 resultSet.getBigDecimal("costo"),
                 resultSet.getBigDecimal("ganancia"),
                 resultSet.getBigDecimal("precio"),
                 TipoProducto.valueOf(resultSet.getString("tipo")),
                 resultSet.getDate("fecha_vencimiento").toLocalDate()
             );

        } catch (SQLException e) {
            throw new RuntimeException("Error al encontrar un producto por Id" + e.getMessage(), e);
        }
    }

    @Override
    public List<Producto> findAll() throws ProductoNotFoundException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto";

        try(Connection conn = connectionProvider.get();
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){

            while (resultSet.next()){
                Producto p = new Producto(
                    resultSet.getLong("id"),
                    resultSet.getString("nombre"),
                    resultSet.getString("marca"),
                    resultSet.getDouble("peso"),
                    UnidadMedida.valueOf(resultSet.getString("unidad_medida")),
                    resultSet.getInt("cantidad"),
                    resultSet.getBigDecimal("costo"),
                    resultSet.getBigDecimal("ganancia"),
                    resultSet.getBigDecimal("precio"),
                    TipoProducto.valueOf(resultSet.getString("tipo")),
                    resultSet.getDate("fecha_vencimiento").toLocalDate()
                );
                productos.add(p);
            }
            if (productos.isEmpty()) throw new ProductoNotFoundException("No hay productos cargados");
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todos los productos" + e.getMessage(), e);
        }

        return productos;
    }


}
