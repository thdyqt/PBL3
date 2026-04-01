package Data;

import Entity.OrderDetail;
import Entity.Product;
import Util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// CRUD operations on the Product into Order
// basically adding/reading/updating/deleting items from a grocery bag
public class OrderDetailData {

    // 1. CREATE: Add an item to the order (The Add-to-Cart)
    public static boolean addProduct_OrderDetail(int id_Order, OrderDetail orderDetail) {
        String sql = "INSERT INTO `OrderDetail` (id_Order, id_Product, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id_Order);
            stmt.setInt(2, orderDetail.getProduct().getProductID());

            // Fixed: Grab the quantity directly from the OrderDetail item!
            stmt.setInt(3, orderDetail.getQuantity());

            // Fixed: Save the historical unit price, not the total price!
            stmt.setInt(4, orderDetail.getPrice());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. READ: Load all items for a specific order (The Receipt Loader)
    public static List<OrderDetail> searchOrderDetail(int id_Order) {
        List<OrderDetail> found = new ArrayList<>();
        String sql = "SELECT * FROM `OrderDetail` WHERE id_Order = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id_Order);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int productId = rs.getInt("id_Product");
                    int quantity = rs.getInt("quantity");
                    int price = rs.getInt("price");

                    // Build the Product stub
                    Product product = new Product();
                    product.setProductID(productId);

                    // Build the OrderDetail object and toss it in the bucket
                    OrderDetail item = new OrderDetail(id_Order, product, quantity, price);
                    found.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return found;
    }

    // 3. UPDATE: Change the quantity of an existing item (The Quantity Tweaker)
    public static boolean updateOrderDetail(int id_Order, int id_Product, int newQuantity) {
        String sql = "UPDATE `OrderDetail` SET quantity = ? WHERE id_Order = ? AND id_Product = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Read the SQL string left-to-right to place the variables!
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, id_Order);
            stmt.setInt(3, id_Product);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. DELETE: Completely remove an item from the cart (The Trash Can)
    public static boolean removeOrderDetail(int id_Order, int id_Product) {
        String sql = "DELETE FROM `OrderDetail` WHERE id_Order = ? AND id_Product = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id_Order);
            stmt.setInt(2, id_Product);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}