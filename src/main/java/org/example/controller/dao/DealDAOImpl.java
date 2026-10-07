package org.example.controller.dao;

import org.example.controller.Database;
import org.example.models.Deal;
import org.example.models.Item;
import org.example.models.PaymentMethod;
import org.example.models.Product;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DealDAOImpl implements DealDAO {

    @Override
    public int registerDeal(Deal deal) {
        String sqlDeal = "INSERT INTO venta (metodo_pago, total, fecha_hora) VALUES (?, ?, ?) RETURNING id_venta";
        String sqlItem = "INSERT INTO detalle_venta (id_venta, codigo_barras, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        String sqlStock = "UPDATE producto SET stock_actual = stock_actual - ? WHERE codigo_barras = ?";

        int generatedId = -1;

        try (Connection con = Database.getInstance().getConexion()) {
            con.setAutoCommit(false);

            try {
                try (PreparedStatement psDeal = con.prepareStatement(sqlDeal)) {
                    psDeal.setString(1, deal.getPaymentMethod().name());
                    psDeal.setFloat(2, deal.getTotalPrice());
                    psDeal.setTimestamp(3, Timestamp.valueOf(deal.getDate()));
                    try (ResultSet rs = psDeal.executeQuery()) {
                        if (rs.next()) {
                            generatedId = rs.getInt(1);
                        }
                    }
                }

                try (PreparedStatement psItem = con.prepareStatement(sqlItem);
                     PreparedStatement psStock = con.prepareStatement(sqlStock)) {

                    for (Item item : deal.getItems()) {
                        // Insertar detalle
                        psItem.setInt(1, generatedId);
                        psItem.setString(2, item.getProduct().getCode());
                        psItem.setFloat(3, item.getQuantity());
                        psItem.setFloat(4, item.getProduct().getPrice());
                        psItem.setFloat(5, item.getSubtotal());
                        psItem.executeUpdate();

                        // Actualizar stock
                        psStock.setFloat(1, item.getQuantity());
                        psStock.setString(2, item.getProduct().getCode());
                        psStock.executeUpdate();
                    }
                }

                con.commit();

            } catch (SQLException e) {
                con.rollback();
                System.err.println("Error en la transacción, se hizo rollback: " + e.getMessage());
                e.printStackTrace();
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener la conexión para la venta: " + e.getMessage());
        }

        return generatedId;
    }
    @Override
    public List<Deal> getAllDeals() {
        List<Deal> list = new ArrayList<>();
        String sql = "SELECT id_venta, fecha_hora, total, metodo_pago FROM venta ORDER BY fecha_hora DESC";

        try (Connection con = Database.getInstance().getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id_venta");
                LocalDateTime date = rs.getTimestamp("fecha_hora").toLocalDateTime();
                float total = rs.getFloat("total");
                PaymentMethod paymentMethod = PaymentMethod.valueOf(rs.getString("metodo_pago"));
                list.add(new Deal(id, date, total, paymentMethod));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar las ventas: " + e.getMessage());
        }

        return list;
    }
}