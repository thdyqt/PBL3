package DataDAL;

import EntityDTO.PromoCode;
import Util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PromoCodeData {
    public static PromoCode mapResultSet(ResultSet rs) throws SQLException {
        return new PromoCode(rs.getString("Code"),
                rs.getString("Description"), rs.getInt("DiscountValue"),
                rs.getString("DiscountType"), rs.getInt("MinOrderValue"),
                rs.getDate("ValidFrom"), rs.getDate("ValidTo"),
                rs.getString("status"));
    }

    public static List<PromoCode> getAllPromoCodes(){
        List<PromoCode> list = new ArrayList<>();
        String sql = "SELECT * FROM PromoCode";

        try (Connection con = DBConnection.getConnection(); PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi getAllPromoCodes: " + e.getMessage());
        }
        return list;
    }

    public static boolean isPromoCodeExist(String code){
        String sql = "SELECT Code FROM PromoCode WHERE Code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean addPromoCode(PromoCode p) {
        String sql = "INSERT INTO PromoCode (Code, Description, DiscountValue, DiscountType, MinOrderValue, ValidFrom, ValidTo, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getCode());
            stmt.setString(2, p.getDescription());
            stmt.setInt(3, p.getDiscountValue());
            stmt.setString(4, p.getDiscountType());
            stmt.setInt(5, p.getMinOrderValue());
            stmt.setDate(6, p.getValidFrom());
            stmt.setDate(7, p.getValidTo());
            stmt.setString(8, p.getStatus());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updatePromoCode(PromoCode p) {
        String sql = "UPDATE PromoCode SET Description = ?, DiscountValue = ?, DiscountType = ?, MinOrderValue = ?, ValidFrom = ?, ValidTo = ?, status = ? WHERE Code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getDescription());
            stmt.setInt(2, p.getDiscountValue());
            stmt.setString(3, p.getDiscountType());
            stmt.setInt(4, p.getMinOrderValue());
            stmt.setDate(5, p.getValidFrom());
            stmt.setDate(6, p.getValidTo());
            stmt.setString(7, p.getStatus());
            stmt.setString(8, p.getCode());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updatePromoStatus(String code, String status) {
        String sql = "UPDATE PromoCode SET status = ? WHERE Code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setString(2, code);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void refreshAllPromoStatuses() {
        String sqlActive = "UPDATE PromoCode SET status = 'Active' WHERE ValidFrom <= CURRENT_TIMESTAMP AND (ValidTo IS NULL OR ValidTo >= CURRENT_TIMESTAMP) AND status = 'Upcoming'";

        String sqlExpired = "UPDATE PromoCode SET status = 'Expired' " +
                "WHERE ValidTo < CURRENT_TIMESTAMP " +
                "AND status IN ('Active', 'Paused', 'Upcoming')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt1 = conn.prepareStatement(sqlActive);
             PreparedStatement stmt2 = conn.prepareStatement(sqlExpired)) {

            stmt1.executeUpdate();
            stmt2.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Lỗi tự động cập nhật trạng thái PromoCode: " + e.getMessage());
        }
    }
}
