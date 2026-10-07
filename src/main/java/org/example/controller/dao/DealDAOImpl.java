package org.example.controller.dao;
import org.example.controller.Database;
import org.example.models.Deal;
import org.example.models.Item;
import java.sql.*;

public class DealDAOImpl implements DealDAO {

    @Override
    public int registerDeal(Deal deal) {
        String sqlDeal = "INSERT INTO venta (metodo_pago, total, fecha_hora) VALUES (?, ?, ?) RETURNING id_venta";
        String sqlItem = "INSERT INTO detalle_venta (id_venta, codigo_barras, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        int generatedId = -1;

        Connection con = Database.getInstance().getConexion();
        try {
            con.setAutoCommit(false);

            try (PreparedStatement psDeal = con.prepareStatement(sqlDeal)) {
                psDeal.setString(1, deal.getPaymentMethod().name());
                psDeal.setFloat(2, deal.getTotalPrice());
                psDeal.setTimestamp(3, Timestamp.valueOf(deal.getDate()));

                ResultSet rs = psDeal.executeQuery();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    deal.setId(generatedId);
                }
            }

            ProductDAO productDAO = new ProductDAOImpl();
            try (PreparedStatement psItem = con.prepareStatement(sqlItem)) {
                for (Item item : deal.getItems()) {
                    psItem.setInt(1, generatedId);
                    psItem.setString(2, item.getProduct().getCode());
                    psItem.setFloat(3, item.getQuantity());
                    psItem.setFloat(4, item.getProduct().getPrice());
                    psItem.setFloat(5, item.getSubtotal());
                    psItem.executeUpdate();
                    productDAO.updateStock(item.getProduct().getCode(), item.getQuantity());
                }
            }

            con.commit();

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.err.println("Error crítico en Rollback: " + ex.getMessage());
            }
            System.err.println("Error al registrar el Deal: " + e.getMessage());
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return generatedId;
    }
}