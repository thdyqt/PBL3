package Data;

import Entity.Customer;
import Entity.Order;
import Entity.Staff;

import Util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//CRUD operations
public class OrderData {
    public static List<Order> getAllOrders(){
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM Order WHERE status = 'active'";

        //open connection (conn) -> load sql query (stmt) -> return result (rs)
        try (
            Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()
        ) {

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
        //foreign keys are in here
        //specifically id_Staff and id_Customer
        //process_time is an exclusive attribute to Order so there's that
        String sql = "INSERT INTO Order (process_time, id_Staff, id_Customer) VALUES (?, ?, ?)";

        try (
            Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            //data loading
            //1,2,3 corresponding to the order in sql
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(order.getProcess_time()));
            stmt.setInt(2, order.getStaff().getId());
            stmt.setInt(3, order.getCustomer().getId());

            int rowsAffected = stmt.executeUpdate();
            //it added something -> return true
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Order searchOrder(int id){
        String sql = "SELECT * FROM Order WHERE id = ?";
        Order foundOrder = null;

        try (
            Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    foundOrder = new Order();
                    foundOrder.setId(rs.getInt("id"));
                    foundOrder.setProcess_time(rs.getTimestamp("process_time").toLocalDateTime());

                    Customer customer = new Customer();
                    customer.setId(rs.getInt("id_Customer"));
                    foundOrder.setCustomer(customer);

                    Staff staff = new Staff();
                    customer.setId(rs.getInt("id_Staff"));
                    foundOrder.setStaff(staff);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundOrder;
    }

    //same thing as addOrder, albeit changed slightly
    public static boolean updateOrder(Order order){
        String sql = "UPDATE Order SET process_time = ?, id_nhan_vien = ?, id_khach_hang = ? WHERE id = ?";

        try (
            Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            //foreign keys
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(order.getProcess_time()));
            stmt.setInt(2, order.getStaff().getId());
            stmt.setInt(3, order.getCustomer().getId());

            //the id of the Order that need to be updated
            stmt.setInt(4, order.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
