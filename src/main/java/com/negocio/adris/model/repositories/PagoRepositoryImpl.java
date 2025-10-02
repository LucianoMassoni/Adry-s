package com.negocio.adris.model.repositories;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.negocio.adris.model.entities.Gasto;
import com.negocio.adris.model.entities.Pago;
import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.exceptions.PagoNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PagoRepositoryImpl implements PagoRepository {
    private final Provider<Connection> connectionProvider;

    @Inject
    public PagoRepositoryImpl(Provider<Connection> connectionProvider){
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void save(Pago p) {
        String sql = """
                    INSERT INTO Pago(
                    id_gasto, fecha_pago, monto_pagado, activo
                )
                VALUES(
                    ?, ?, ?, 1
                )
                """;

        try (Connection conn = connectionProvider.get();
             PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            preparedStatement.setLong(1, p.getGasto().getId());
            preparedStatement.setString(2, p.getFechaPago().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            preparedStatement.setBigDecimal(3, p.getMontoPagado());

            preparedStatement.executeUpdate();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()){
                if (rs.next()){
                    p.setId(rs.getLong(1)); //le setea el Id generado
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el Pago " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Pago p) {
        String sql = """
                    UPDATE Pago SET
                        id_gasto = ?,
                        fecha_pago = ?,
                        monto_pagado = ?
                    WHERE id = ?
                """;

        try (Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
            preparedStatement.setLong(1, p.getGasto().getId());
            preparedStatement.setString(2, p.getFechaPago().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            preparedStatement.setBigDecimal(3, p.getMontoPagado());
            preparedStatement.setLong(4, p.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el Pago " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(long id) throws PagoNotFoundException {
        String sql = "UPDATE Pago SET activo = 0 WHERE id = ?";

        try (Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al borrar el Pago " + e.getMessage(), e);
        }
    }

    @Override
    public Pago findById(long id) throws PagoNotFoundException {
        String sql = """
                    SELECT prov.id AS proveedor_id, prov.nombre AS proveedor_nombre, prov.telefono as proveedor_telefono,
                           g.id AS gasto_id, g.fecha_deuda_contraida AS gasto_fecha_cont, g.fecha_vencimiento AS gasto_fecha_ven, g.monto AS gasto_monto, g.nota AS gasto_nota, g.saldado AS gasto_saldado,
                           pago.id, pago.fecha_pago, pago.monto_pagado
                    FROM Pago pago
                    JOIN Gasto g ON pago.id_gasto = g.id
                    JOIN Proveedor prov ON g.id_proveedor = prov.id
                    WHERE pago.id = ? AND pago.activo = 1;
                """;

        try (Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) throw new PagoNotFoundException("No hay ningùn pago con id:" + id);
            Proveedor proveedor = new Proveedor(
                    resultSet.getLong("proveedor_id"),
                    resultSet.getString("proveedor_nombre"),
                    resultSet.getString("proveedor_telefono")
            );

            Gasto gasto = new Gasto(
                    resultSet.getLong("gasto_id"),
                    proveedor,
                    LocalDateTime.parse(resultSet.getString("gasto_fecha_cont")),
                    LocalDateTime.parse(resultSet.getString("gasto_fecha_ven")),
                    resultSet.getBigDecimal("gasto_monto"),
                    resultSet.getString("gasto_nota"),
                    resultSet.getBoolean("gasto_saldado")
            );
            
            return new Pago(
                    resultSet.getLong("id"),
                    gasto,
                    LocalDateTime.parse(resultSet.getString("fecha_pago")),
                    resultSet.getBigDecimal("monto_pagado")
            );

        } catch (SQLException e) {
            throw new RuntimeException("Error al encontrar el Pago" + e.getMessage() ,e);
        }
    }

    @Override
    public List<Pago> findAll() {
        String sql = """
                    SELECT prov.id AS proveedor_id, prov.nombre AS proveedor_nombre, prov.telefono as proveedor_telefono,
                           g.id AS gasto_id, g.fecha_deuda_contraida AS gasto_fecha_cont, g.fecha_vencimiento AS gasto_fecha_ven, g.monto AS gasto_monto, g.nota AS gasto_nota, g.saldado AS gasto_saldado,
                           pago.id, pago.fecha_pago, pago.monto_pagado
                    FROM Pago pago
                    JOIN Gasto g ON pago.id_gasto = g.id
                    JOIN Proveedor prov ON g.id_proveedor = prov.id
                    WHERE pago.activo = 1;
                """;

        try (Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Pago> lista = new ArrayList<>();

            while (resultSet.next()){
                Proveedor proveedor = new Proveedor(
                        resultSet.getLong("proveedor_id"),
                        resultSet.getString("proveedor_nombre"),
                        resultSet.getString("proveedor_telefono")
                );

                Gasto gasto = new Gasto(
                        resultSet.getLong("gasto_id"),
                        proveedor,
                        LocalDateTime.parse(resultSet.getString("gasto_fecha_cont")),
                        LocalDateTime.parse(resultSet.getString("gasto_fecha_ven")),
                        resultSet.getBigDecimal("gasto_monto"),
                        resultSet.getString("gasto_nota"),
                        resultSet.getBoolean("gasto_saldado")
                );

                Pago p = new Pago(
                        resultSet.getLong("id"),
                        gasto,
                        LocalDateTime.parse(resultSet.getString("fecha_pago")),
                        resultSet.getBigDecimal("monto_pagado")
                );

                lista.add(p);
            }
            return lista;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
