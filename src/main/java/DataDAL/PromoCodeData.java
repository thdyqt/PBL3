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
                rs.getString("DiscountType"), rs.getInt("MinOrderValue"));
    }

    public static List<PromoCode> getAllPromoCodes(){
        List<PromoCode> list = new ArrayList<>();
        String sql = "SELECT * FROM PromoCode WHERE  status = 'Active'";

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
}
