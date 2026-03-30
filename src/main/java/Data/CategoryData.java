package Data;

import Entity.Category;
import Util.DBConnection;
import Util.UserSession;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CategoryData {
    private static Connection connection = DBConnection.getConnection();
    public static Category mapResultSet(ResultSet rs) throws SQLException {
        return new Category(rs.getInt("CategoryID"),
                            rs.getString("CategoryName"));
    }
    public static List<Category> getALL(){
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Category";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi getAll Category: " + e.getMessage());
        }
        return list;
    }
    // ===== ADD =====
    public static boolean addCategory(Category category) {
        String sql = "INSERT INTO Category (category_name) VALUES (?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, category.getCategoryName());

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
    public static boolean updateCategory(Category category) {
        String sql = "UPDATE Category SET category_name = ? WHERE category_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, category.getCategoryName());
            stmt.setInt(2, category.getCategoryID());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi updateCategory: " + e.getMessage());
        }
        return false;
    }

    // ===== DELETE =====
    public static boolean deleteCategory(int categoryID) {
        String sql = "DELETE FROM Category WHERE category_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, categoryID);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi deleteCategory: " + e.getMessage());
        }
        return false;
    }
}
