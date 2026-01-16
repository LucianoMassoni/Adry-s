package com.negocio.adris.model.repositories;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.negocio.adris.model.entities.Venta;
import com.negocio.adris.model.enums.FormaDePago;
import com.negocio.adris.model.exceptions.VentaNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VentaRepositoryImpl implements VentaRepository{
    private final Provider<Connection> connectionProvider;

    @Inject
    public VentaRepositoryImpl(Provider<Connection> connectionProvider){
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void save(Venta v) {
        String sql = """
                INSERT INTO Venta(
                    forma_de_pago, fecha, total, activo
                )
                VALUES (
                    ?,?,?, 1
                )
                """;

        try (Connection conn = connectionProvider.get();
             PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

                preparedStatement.setString(1, v.getFormaDePago().name());
            preparedStatement.setString(2, v.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            preparedStatement.setBigDecimal(3, v.getTotal());

            preparedStatement.executeUpdate();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    v.setId(rs.getLong(1)); // Asigna el ID generado
                } else {
                    throw new SQLException("Fallo al obtener el ID generado para Venta.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update(Venta v) {
        String sql = """
                UPDATE Venta SET
                    forma_de_pago = ?,
                    fecha = ?,
                    total = ?
                WHERE id = ?
                """;

        try (Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){

            preparedStatement.setString(1, v.getFormaDePago().name());
            preparedStatement.setString(2, v.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            preparedStatement.setBigDecimal(3, v.getTotal());
            preparedStatement.setLong(4, v.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar venta" + e.getMessage(), e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "UPDATE Venta SET activo = 0 WHERE id = ?";

        try(Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar una Venta por Id" + e.getMessage(), e);
        }
    }

    @Override
    public Venta findById(long id) throws VentaNotFoundException {
        String sql = "SELECT * FROM Venta WHERE id = ? AND activo = 1";

        try (Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()){
                throw new VentaNotFoundException("Error al encontrar una venta con id = " + id);
            }

            return new Venta(
                    resultSet.getLong("id"),
                    FormaDePago.valueOf(resultSet.getString("forma_de_pago")),
                    LocalDateTime.parse(resultSet.getString("fecha"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    resultSet.getBigDecimal("total")
            );

        } catch (SQLException e) {
            throw new RuntimeException("error al encontrar una venta por id" + e.getMessage(), e);
        }
    }

    @Override
    public List<Venta> findAll() throws VentaNotFoundException {
        String sql = "SELECT * FROM Venta WHERE activo = 1";
        List<Venta> list = new ArrayList<>();

        try (Connection conn = connectionProvider.get();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)){


            while (resultSet.next()){
                Venta v = new Venta(
                        resultSet.getLong("id"),
                        FormaDePago.valueOf(resultSet.getString("forma_de_pago")),
                        LocalDateTime.parse(resultSet.getString("fecha"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        resultSet.getBigDecimal("total")
                );
                list.add(v);
            }

            if (list.isEmpty()) throw new VentaNotFoundException("No se encontraron ventas");

            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Error al encontrar ar Ventas" + e.getMessage(), e);
        }
    }

    @Override
    public List<Venta> getAllVentasByFecha(String fecha) {
        String sql = String.format("SELECT * FROM Venta WHERE instr(fecha, '%s') > 0 AND activo = 1;", fecha);

        List<Venta> list = new ArrayList<>();

        try (Connection conn = connectionProvider.get();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql))
        {
            while (resultSet.next()){
                Venta v = new Venta(
                        resultSet.getLong("id"),
                        FormaDePago.valueOf(resultSet.getString("forma_de_pago")),
                        LocalDateTime.parse(resultSet.getString("fecha"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        resultSet.getBigDecimal("total")
                );
                list.add(v);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error al encontar las ventas del " + fecha + "\n" + e.getMessage(), e);
        }
    }
}
