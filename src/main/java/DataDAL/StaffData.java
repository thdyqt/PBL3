package DataDAL;

import EntityDTO.Staff;
import Util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffData {
    public static List<Staff> getAllStaff() {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM Staff";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Staff(
                        rs.getInt("id_nhan_vien"),
                        rs.getString("phone"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("position"),
                        rs.getDate("hire_date"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isAccountExist(String username, String phone, int idToIgnore){
        String sql = "SELECT id_nhan_vien FROM Staff WHERE (username = ? OR phone = ?) AND id_nhan_vien != ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, phone);
            stmt.setInt(3, idToIgnore);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean addStaff(Staff s) {
        String sql = "INSERT INTO Staff (phone, full_name, username, pass_word, position, hire_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String hashedPassword = BCrypt.hashpw(s.getPassword(), BCrypt.gensalt(12));

            stmt.setString(1, s.getPhone());
            stmt.setString(2, s.getName());
            stmt.setString(3, s.getUser());
            stmt.setString(4, hashedPassword);
            stmt.setString(5, s.getRole());
            stmt.setDate(6, s.getHire_date());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateStaffStatus(int id, String status) {
        String sql = "UPDATE Staff SET status = ? WHERE id_nhan_vien = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateStaff(Staff s) {
        String sql;
        boolean isChangePass = s.getPassword() != null && !s.getPassword().trim().isEmpty();

        if (isChangePass) sql = "UPDATE Staff SET phone = ?, full_name = ?, username = ?, pass_word = ?, position = ?, hire_date = ? WHERE id_nhan_vien = ?";
        else sql = "UPDATE Staff SET phone = ?, full_name = ?, username = ?, position = ?, hire_date = ? WHERE id_nhan_vien=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, s.getPhone());
            stmt.setString(2, s.getName());
            stmt.setString(3, s.getUser());

            int index = 4;
            if (isChangePass) {
                String hashedPassword = BCrypt.hashpw(s.getPassword(), BCrypt.gensalt());
                stmt.setString(index++, hashedPassword);
            }

            stmt.setString(index++, s.getRole());
            stmt.setDate(index++, s.getHire_date());
            stmt.setInt(index, s.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Staff getStaffByUsernameOrPhone(String user) {
        String sql = "SELECT * FROM Staff WHERE (username = ? OR phone = ?) AND status = 'Active'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user);
            stmt.setString(2, user);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Staff(
                            rs.getInt("id_nhan_vien"),
                            rs.getString("phone"),
                            rs.getString("full_name"),
                            rs.getString("username"),
                            rs.getString("pass_word"),
                            rs.getString("position"),
                            rs.getDate("hire_date"),
                            rs.getString("status")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
