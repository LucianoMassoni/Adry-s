package com.negocio.adris.model.repositories;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.negocio.adris.model.entities.DetalleVenta;
import com.negocio.adris.model.entities.Producto;
import com.negocio.adris.model.entities.Venta;
import com.negocio.adris.model.enums.FormaDePago;
import com.negocio.adris.model.enums.TipoProducto;
import com.negocio.adris.model.enums.UnidadMedida;
import com.negocio.adris.model.exceptions.VentaNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VentaRepositoryImpl implements VentaRepository{
    private final Provider<Connection> connectionProvider;

    @Inject
    public VentaRepositoryImpl(Provider<Connection> connectionProvider){
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void save(Venta v) {
        String sqlVenta = """
        INSERT INTO Venta (forma_de_pago, fecha, total, activo)
        VALUES (?, ?, ?, 1)
    """;

        String sqlDetalle = """
        INSERT INTO DetalleVenta (
            id_venta, id_producto, cantidad, precio_unitario, descuento, subtotal, activo
        )
        VALUES (?, ?, ?, ?, ?, ?, 1)
    """;

        try (Connection conn = connectionProvider.get()) {
            conn.setAutoCommit(false);

            // 1️⃣ Insert Venta
            try (PreparedStatement psVenta =
                         conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {

                psVenta.setString(1, v.getFormaDePago().name());
                psVenta.setString(2, v.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                psVenta.setBigDecimal(3, v.getTotal());

                psVenta.executeUpdate();

                try (ResultSet rs = psVenta.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Fallo al obtener el ID generado para Venta.");
                    }
                    v.setId(rs.getLong(1));
                }
            }

            // 2️⃣ Insert DetalleVenta
            try (PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle)) {
                for (DetalleVenta dv : v.getDetalleVentas()) {
                    psDetalle.setLong(1, v.getId());
                    psDetalle.setLong(2, dv.getProducto().getId());
                    psDetalle.setBigDecimal(3, dv.getCantidad());
                    psDetalle.setBigDecimal(4, dv.getPrecioUnitario());
                    psDetalle.setBigDecimal(5, dv.getDescuento());
                    psDetalle.setBigDecimal(6, dv.getSubtotal());

                    psDetalle.executeUpdate();
                }
            }

            conn.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar venta", e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = """
                        UPDATE Venta
                        SET activo = 0
                        WHERE id = ?
                    
                        UPDATE DetalleVenta
                        SET activo = 0
                        WHERE id_venta = ?
                    """;

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
        String sql = """
                   SELECT
                       dv.id,
                       dv.cantidad,
                       dv.precio_unitario,
                       dv.descuento,
                       dv.subtotal,
                   
                       v.id as venta_id,
                       v.fecha as venta_fecha,
                       v.forma_de_pago as venta_forma_de_pago,
                       v.total as venta_total,
                   
                       p.id as producto_id,
                       p.nombre as producto_nombre,
                       p.marca as producto_marca,
                       p.tipo as producto_tipo,
                       p.peso as producto_peso,
                       p.unidad_medida as producto_unidad_medida
                   
                   FROM Venta v
                   JOIN DetalleVenta dv ON dv.id_venta = v.id
                   JOIN Producto p on dv.id_producto = p.id
                   WHERE v.id = ? AND v.activo = 1
                   """;

        try (Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<DetalleVenta> detalleVentas = new ArrayList<>();
            while (resultSet.next()){
                String unidadMedidaStr = resultSet.getString("producto_unidad_medida");
                UnidadMedida unidadMedida = (unidadMedidaStr == null) ? null : UnidadMedida.valueOf(unidadMedidaStr);
                String tipoStr = resultSet.getString("producto_tipo");
                TipoProducto tipo = (tipoStr == null) ? null : TipoProducto.valueOf(tipoStr);

                Producto p = new Producto(
                        resultSet.getLong("producto_id"),
                        resultSet.getString("producto_nombre"),
                        resultSet.getString("producto_marca"),
                        tipo,
                        resultSet.getDouble("producto_peso"),
                        unidadMedida
                );


                detalleVentas.add(new DetalleVenta(
                        resultSet.getLong("id"),
                        p,
                        resultSet.getBigDecimal("cantidad"),
                        resultSet.getBigDecimal("precio_unitario"),
                        resultSet.getBigDecimal("descuento"),
                        resultSet.getBigDecimal("subtotal")
                ));
            }

            return new Venta(
                    resultSet.getLong("venta_id"),
                    FormaDePago.valueOf(resultSet.getString("venta_forma_de_pago")),
                    LocalDateTime.parse(resultSet.getString("venta_fecha"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    resultSet.getBigDecimal("venta_total"),
                    detalleVentas
            );

        } catch (SQLException e) {
            throw new RuntimeException("error al encontrar una venta por id" + e.getMessage(), e);
        }
    }

    @Override
    public List<Venta> findAll() {
        String sql = """
            SELECT
                v.id AS venta_id,
                v.fecha AS venta_fecha,
                v.forma_de_pago AS venta_forma_de_pago,
                v.total AS venta_total,
        
                dv.id AS detalle_id,
                dv.cantidad,
                dv.precio_unitario,
                dv.descuento,
                dv.subtotal,
        
                p.id AS producto_id,
                p.nombre AS producto_nombre,
                p.marca AS producto_marca,
                p.tipo AS producto_tipo,
                p.peso AS producto_peso,
                p.unidad_medida AS producto_unidad_medida
        
            FROM Venta v
            JOIN DetalleVenta dv ON dv.id_venta = v.id
            JOIN Producto p ON dv.id_producto = p.id
            WHERE v.activo = 1
            ORDER BY v.fecha DESC, v.id
        """;

        Map<Long, Venta> ventasMap = new LinkedHashMap<>();

        try (Connection conn = connectionProvider.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long ventaId = rs.getLong("venta_id");

                Venta venta = ventasMap.get(ventaId);
                if (venta == null) {
                    venta = new Venta(
                            ventaId,
                            FormaDePago.valueOf(rs.getString("venta_forma_de_pago")),
                            LocalDateTime.parse(
                                    rs.getString("venta_fecha"),
                                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
                            ),
                            rs.getBigDecimal("venta_total"),
                            new ArrayList<>()
                    );
                    ventasMap.put(ventaId, venta);
                }

                String unidadMedidaStr = rs.getString("producto_unidad_medida");
                UnidadMedida unidadMedida = (unidadMedidaStr == null) ? null : UnidadMedida.valueOf(unidadMedidaStr);
                String tipoStr = rs.getString("producto_tipo");
                TipoProducto tipo = (tipoStr == null) ? null : TipoProducto.valueOf(tipoStr);

                Producto producto = new Producto(
                        rs.getLong("producto_id"),
                        rs.getString("producto_nombre"),
                        rs.getString("producto_marca"),
                        tipo,
                        rs.getDouble("producto_peso"),
                        unidadMedida
                );

                DetalleVenta detalle = new DetalleVenta(
                        rs.getLong("detalle_id"),
                        producto,
                        rs.getBigDecimal("cantidad"),
                        rs.getBigDecimal("precio_unitario"),
                        rs.getBigDecimal("descuento"),
                        rs.getBigDecimal("subtotal")
                );

                venta.getDetalleVentas().add(detalle);
            }

            return new ArrayList<>(ventasMap.values());

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener ventas", e);
        }
    }

    @Override
    public List<Venta> getAllVentasByFecha(String fecha) {
        String sql = String.format("""
            SELECT
                v.id AS venta_id,
                v.fecha AS venta_fecha,
                v.forma_de_pago AS venta_forma_de_pago,
                v.total AS venta_total,
        
                dv.id AS detalle_id,
                dv.cantidad,
                dv.precio_unitario,
                dv.descuento,
                dv.subtotal,
        
                p.id AS producto_id,
                p.nombre AS producto_nombre,
                p.marca AS producto_marca,
                p.tipo AS producto_tipo,
                p.peso AS producto_peso,
                p.unidad_medida AS producto_unidad_medida
        
            FROM Venta v
            JOIN DetalleVenta dv ON dv.id_venta = v.id
            JOIN Producto p ON dv.id_producto = p.id
            WHERE instr(fecha, '%s') > 0 AND v.activo = 1
            ORDER BY v.fecha DESC, v.id
        """, fecha);

        Map<Long, Venta> ventasMap = new LinkedHashMap<>();

        try (Connection conn = connectionProvider.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long ventaId = rs.getLong("venta_id");

                Venta venta = ventasMap.get(ventaId);
                if (venta == null) {
                    venta = new Venta(
                            ventaId,
                            FormaDePago.valueOf(rs.getString("venta_forma_de_pago")),
                            LocalDateTime.parse(
                                    rs.getString("venta_fecha"),
                                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
                            ),
                            rs.getBigDecimal("venta_total"),
                            new ArrayList<>()
                    );
                    ventasMap.put(ventaId, venta);
                }

                String unidadMedidaStr = rs.getString("producto_unidad_medida");
                UnidadMedida unidadMedida = (unidadMedidaStr == null) ? null : UnidadMedida.valueOf(unidadMedidaStr);
                String tipoStr = rs.getString("producto_tipo");
                TipoProducto tipo = (tipoStr == null) ? null : TipoProducto.valueOf(tipoStr);

                Producto producto = new Producto(
                        rs.getLong("producto_id"),
                        rs.getString("producto_nombre"),
                        rs.getString("producto_marca"),
                        tipo,
                        rs.getDouble("producto_peso"),
                        unidadMedida
                );

                DetalleVenta detalle = new DetalleVenta(
                        rs.getLong("detalle_id"),
                        producto,
                        rs.getBigDecimal("cantidad"),
                        rs.getBigDecimal("precio_unitario"),
                        rs.getBigDecimal("descuento"),
                        rs.getBigDecimal("subtotal")
                );

                venta.getDetalleVentas().add(detalle);
            }

            return new ArrayList<>(ventasMap.values());

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener ventas", e);
        }
    }
}
