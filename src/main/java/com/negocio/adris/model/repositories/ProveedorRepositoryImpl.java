package com.negocio.adris.model.repositories;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.negocio.adris.model.entities.Proveedor;
import com.negocio.adris.model.exceptions.ProveedorNotFoundException;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorRepositoryImpl implements ProveedorRepository {
    private final Provider<Connection> connectionProvider;

    @Inject
    public ProveedorRepositoryImpl(Provider<Connection> connectionProvider){
        this.connectionProvider = connectionProvider;
    }
    @Override
    public void save(Proveedor p) {
        String sql = """
                    INSERT INTO Proveedor(
                        nombre, telefono, activo
                    )
                    VALUES(
                        ?, ?,1
                    )
                """;

        try (Connection conn = connectionProvider.get();
             PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            preparedStatement.setString(1, p.getNombre());
            preparedStatement.setString(2, p.getTelefono());

            preparedStatement.executeUpdate();

            try(ResultSet rs = preparedStatement.getGeneratedKeys()){
                if (rs.next()){
                    p.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el Proveedor. " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Proveedor p) {
        String sql = """
                    UPDATE Proveedor SET
                        nombre = ?,
                        telefono = ?
                    WHERE id = ?
                """;

        try (Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
            preparedStatement.setString(1, p.getNombre());
            preparedStatement.setString(2, p.getTelefono());
            preparedStatement.setLong(3, p.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar Proveedor" + e.getMessage(), e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "UPDATE Proveedor Set activo = 0 WHERE id = ?";

        try (Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar un Proveedor por Id " + e.getMessage(), e);
        }
    }

    @Override
    public Proveedor findById(long id) throws ProveedorNotFoundException {
        String sql = "SELECT * FROM Proveedor WHERE id = ?";

        try (Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()){
                throw  new ProveedorNotFoundException("Proveedor con ID " + id + " no econtrado");
            }

            return new Proveedor(
                    resultSet.getLong("id"),
                    resultSet.getString("nombre"),
                    resultSet.getString("telefono")
            );

        } catch (SQLException e) {
            throw new RuntimeException("Error al encontrar un producto por Id\n" + e.getMessage(), e);
        }
    }

    @Override
    public List<Proveedor> findAll() {
        String sql = "SELECT * FROM Proveedor";

        try (Connection conn = connectionProvider.get();
            PreparedStatement preparedStatement = conn.prepareStatement(sql))
        {
            ResultSet rs = preparedStatement.executeQuery();

            List<Proveedor> lista = new ArrayList<>();
            while (rs.next()){
                int activo = rs.getInt("activo");
                if (activo != 0)
                    continue;

                Proveedor p = new Proveedor(
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getString("telefono")
                );
                lista.add(p);
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todos los Proveedores " + e.getMessage(), e);
        }
    }
}
