package DataDAL;

import EntityDTO.OrderDetail;
import EntityDTO.Product;
import Util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartData {
    public static void saveCartItem(int customerID, int productID, int quantity) {
        String sql = "INSERT INTO Cart (id_Customer, id_Product, quantity) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE quantity = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerID);
            stmt.setInt(2, productID);
            stmt.setInt(3, quantity);
            stmt.setInt(4, quantity);

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi lưu giỏ hàng: " + e.getMessage());
        }
    }

    public static void removeCartItem(int customerID, int productID) {
        String sql = "DELETE FROM Cart WHERE id_Customer = ? AND id_Product = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerID);
            stmt.setInt(2, productID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearCart(int customerID) {
        String sql = "DELETE FROM Cart WHERE id_Customer = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<OrderDetail> loadCustomerCart(int customerID) {
        List<OrderDetail> cartList = new ArrayList<>();
        String sql = "SELECT id_Product, quantity FROM Cart WHERE id_Customer = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product p = ProductData.getByID(rs.getInt("id_Product"));
                    if (p != null) {
                        int qty = rs.getInt("quantity");

                        OrderDetail detail = new OrderDetail();
                        detail.setProduct(p);
                        detail.setQuantity(qty);
                        detail.setPrice(p.getProductPrice());
                        detail.setTotalPrice(p.getProductPrice() * qty);

                        cartList.add(detail);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartList;
    }
}