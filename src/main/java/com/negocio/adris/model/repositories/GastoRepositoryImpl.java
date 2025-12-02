package com.negocio.adris.model.repositories;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.entities.Pago;
import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.exceptions.GastoNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GastoRepositoryImpl implements GastoRepository {
    private final Provider<Connection> connectionProvider;

    @Inject
    public GastoRepositoryImpl(Provider<Connection> connectionProvider){
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void save(Gasto g) {
        String sql = """
                INSERT INTO Gasto(
                    id_proveedor, fecha_deuda_contraida, fecha_vencimiento, monto, nota, saldado, activo
                )
                VALUES(
                    ?, ?, ?, ?, ?, ?, 1
                )
                """;

        try (Connection conn = connectionProvider.get();
             PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            preparedStatement.setLong(1, g.getProveedor().getId());
            preparedStatement.setString(2, g.getFechaDeudaContraida().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            preparedStatement.setString(3, g.getFechaVencimiento().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            preparedStatement.setBigDecimal(4, g.getMonto());
            preparedStatement.setString(5, g.getNota());
            preparedStatement.setBoolean( 6, g.isSaldado());

            preparedStatement.executeUpdate();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()){
                if (rs.next()){
                    g.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el Gasto " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Gasto g) {
        String sql = """
                    UPDATE Gasto SET
                        id_proveedor = ?,
                        fecha_deuda_contraida = ?,
                        fecha_vencimiento = ?,
                        monto = ?,
                        nota = ?,
                        saldado = ?
                    WHERE id = ?
                """;

        try (Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
            preparedStatement.setLong(1, g.getProveedor().getId());
            preparedStatement.setString(2, g.getFechaDeudaContraida().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            preparedStatement.setString(3, g.getFechaVencimiento().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            preparedStatement.setBigDecimal(4, g.getMonto());
            preparedStatement.setString(5, g.getNota());
            preparedStatement.setBoolean(6, g.isSaldado());
            preparedStatement.setLong(7, g.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el Gasto " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(long id) throws GastoNotFoundException {
        String sql = "UPDATE Gasto SET activo = 0 WHERE id = ?";

        try (Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar un Gasto por Id " + e.getMessage(), e);
        }
    }

    @Override
    public Gasto findById(long id) throws GastoNotFoundException {
        String sql = """
                    SELECT g.id, g.fecha_deuda_contraida, g.fecha_vencimiento, g.monto ,g.nota, g.saldado,
                    p.id as id_proveedor, p.nombre as proveedor_nombre, p.telefono as proveedor_telefono
                    FROM Gasto g
                    JOIN Proveedor p ON g.id_proveedor = p.id
                    WHERE g.id = ? AND g.activo = 1;
                """;

        try (Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (!rs.next()) throw new GastoNotFoundException("No se encontró un Gsto con id " + id);

            Proveedor p = new Proveedor(
                    rs.getLong("id_proveedor"),
                    rs.getString("proveedor_nombre"),
                    rs.getString("proveedor_telefono")
            );

            Gasto g = new Gasto(
                    rs.getLong("id"),
                    p,
                    LocalDateTime.parse(rs.getString("fecha_deuda_contraida")),
                    LocalDateTime.parse(rs.getString("fecha_vencimiento")),
                    rs.getBigDecimal("monto"),
                    rs.getString("nota"),
                    rs.getBoolean("saldado")
            );

            g.setPagos(getPagosByGastoId(g));

            return g;

        } catch (SQLException e) {
            throw new RuntimeException("Error al encontrar un Gasto por id" + e.getMessage(), e);
        }
    }

    @Override
    public List<Gasto> findAll() {
        String sql = """
                    SELECT g.id, g.fecha_deuda_contraida, g.fecha_vencimiento, g.monto ,g.nota, g.saldado,
                    p.id as id_proveedor, p.nombre as proveedor_nombre, p.telefono as proveedor_telefono
                    FROM Gasto g JOIN Proveedor p ON g.id_proveedor = p.id
                    WHERE g.activo = 1;
                """;

        try (Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
            ResultSet rs = preparedStatement.executeQuery();

            List<Gasto> lista = new ArrayList<>();

            while (rs.next()){
                Proveedor p = new Proveedor(
                        rs.getLong("id_proveedor"),
                        rs.getString("proveedor_nombre"),
                        rs.getString("proveedor_telefono")
                );

                Gasto g = new Gasto(
                        rs.getLong("id"),
                        p,
                        LocalDateTime.parse(rs.getString("fecha_deuda_contraida")),
                        LocalDateTime.parse(rs.getString("fecha_vencimiento")),
                        rs.getBigDecimal("monto"),
                        rs.getString("nota"),
                        rs.getBoolean("saldado")
                );

                g.setPagos(getPagosByGastoId(g));

                lista.add(g);
            }

            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error al encontrar Gastos " + e.getMessage(), e);
        }
    }

    private List<Pago> getPagosByGastoId(Gasto gasto){
        List<Pago> list = new ArrayList<>();
        String sql = """
                    SELECT *
                    FROM  Pago
                    JOIN Gasto ON Pago.id_gasto = Gasto.id
                    WHERE Gasto.id = ? AND Pago.activo = 1;
                """;

        try (Connection conn = connectionProvider.get();
            PreparedStatement statement = conn.prepareStatement(sql))
        {
            statement.setLong(1, gasto.getId());

            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                Pago p = new Pago(
                        rs.getLong("id"),
                        gasto,
                        LocalDateTime.parse((rs.getString("fecha_pago"))),
                        rs.getBigDecimal("monto_pagado")
                );

                list.add(p);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
