package DataDAL;

import EntityDTO.Customer;
import EntityDTO.Order;
import EntityDTO.Staff;

import Util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//CRUD operations
public class OrderData {
    public static List<Order> getAllOrders(){
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, s.full_name AS staff_name, s.username AS staff_username, c.full_name AS customer_name, c.phone AS customer_phone " +
                "FROM Orders o " +
                "JOIN Staff s ON o.id_Staff = s.id_nhan_vien " +
                "LEFT JOIN Customer c ON o.id_Customer = c.id_khach_hang";

        //open connection (conn) -> load sql query (stmt) -> return result (rs)
        try (
            Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()
        ) {

            //get everything there is of order
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id_Order"));
                order.setProcess_time(rs.getTimestamp("process_time").toLocalDateTime());
                order.setSubTotal(rs.getInt("subtotal"));
                order.setDiscountAmount(rs.getInt("discount_amount"));
                order.setFinalAmount(rs.getInt("final_total"));

                Staff staff = new Staff();
                staff.setId(rs.getInt("id_Staff"));
                staff.setName(rs.getString("staff_name"));
                staff.setUser(rs.getString("staff_username"));
                order.setStaff(staff);

                int id_Customer = rs.getInt("id_Customer");
                if (!rs.wasNull()){
                    Customer customer = new Customer();
                    customer.setId(id_Customer);
                    customer.setName(rs.getString("customer_name"));
                    customer.setPhone(rs.getString("customer_phone"));
                    order.setCustomer(customer);
                }

                order.setStatus(Order.orderStatus.valueOf(rs.getString("status")));
                order.setType(Order.orderType.valueOf(rs.getString("type")));
                order.setPayment(Order.orderPayment.valueOf(rs.getString("payment")));

                list.add(order);
            }
        //shits fucked, aint it?
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int addOrder(Order order){
        //foreign keys are in here
        //specifically id_Staff and id_Customer
        //process_time is an exclusive attribute to Order so there's that
        String sql = "INSERT INTO Orders (process_time, id_Staff, id_Customer, status, type, payment, subtotal, discount_amount, final_total) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            //data loading
            //1,2,3,4 corresponding to the order in sql
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(order.getProcess_time()));
            stmt.setInt(2, order.getStaff().getId());

            if (order.getCustomer() != null && order.getCustomer().getId() > 0) {
                stmt.setInt(3, order.getCustomer().getId());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }

            stmt.setString(4, order.getStatus().name());
            stmt.setString(5, order.getType().name());
            stmt.setString(6, order.getPayment().name());

            stmt.setInt(7, order.getSubTotal());
            stmt.setInt(8, order.getDiscountAmount());
            stmt.setInt(9, order.getFinalAmount());


            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                        //this method now return the id of the created order
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    //mutiple search methods (ID/Staff/Customer)
    public static Order searchOrder_ByID(int id_Order){
        String sql = "SELECT o.*, s.full_name AS staff_name, c.full_name AS customer_name, c.phone AS customer_phone " +
                "FROM Orders o " +
                "JOIN Staff s ON o.id_Staff = s.id_nhan_vien " +
                "LEFT JOIN Customer c ON o.id_Customer = c.id_khach_hang " +
                "WHERE o.id_Order = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setInt(1, id_Order);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                Order order = new Order();
                order.setId(rs.getInt("id_Order"));
                order.setProcess_time(rs.getTimestamp("process_time").toLocalDateTime());
                order.setStatus(Order.orderStatus.valueOf(rs.getString("status")));
                order.setType(Order.orderType.valueOf(rs.getString("type")));
                order.setPayment(Order.orderPayment.valueOf(rs.getString("payment")));
                order.setSubTotal(rs.getInt("subtotal"));
                order.setDiscountAmount(rs.getInt("discount_amount"));
                order.setFinalAmount(rs.getInt("final_total"));

                Staff staff = new Staff();
                staff.setId(rs.getInt("id_Staff"));
                staff.setName(rs.getString("staff_name"));
                staff.setUser(rs.getString("staff_username"));
                order.setStaff(staff);

                int customerId = rs.getInt("id_Customer");
                if (!rs.wasNull()){
                    Customer customer = new Customer();
                    customer.setId(customerId);
                    customer.setName(rs.getString("customer_name"));
                    customer.setPhone(rs.getString("customer_phone"));
                    order.setCustomer(customer);
                }

                return order;
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    //same thing as addOrder, albeit changed slightly
    public static boolean updateOrder(Order order){
        // 1. Updated SQL string with the 3 new columns added before the WHERE clause
        String sql = "UPDATE Orders SET process_time = ?, id_Staff = ?, id_Customer = ?, status = ?, type = ?, payment = ?, subtotal = ?, discount_amount = ?, final_total = ? WHERE id_Order = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            // Parameters 1, 2, 3: Timestamp and Foreign Keys
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(order.getProcess_time()));
            stmt.setInt(2, order.getStaff().getId());

            if (order.getCustomer() != null) {
                stmt.setInt(3, order.getCustomer().getId());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }

            // Parameters 4, 5, 6: Enums
            stmt.setString(4, order.getStatus().name());
            stmt.setString(5, order.getType().name());
            stmt.setString(6, order.getPayment().name());

            // Parameters 7, 8, 9: The new math values
            stmt.setInt(7, order.getSubTotal());
            stmt.setInt(8, order.getDiscountAmount());
            stmt.setInt(9, order.getFinalAmount());

            // Parameter 10: The Order ID used in the WHERE clause
            stmt.setInt(10, order.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteOrder(int OrderID){
        String sql = "DELETE FROM Orders WHERE id_Order = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, OrderID);

            // Returns true if 1 or more rows were deleted
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
