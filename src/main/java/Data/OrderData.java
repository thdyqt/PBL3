package Data;

import Entity.Order;
import Util.DBConnection;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderData {
    public static List<Order> getAllOrders(){
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM Order WHERE status = 'active'";

        //open connection (conn) -> load sql query (stmt) -> return result (rs)
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            //get everything
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));

                order.setProcess_time(rs.getTimestamp("process_time").toLocalDateTime());

                list.add(order);
            }
        //shits fucked, aint it?
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean addOrder(Order order){
        String sql = "INSERT INTO `Order` (process_time, id_nhan_vien, id_khach_hang) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(order.getProcess_time()));

            // Assuming your Staff and Customer objects are already attached to the order
            stmt.setInt(2, order.getStaff().getId());
            stmt.setInt(3, order.getCustomer().getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Order searchOrder(String keyword){

    }

    public static boolean updateOrder(Order order){

    }

}
