package DataDAL;

import EntityDTO.Category;
import Util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CategoryData {
    public static Category mapResultSet(ResultSet rs) throws SQLException {
        return new Category(rs.getInt("category_id"),
                            rs.getString("category_name"), rs.getString("status"));
    }
    public static List<Category> getAll(){
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Category WHERE  status = 'Active'";

        try (Connection con = DBConnection.getConnection(); PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi getAll Category: " + e.getMessage());
        }
        return list;
    }
    // ===== GET BY ID =====
    public static int getCategoryIDByName(String categoryName) {
        List<Category> categories = CategoryData.getAll();
        for (Category c : categories) {
            if (c.getCategoryName().equals(categoryName)) {
                return c.getCategoryID();
            }
        }
        return -1; // Không tìm thấy
    }
    public static Category getByID(int categoryID) {
        String sql = "SELECT * FROM Category WHERE category_id = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, categoryID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi getByID Category: " + e.getMessage());
        }
        return null;
    }
    // ===== ADD =====
    public static boolean addCategory(Category category) {
        String sql = "INSERT INTO Category (category_name,status) VALUES (?,?)";

        try (Connection con = DBConnection.getConnection(); PreparedStatement stmt = con.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, category.getCategoryName());
            stmt.setString(2, "Active");
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) category.setCategoryID(keys.getInt(1));
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Lỗi addCategory: " + e.getMessage());
        }
        return false;
    }
    //====== UPDATE ====
    public static boolean updateCategory(Category category) {
        String sql = "UPDATE Category SET category_name = ? WHERE category_id = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, category.getCategoryName());
            stmt.setInt(2, category.getCategoryID());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi updateCategory: " + e.getMessage());
        }
        return false;
    }


    // ===== GHI LOG =====
    public static void addLog(int categoryID, String categoryName,
                              String action, String note) {
        String sql = "INSERT INTO ActivityLog (username,action,created_at) "
                + "VALUES (?, ?,?)";

        try (Connection con = DBConnection.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, "admin");
            stmt.setString(2, note);
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            stmt.setTimestamp(3, Timestamp.valueOf(now));
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Lỗi addLog Category: " + e.getMessage());
        }
    }

    // ===== KIỂM TRA TỒN TẠI =====
    public static boolean isCategoryExist(String categoryName) {
        String sql = "SELECT 1 FROM Category WHERE category_name = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, categoryName);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Lỗi isCategoryExist: " + e.getMessage());
        }
        return false;
    }
    // ===== NGỪNG KINH DOANH =====
    public static boolean stopBusiness(int categoryID) {

        String abc = "SELECT * FROM Product WHERE CategoryID = ?";
        try(Connection con = DBConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(abc)){
            pstmt.setInt(1,categoryID);
            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                   ProductData.stopBusiness(resultSet.getInt("CategoryID"));
                }
            }
        } catch (SQLException e){
            System.err.println("Lỗi khi ngừng kinh doanh Sản Phẩm thuộc Danh mục này: "+e.getMessage());
        }
        String sql = "UPDATE Category SET status = 'Inactive' WHERE category_id = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, categoryID);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi stopBusiness Category: " + e.getMessage());
        }
        return false;
    }

    // ===== MỞ LẠI KINH DOANH =====
    public static boolean restartBusiness(int categoryID) {
        String abc = "SELECT * FROM Product WHERE CategoryID = ?";
        try(Connection con = DBConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(abc)){
            pstmt.setInt(1,categoryID);
            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    ProductData.restartBusiness(resultSet.getInt("CategoryID"));
                }
            }
        } catch (SQLException e){
            System.err.println("Lỗi khi ngừng kinh doanh Sản Phẩm thuộc Danh mục này: "+e.getMessage());
        }
        String sql = "UPDATE Category SET status = 'Active' WHERE category_id = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, categoryID);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi restartBusiness Category: " + e.getMessage());
        }
        return false;
    }

    // ===== KIỂM TRA TRẠNG THÁI =====
    public static boolean isInactive(int categoryID) {
        String sql = "SELECT 1 FROM Category WHERE category_id = ? AND status = 'Inactive'";

        try (Connection con = DBConnection.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, categoryID);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Lỗi isInactive Category: " + e.getMessage());
        }
        return false;
    }
}
