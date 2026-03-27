package Data;

import Entity.Staff;
import java.sql.*;
import java.util.*;

import Util.DBConnection;
import Util.UserSession;
import org.mindrot.jbcrypt.BCrypt;

public class StaffData {
    public List<Staff> getAllStaff() {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM Staff WHERE status = 'Active'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Staff(
                        rs.getInt("id_nhan_vien"),
                        rs.getString("phone"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("pass_word"),
                        rs.getString("position"),
                        rs.getDate("hire_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addStaff(Staff s) {
        String sql = "INSERT INTO Staff (phone, full_name, username, pass_word, position, hire_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, s.getPhone());
            stmt.setString(2, s.getName());
            stmt.setString(3, s.getUser());
            stmt.setString(4, s.getPassword());
            stmt.setString(5, s.getRole());
            stmt.setDate(6, s.getHire_date());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean resignStaff(int id) {
        String sql = "UPDATE Staff SET status = 'Inactive' WHERE id_nhan_vien = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStaff(Staff s) {
        String sql = "UPDATE Staff SET phone = ?, full_name = ?, username = ?, pass_word = ?, position = ?, hire_date = ? WHERE id_nhan_vien = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, s.getPhone());
            stmt.setString(2, s.getName());
            stmt.setString(3, s.getUser());
            stmt.setString(4, s.getPassword());
            stmt.setString(5, s.getRole());
            stmt.setDate(6, s.getHire_date());
            stmt.setInt(7, s.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Staff> searchStaff(String keyword) {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM Staff WHERE status = 'Active' AND " +
                "(phone LIKE ? OR full_name LIKE ? OR username LIKE ? OR position LIKE ? OR hire_date LIKE ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";

            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            stmt.setString(5, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Staff(
                            rs.getInt("id_nhan_vien"),
                            rs.getString("phone"),
                            rs.getString("full_name"),
                            rs.getString("username"),
                            rs.getString("pass_word"),
                            rs.getString("position"),
                            rs.getDate("hire_date")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int checkLogin(String user, String pass) {
        String sql = "SELECT id_nhan_vien, pass_word, position FROM Staff WHERE username = ? OR phone = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, user);
            stmt.setString(2, user);
            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    String dbPasswordHash = rs.getString("pass_word");

                    if (BCrypt.checkpw(pass, dbPasswordHash)){
                        int dbId = rs.getInt("id_nhan_vien");
                        String dbPosition = rs.getString("position");
                        UserSession.getInstance().setUser(dbId, user, dbPosition);
                        return 1;
                    }
                    else return 2;
                }
                else return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi BCrypt: Mật khẩu dưới DB chưa được mã hóa đúng chuẩn!");
            return -1;
        }
    }
}
