package DataDAL;

import EntityDTO.PromoCode;
import Util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PromoCodeData {
    public static PromoCode mapResultSet(ResultSet rs) throws SQLException {
        String discountType = rs.getString("DiscountType");
        String type = rs.getString("Type");
        String status = rs.getString("status");

        return new PromoCode(
                rs.getString("Code"),
                rs.getString("Description"),
                rs.getInt("DiscountValue"),
                PromoCode.CodeType.valueOf(discountType),
                PromoCode.Type.valueOf(type),
                rs.getInt("MinOrderValue"),
                rs.getTimestamp("ValidFrom").toLocalDateTime(),
                rs.getTimestamp("ValidTo").toLocalDateTime(),
                PromoCode.CodeStatus.valueOf(status)
        );
    }

    public static PromoCode getPromoCode(String code){
        String sql = "SELECT * FROM PromoCode WHERE BINARY Code = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, code);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getPromoCode: " + e.getMessage());
        }
        return null;
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
        String sql = "INSERT INTO PromoCode (Code, Description, DiscountValue, DiscountType, Type, MinOrderValue, ValidFrom, ValidTo, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getCode());
            stmt.setString(2, p.getDescription());
            stmt.setInt(3, p.getDiscountValue());
            stmt.setString(4, p.getDiscountType().name());
            stmt.setString(5, p.getType().name());
            stmt.setInt(6, p.getMinOrderValue());
            stmt.setTimestamp(7, java.sql.Timestamp.valueOf(p.getValidFrom()));
            stmt.setTimestamp(8, java.sql.Timestamp.valueOf(p.getValidTo()));;
            stmt.setString(9, p.getStatus().name());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updatePromoCode(PromoCode p) {
        String sql = "UPDATE PromoCode SET Description = ?, DiscountValue = ?, DiscountType = ?, Type = ?, MinOrderValue = ?, ValidFrom = ?, ValidTo = ?, status = ? WHERE Code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getDescription());
            stmt.setInt(2, p.getDiscountValue());
            stmt.setString(3, p.getDiscountType().name());
            stmt.setString(4, p.getType().name());
            stmt.setInt(5, p.getMinOrderValue());
            stmt.setTimestamp(6, Timestamp.valueOf(p.getValidFrom()));
            stmt.setTimestamp(7, Timestamp.valueOf(p.getValidTo()));
            stmt.setString(8, p.getStatus().name());
            stmt.setString(9, p.getCode());

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

    public static boolean updatePromoStatusAndStartDate(String code, String status, LocalDateTime newValidFrom) {
        String sql = "UPDATE PromoCode SET status = ?, ValidFrom = ? WHERE Code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setTimestamp(2, Timestamp.valueOf(newValidFrom));
            stmt.setString(3, code);

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
