package Data;

import Entity.Customer;
import Util.DBConnection;
import Util.UserSession;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerData {
    public static List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM Customer WHERE status = 'Active'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Customer(
                        rs.getInt("id_khach_hang"),
                        rs.getString("phone"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("pass_word"),
                        rs.getInt("point")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isAccountExist(String username, String phone){
        String sql = "SELECT id_khach_hang FROM Customer WHERE username = ? OR phone = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, phone);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean addCustomer(Customer c) {
        String sql = "INSERT INTO Customer (phone, full_name, username, pass_word, point) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getPhone());
            stmt.setString(2, c.getName());
            stmt.setString(3, c.getUser());
            stmt.setString(4, c.getPassword());
            stmt.setInt(5, c.getPoint());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateCustomer(Customer c) {
        String sql = "UPDATE Customer SET phone = ?, full_name = ?, username = ?, pass_word = ?, point = ? WHERE id_khach_hang = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getPhone());
            stmt.setString(2, c.getName());
            stmt.setString(3, c.getUser());
            stmt.setString(4, c.getPassword());
            stmt.setInt(5, c.getPoint());
            stmt.setInt(6, c.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Customer> searchCustomer(String keyword) {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM Customer WHERE status = 'Active' AND " +
                "(phone LIKE ? OR full_name LIKE ? OR username LIKE ? OR point LIKE ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";

            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Customer(
                            rs.getInt("id_khach_hang"),
                            rs.getString("phone"),
                            rs.getString("full_name"),
                            rs.getString("username"),
                            rs.getString("pass_word"),
                            rs.getInt("point")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int checkLogin(String user, String pass) {
        String sql = "SELECT id_khach_hang, pass_word FROM Customer WHERE username = ? OR phone = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, user);
            stmt.setString(2, user);
            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    String dbPasswordHash = rs.getString("pass_word");

                    if (BCrypt.checkpw(pass, dbPasswordHash)){
                        int dbId = rs.getInt("id_khach_hang");
                        String dbPhone = rs.getString("phone");
                        String dbFullName = rs.getString("full_name");
                        String dbUsername = rs.getString("username");
                        int dbPoint = rs.getInt("point");
                        UserSession.getInstance().setCustomer(dbId, dbPhone, dbFullName, dbUsername, pass, dbPoint);
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