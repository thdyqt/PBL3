package DataDAL;

import EntityDTO.Customer;
import Util.DBConnection;
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
                        rs.getString("address"),
                        rs.getInt("point")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isAccountExist(String username, String phone, int idToIgnore){
        String sql = "SELECT id_khach_hang FROM Customer WHERE ((username = ? AND username != '') OR phone = ?) AND id_khach_hang != ?";

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

    public static boolean addCustomer(Customer c) {
        String sql = "INSERT INTO Customer (phone, full_name, username, pass_word, point) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String hashedPassword = BCrypt.hashpw(c.getPassword(), BCrypt.gensalt(12));

            stmt.setString(1, c.getPhone());
            stmt.setString(2, c.getName());
            stmt.setString(3, c.getUser());
            stmt.setString(4, hashedPassword);
            stmt.setInt(5, c.getPoint());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateCustomer(Customer c) {
        String sql;
        boolean isChangePass = c.getPassword() != null && !c.getPassword().trim().isEmpty();

        if (isChangePass) {
            sql = "UPDATE Customer SET phone = ?, full_name = ?, username = ?, pass_word = ?, address = ?, point = ? WHERE id_khach_hang = ?";
        } else {
            sql = "UPDATE Customer SET phone = ?, full_name = ?, username = ?, address = ?, point = ? WHERE id_khach_hang = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getPhone());
            stmt.setString(2, c.getName());
            stmt.setString(3, c.getUser());

            int index = 4;
            if (isChangePass) {
                String hashedPassword = BCrypt.hashpw(c.getPassword(), BCrypt.gensalt(12));
                stmt.setString(index++, hashedPassword);
            }
            stmt.setString(index++, c.getAddress());
            stmt.setInt(index++, c.getPoint());
            stmt.setInt(index, c.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Customer getCustomerByUsernameOrPhone(String user) {
        String sql = "SELECT id_khach_hang, phone, full_name, username, pass_word, address, point FROM Customer WHERE username = ? OR phone = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user);
            stmt.setString(2, user);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getInt("id_khach_hang"),
                            rs.getString("phone"),
                            rs.getString("full_name"),
                            rs.getString("username"),
                            rs.getString("pass_word"),
                            rs.getString("address"),
                            rs.getInt("point")
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