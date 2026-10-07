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
                psDeal.setString(1, deal.getPaymentMethod().name()); // Asume que es un Enum
                psDeal.setFloat(2, deal.getTotalPrice());
                psDeal.setTimestamp(3, Timestamp.valueOf(deal.getDate())); // LocalDateTime a Timestamp

                ResultSet rs = psDeal.executeQuery();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    deal.setId(generatedId); // Asignamos el ID al objeto Java
                }
            }

            // 2. Guardar Items y descontar stock
            ProductDAO productDAO = new ProductDAOImpl();
            try (PreparedStatement psItem = con.prepareStatement(sqlItem)) {
                for (Item item : deal.getItems()) {
                    psItem.setInt(1, generatedId);
                    psItem.setString(2, item.getProduct().getCode());
                    psItem.setFloat(3, item.getQuantity());
                    psItem.setFloat(4, item.getProduct().getPrice()); // Precio al momento de vender
                    psItem.setFloat(5, item.getSubtotal());
                    psItem.executeUpdate();

                    // 3. Descontar el stock por cada producto vendido
                    productDAO.updateStock(item.getProduct().getCode(), item.getQuantity());
                }
            }

            con.commit();

        } catch (SQLException e) {
            try {
                // Si hay error en algún paso, revertimos todo
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