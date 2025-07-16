package com.negocio.adris.model.repositories;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.negocio.adris.model.entities.DetalleVenta;
import com.negocio.adris.model.exceptions.DetalleVentaNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleVentaRepositoryImpl implements DetalleVentaRepository{
    private final Provider<Connection> connectionProvider;

    @Inject
    public DetalleVentaRepositoryImpl(Provider<Connection> connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void save(DetalleVenta detalleVenta) {
        String sql = """
                INSERT INTO DetalleVenta(
                 venta_id, producto_id, cantidad, precio_unitario, descuento, subtotal
                )
                VALUES(
                ?, ?, ?, ?, ?, ?
                )
                """;

        try(Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            preparedStatement.setLong(1, detalleVenta.getVentaId());
            preparedStatement.setLong(2, detalleVenta.getProductoId());
            preparedStatement.setInt(3, detalleVenta.getCantidad());
            preparedStatement.setBigDecimal(4, detalleVenta.getPrecioUnitario());
            preparedStatement.setBigDecimal(5, detalleVenta.getDescuento());
            preparedStatement.setBigDecimal(6, detalleVenta.getSubtotal());

            preparedStatement.executeUpdate();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    detalleVenta.setId(rs.getLong(1)); // Asigna el ID generado
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar DetalleVenta " + e.getMessage(), e);
        }
    }

    @Override
    public void update(DetalleVenta detalleVenta) {
        String sql = """
                UPDATE DetalleVenta SET
                    venta_id = ?,
                    producto_id = ?,
                    cantidad = ?,
                    precio_unitario = ?,
                    descuento = ?,
                    subtotal = ?
                WHERE id = ?
                """;

        try (Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setLong(1, detalleVenta.getVentaId());
            preparedStatement.setLong(2, detalleVenta.getProductoId());
            preparedStatement.setInt(3, detalleVenta.getCantidad());
            preparedStatement.setBigDecimal(4, detalleVenta.getPrecioUnitario());
            preparedStatement.setBigDecimal(5, detalleVenta.getDescuento());
            preparedStatement.setBigDecimal(6, detalleVenta.getSubtotal());
            preparedStatement.setLong(7, detalleVenta.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar DetalleVenta " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM DetalleVenta WHERE id = ?";

        try(Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar detalleVenta por Id " + e.getMessage(), e);
        }
    }

    @Override
    public DetalleVenta findById(long id) throws DetalleVentaNotFoundException {
        String sql = "SELECT * FROM DetalleVenta WHERE id = ?";

        try(Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()){
                throw new DetalleVentaNotFoundException("Error al encontrar detalleVenta con Id: " + id);
            }
            return new DetalleVenta(
                    resultSet.getLong("id"),
                    resultSet.getLong("venta_id"),
                    resultSet.getLong("producto_id"),
                    resultSet.getInt("cantidad"),
                    resultSet.getBigDecimal("precio_unitario"),
                    resultSet.getBigDecimal("descuento"),
                    resultSet.getBigDecimal("subtotal")
            );


        } catch (SQLException e) {
            throw new RuntimeException("Error al encontrar detalleVenta por Id " + e.getMessage(), e);
        }
    }

    @Override
    public List<DetalleVenta> findAll() throws DetalleVentaNotFoundException {
        String sql = "SELECT * FROM DetalleVenta";

        try (Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            ResultSet resultSet = preparedStatement.executeQuery();

            List<DetalleVenta> list = new ArrayList<>();

            while (resultSet.next()){
                DetalleVenta dv = new DetalleVenta(
                        resultSet.getLong("id"),
                        resultSet.getLong("venta_id"),
                        resultSet.getLong("producto_id"),
                        resultSet.getInt("cantidad"),
                        resultSet.getBigDecimal("precio_unitario"),
                        resultSet.getBigDecimal("descuento"),
                        resultSet.getBigDecimal("subtotal")
                );
                list.add(dv);
            }

            if (list.isEmpty()) throw new DetalleVentaNotFoundException("No hay DetalleVenta cargado");

            return list;

        } catch (SQLException e) {
            throw new RuntimeException("error al obtener lista de DetalleVenta " + e.getMessage(), e);
        }
    }

    @Override
    public List<DetalleVenta> findAllByVentaId(long ventaId) throws DetalleVentaNotFoundException {
        String sql = "SELECT * FROM DetalleVenta WHERE venta_id = ?";

        try (Connection conn = connectionProvider.get();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)){

            preparedStatement.setLong(1, ventaId);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<DetalleVenta> list = new ArrayList<>();

            while (resultSet.next()){
                DetalleVenta dv = new DetalleVenta(
                        resultSet.getLong("id"),
                        resultSet.getLong("venta_id"),
                        resultSet.getLong("producto_id"),
                        resultSet.getInt("cantidad"),
                        resultSet.getBigDecimal("precio_unitario"),
                        resultSet.getBigDecimal("descuento"),
                        resultSet.getBigDecimal("subtotal")
                );
                list.add(dv);
            }

            if (list.isEmpty()) throw new DetalleVentaNotFoundException("No hay DetalleVenta cargado para un id de venta: " + ventaId);

            return list;

        } catch (SQLException e) {
            throw new RuntimeException("error al obtener lista de DetalleVenta " + e.getMessage(), e);
        }
    }
}
