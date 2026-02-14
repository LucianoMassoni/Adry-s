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
                        nombre, marca, peso, peso_actual, unidad_medida,cantidad, costo, ganancia, precio, tipo, es_divisible, activo
                    )
                    VALUES(
                        ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1
                    )
                """;

        try(Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
          preparedStatement.setString(1, p.getNombre());
          preparedStatement.setString(2, p.getMarca());
          preparedStatement.setDouble(3, p.getPeso());
          preparedStatement.setDouble(4, p.getPesoActual());
          if (p.getUnidadMedida() == null) {
              preparedStatement.setNull(5, Types.VARCHAR);
          } else {
              preparedStatement.setString(5, p.getUnidadMedida().name());
          }
          preparedStatement.setInt(6, p.getCantidad());
          preparedStatement.setBigDecimal(7, p.getCosto());
          preparedStatement.setBigDecimal(8, p.getGanancia());
          preparedStatement.setBigDecimal(9, p.getPrecio());
          if (p.getTipo() == null){
              preparedStatement.setNull(10, Types.VARCHAR);
          } else {
              preparedStatement.setString(10, p.getTipo().name());
          }
          preparedStatement.setBoolean(11, p.esDivisible());

          preparedStatement.executeUpdate();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getLong(1)); // Asigna el ID generado
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar producto " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Producto p) {
            String sql = """
                    UPDATE Producto SET
                        nombre = ?,
                        marca = ?,
                        peso = ?,
                        peso_actual = ?,
                        unidad_medida = ?,
                        cantidad = ?,
                        costo = ?,
                        ganancia = ?,
                        precio = ?,
                        tipo = ?,
                        es_divisible = ?
                    WHERE id = ?
                """;

            try(Connection conn = connectionProvider.get();
                PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

                preparedStatement.setString(1, p.getNombre());
                preparedStatement.setString(2, p.getMarca());
                preparedStatement.setDouble(3, p.getPeso());
                preparedStatement.setDouble(4, p.getPesoActual());
                if (p.getUnidadMedida() == null) {
                    preparedStatement.setNull(5, Types.VARCHAR);
                } else {
                    preparedStatement.setString(5, p.getUnidadMedida().name());
                }
                preparedStatement.setInt(6, p.getCantidad());
                preparedStatement.setBigDecimal(7, p.getCosto());
                preparedStatement.setBigDecimal(8, p.getGanancia());
                preparedStatement.setBigDecimal(9, p.getPrecio());
                if (p.getTipo() == null){
                    preparedStatement.setNull(10, Types.VARCHAR);
                } else {
                    preparedStatement.setString(10, p.getTipo().name());
                }
                preparedStatement.setBoolean(11, p.esDivisible());
                preparedStatement.setLong(12, p.getId());

                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException("Error al actualizar el producto " + e.getMessage(), e);
            }

    }

    @Override
    public void delete(long id) {
        String sql = "UPDATE Producto SET activo = 0 WHERE id = ?";

        try(Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar un producto por Id " + e.getMessage(), e);
        }
    }

    @Override
    public Producto findById(long id) throws ProductoNotFoundException {
        String sql = "SELECT * FROM Producto WHERE id = ? AND activo = 1";

        try(Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()){
                throw new ProductoNotFoundException("Producto con ID " + id + " no encontrado");
            }

            String unidadMedidaStr = resultSet.getString("unidad_medida");
            UnidadMedida unidadMedida = (unidadMedidaStr == null) ? null : UnidadMedida.valueOf(unidadMedidaStr);
            String tipoStr = resultSet.getString("tipo");
            TipoProducto tipo = (tipoStr == null) ? null : TipoProducto.valueOf(tipoStr);

            return new Producto(
                 resultSet.getLong("id"),
                 resultSet.getString("nombre"),
                 resultSet.getString("marca"),
                 resultSet.getDouble("peso"),
                 resultSet.getDouble("peso_actual"),
                 unidadMedida,
                 resultSet.getInt("cantidad"),
                 resultSet.getBigDecimal("costo"),
                 resultSet.getBigDecimal("ganancia"),
                 resultSet.getBigDecimal("precio"),
                 tipo,
                 resultSet.getBoolean("es_divisible")
             );

        } catch (SQLException e) {
            throw new RuntimeException("Error al encontrar un producto por Id " + e.getMessage(), e);
        }
    }

    @Override
    public List<Producto> findAll() {
        String sql = "SELECT * FROM Producto WHERE activo = 1 ORDER BY id;";

        try(Connection conn = connectionProvider.get();
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){

            List<Producto> productos = new ArrayList<>();

            while (resultSet.next()){
                String unidadMedidaStr = resultSet.getString("unidad_medida");
                UnidadMedida unidadMedida = (unidadMedidaStr == null) ? null : UnidadMedida.valueOf(unidadMedidaStr);
                String tipoStr = resultSet.getString("tipo");
                TipoProducto tipo = (tipoStr == null) ? null : TipoProducto.valueOf(tipoStr);

                Producto p = new Producto(
                    resultSet.getLong("id"),
                    resultSet.getString("nombre"),
                    resultSet.getString("marca"),
                    resultSet.getDouble("peso"),
                    resultSet.getDouble("peso_actual"),
                    unidadMedida,
                    resultSet.getInt("cantidad"),
                    resultSet.getBigDecimal("costo"),
                    resultSet.getBigDecimal("ganancia"),
                    resultSet.getBigDecimal("precio"),
                    tipo,
                    resultSet.getBoolean("es_divisible")
                );
                productos.add(p);
            }
            return productos;

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todos los productos " + e.getMessage(), e);
        }
    }
}
