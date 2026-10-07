package org.example.controller.dao;

import org.example.controller.Database;
import org.example.models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {

    @Override
    public void create(Product p) {
        String sql = "INSERT INTO producto (codigo_barras, nombre, descripcion, precio_base, stock_actual, tipo_venta) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = Database.getInstance().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getCode());
            ps.setString(2, p.getName());
            ps.setString(3, p.getDescription());
            ps.setFloat(4, p.getPrice());
            ps.setFloat(5, p.getStockActual());
            ps.setString(6, p.getTipoVenta());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
        }
    }

    @Override
    public void update(Product p) {
        String sql = "UPDATE producto SET nombre=?, descripcion=?, precio_base=?, stock_actual=?, tipo_venta=? WHERE codigo_barras=?";

        try (Connection con = Database.getInstance().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setFloat(3, p.getPrice());
            ps.setFloat(4, p.getStockActual());
            ps.setString(5, p.getTipoVenta());
            ps.setString(6, p.getCode());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
        }
    }

    @Override
    public void delete(String code) {
        String sql = "DELETE FROM producto WHERE codigo_barras=?";
        try (Connection con = Database.getInstance().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
        }
    }

    @Override
    public Product getByCode(String code) {
        String sql = "SELECT * FROM producto WHERE codigo_barras=?";
        try (Connection con = Database.getInstance().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getFloat("precio_base"),
                            rs.getFloat("stock_actual"),
                            rs.getString("tipo_venta"),
                            rs.getString("codigo_barras")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar producto: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM producto";

        // ResultSet, Statement y Connection adentro del try
        try (Connection con = Database.getInstance().getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Product(
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getFloat("precio_base"),
                        rs.getFloat("stock_actual"),
                        rs.getString("tipo_venta"),
                        rs.getString("codigo_barras")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void updateStock(String code, float quantitySold) {
        String sql = "UPDATE producto SET stock_actual = stock_actual - ? WHERE codigo_barras = ?";
        try (Connection con = Database.getInstance().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setFloat(1, quantitySold);
            ps.setString(2, code);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
        }
    }
}