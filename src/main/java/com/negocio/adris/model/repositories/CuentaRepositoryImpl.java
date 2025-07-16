package com.negocio.adris.model.repositories;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.negocio.adris.model.entities.Cuenta;
import com.negocio.adris.model.enums.TipoCuenta;
import com.negocio.adris.model.exceptions.CuentaNotFoundException;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CuentaRepositoryImpl implements CuentaRepository{
    private final Provider<Connection> connectionProvider;

    @Inject
    public CuentaRepositoryImpl(Provider<Connection> connectionProvider) {
        this.connectionProvider = connectionProvider;
    }


    @Override
    public void save(Cuenta c) {
        String sql = """
                INSERT INTO Cuenta(
                    tipo_cuenta, detalle, debe, haber, fecha
                )
                VALUES(
                    ?, ?, ?, ?, ?
                )
                """;

        try(Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, c.getTipo().getSimbolo());
            preparedStatement.setString(2, c.getDetalle());
            preparedStatement.setBigDecimal(3, c.getDebe());
            preparedStatement.setBigDecimal(4, c.getHaber());
            preparedStatement.setString(5, c.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE));

            preparedStatement.executeUpdate();

            try(ResultSet rs = preparedStatement.getGeneratedKeys()){
                if (rs.next()){
                    c.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar Cuenta " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Cuenta c) {
        String sql = """
                UPDATE Cuenta SET
                    tipo_producto = ?,
                    detalle = ?,
                    debe = ?,
                    haber = ?,
                    fecha = ?
                WHERE id = ?
                """;

        try(Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, c.getTipo().getSimbolo());
            preparedStatement.setString(2, c.getDetalle());
            preparedStatement.setBigDecimal(3, c.getDebe());
            preparedStatement.setBigDecimal(4, c.getHaber());
            preparedStatement.setString(5, c.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE));
            preparedStatement.setLong(6, c.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar Cuenta " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM Cuenta WHERE id = ?";

        try (Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar Cuenta " + e.getMessage(), e);
        }
    }

    @Override
    public Cuenta findById(long id) throws CuentaNotFoundException {
        String sql = "SELECT * FROM Cuenta WHERE id = ?";

        try(Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()){
                throw new CuentaNotFoundException("Cuenta con id " + id + " no encontrado");
            }

            return new Cuenta(
                    resultSet.getLong("id"),
                    TipoCuenta.valueOf(resultSet.getString("tipo_cuenta")),
                    resultSet.getString("detalle"),
                    resultSet.getBigDecimal("debe"),
                    resultSet.getBigDecimal("haber"),
                    LocalDate.parse(resultSet.getString("fecha"), DateTimeFormatter.ISO_LOCAL_DATE)
                    );

        } catch (SQLException e) {
            throw new RuntimeException("Error al encontrar una Cuenta " + e.getMessage(), e);
        }
    }

    @Override
    public List<Cuenta> findAll() throws CuentaNotFoundException {
        List<Cuenta> list = new ArrayList<>();
        String sql = "SELECT * FROM Cuenta";

        try(Connection conn = connectionProvider.get();
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {

           while (resultSet.next()){
               Cuenta c = new Cuenta(
                       resultSet.getLong("id"),
                       TipoCuenta.valueOf(resultSet.getString("tipo_cuenta")),
                       resultSet.getString("detalle"),
                       resultSet.getBigDecimal("debe"),
                       resultSet.getBigDecimal("haber"),
                       LocalDate.parse(resultSet.getString("fecha"), DateTimeFormatter.ISO_LOCAL_DATE)
               );
               list.add(c);
           }
            if (list.isEmpty()) throw new CuentaNotFoundException("No hay cuentas cargadas");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
