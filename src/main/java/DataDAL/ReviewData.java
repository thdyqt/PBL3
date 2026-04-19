package DataDAL;

import EntityDTO.ProductReview;
import Util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewData {
    public static boolean addReview(ProductReview review) {
        String sql = "INSERT INTO ProductReview (ProductID, CustomerID, Rating, Comment, ReviewDate) VALUES (?, ?, ?, ?, GETDATE())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, review.getProductID());
            stmt.setInt(2, review.getCustomerID());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi addReview: " + e.getMessage());
            return false;
        }
    }

    public static List<ProductReview> getReviewsByProduct(int productID) {
        List<ProductReview> list = new ArrayList<>();
        String sql = "SELECT r.*, c.full_name " +
                "FROM ProductReview r " +
                "JOIN Customer c ON r.CustomerID = c.id_khach_hang " +
                "WHERE r.ProductID = ? " +
                "ORDER BY r.ReviewDate DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new ProductReview(
                            rs.getInt("ReviewID"),
                            rs.getInt("ProductID"),
                            rs.getInt("CustomerID"),
                            rs.getString("CustomerName"),
                            rs.getInt("Rating"),
                            rs.getString("Comment"),
                            rs.getTimestamp("ReviewDate")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getReviewsByProduct: " + e.getMessage());
        }
        return list;
    }

    public static boolean hasPurchased(int customerID, int productID) {
        String sql = "SELECT 1 FROM Orders o " +
                "JOIN OrderDetail od ON o.id_Order = od.id_Order " +
                "WHERE o.id_Customer = ? AND od.id_Product = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerID);
            stmt.setInt(2, productID);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi hasPurchased: " + e.getMessage());
            return false;
        }
    }

    public static boolean hasReviewed(int customerID, int productID) {
        String sql = "SELECT 1 FROM ProductReview WHERE CustomerID = ? AND ProductID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerID);
            stmt.setInt(2, productID);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi hasReviewed: " + e.getMessage());
            return false;
        }
    }
}