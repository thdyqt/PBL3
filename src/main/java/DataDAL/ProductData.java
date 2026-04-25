package DataDAL;

import EntityDTO.OrderDetail;
import EntityDTO.Product;
import Util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductData {
    private static Product mapResultSet(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("ProductID"),
                rs.getString("ProductName"),
                rs.getInt("CategoryID"),
                rs.getInt("ProductPrice"),
                rs.getInt("quantity"),
                rs.getString("status"),
                rs.getString("description"),
                rs.getString("ingredients"),
                rs.getDouble("rating"),
                rs.getString("image")
        );
    }

    //====Lấy tất cả====
    public static List<Product> getAllProduct(){
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE status = 'Active'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi getAllProduct: " + e.getMessage());
        }
        return list;
    }

    //====Lấy Top sản phẩm bán chạy====
    public static List<Product> getTopBestSellers(int quantity){
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.* FROM Product p JOIN OrderDetail od ON p.ProductID = od.id_Product WHERE p.status = 'Active' GROUP BY p.ProductID ORDER BY SUM(od.quantity) DESC LIMIT ?;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi getTopBestSellers: " + e.getMessage());
        }
        return list;
    }

    //====Lấy Top sản phẩm bán chạy====
    public static List<Product> getNewestProducts(int quantity){
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.* FROM Product p WHERE p.status = 'Active' ORDER BY ProductID DESC LIMIT ?;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi getNewestProducts: " + e.getMessage());
        }
        return list;
    }

    //====THÊM SẢN PHẨM===
    public static boolean addProduct(Product product) {
        String sql = "INSERT INTO Product (ProductName, CategoryID, ProductPrice, quantity, status, description, ingredients, rating, image) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.getProductName());
            stmt.setInt(2, product.getCategoryID());
            stmt.setInt(3, product.getProductPrice());
            stmt.setInt(4, product.getQuantity());

            stmt.setString(5, product.getStatus() != null ? product.getStatus() : "Active");
            stmt.setString(6, product.getDescription());
            stmt.setString(7, product.getIngredients());
            stmt.setDouble(8, product.getRating() > 0 ? product.getRating() : 0.0);
            stmt.setString(9, product.getImage());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) product.setProductID(keys.getInt(1));
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi addProduct: " + e.getMessage());
        }
        return false;
    }

    // ===== UPDATE =====
    public static boolean updateProduct(Product product) {
        String sql = "UPDATE Product SET ProductName = ?, CategoryID = ?, "
                + "ProductPrice = ?, quantity = ?, status = ?, description = ?, ingredients = ?, rating = ?, image = ? "
                + "WHERE ProductID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getProductName());
            stmt.setInt(2, product.getCategoryID());
            stmt.setInt(3, product.getProductPrice());
            stmt.setInt(4, product.getQuantity());
            stmt.setString(5, product.getStatus());
            stmt.setString(6, product.getDescription());
            stmt.setString(7, product.getIngredients());
            stmt.setDouble(8, product.getRating());
            stmt.setString(9, product.getImage());
            stmt.setInt(10, product.getProductID());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi updateProduct: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateProductAverageRating(int productId) {
        String sql = "UPDATE Product " +
                "SET rating = (SELECT IFNULL(AVG(RatingValue), 0) FROM ProductReview WHERE ProductID = ?) " +
                "WHERE ProductID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            stmt.setInt(2, productId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật rating trung bình: " + e.getMessage());
            return false;
        }
    }

    // ===== NGỪNG KINH DOANH =====
    public static boolean stopBusiness(int productID) {
        String sql = "UPDATE Product SET status = 'Inactive' WHERE ProductID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productID);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi Ngừng kinh doanh Sản Phẩm: " + e.getMessage());
        }
        return false;
    }

    // ===== MỞ LẠI KINH DOANH =====
    public static boolean restartBusiness(int productID) {
        String sql = "UPDATE Product SET status = 'Active' WHERE ProductID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productID);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi restartBusiness Product: " + e.getMessage());
        }
        return false;
    }

    public static void reduceStockBatch(Connection conn, List<OrderDetail> details) throws SQLException {
        String sql = "UPDATE Product SET quantity = quantity - ? WHERE ProductID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (OrderDetail item : details) {
                stmt.setInt(1, item.getQuantity());
                stmt.setInt(2, item.getProduct().getProductID());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public static List<Product> getByCategory(int categoryID) {

        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE CategoryID = ? AND status = 'Active'";
        System.out.println("=== DEBUG getByCategory ===");
        System.out.println("→ CategoryID đang tìm: " + categoryID);
        System.out.println("→ SQL: " + sql);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi getByCategory Product: " + e.getMessage());
        }
        return list;
    }

    // ===== LẤY SẢN PHẨM ĐÃ NGỪNG KINH DOANH =====
    public static List<Product> getInactiveProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE status = 'Inactive'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);

             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) list.add(mapResultSet(rs));

        } catch (SQLException e) {
            System.err.println("Lỗi getInactiveProducts: " + e.getMessage());
        }
        return list;
    }
    public static Product getByID(int productID) {
        String sql = "SELECT * FROM Product WHERE ProductID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi getByID Product: " + e.getMessage());
        }
        return null;
    }
    public static String getImage(int productID){
        String sql = "SELECT * FROM Product WHERE ProductID = ?";
        try(Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,productID);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()) return mapResultSet(rs).getImage();
            }
        } catch (SQLException e){
            System.err.println("Lỗi lấy hình ảnh: "+e.getMessage());
        }
        return null;
}
    public static boolean isProductExist(String productName) {
        String sql = "SELECT 1 FROM Product WHERE ProductName = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productName);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Lỗi isProductExist: " + e.getMessage());
        }
        return false;
    }

    public static boolean isInactive(int productID) {
        String sql = "SELECT 1 FROM Product WHERE ProductID = ? AND status = 'Inactive'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productID);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Lỗi isInactive Product: " + e.getMessage());
        }
        return false;
    }
}