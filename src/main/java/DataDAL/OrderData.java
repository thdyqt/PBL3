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
        String sql = "SELECT * FROM Orders WHERE status = 'Created'";

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

    public static int addOrder(Order order){
        //foreign keys are in here
        //specifically id_Staff and id_Customer
        //process_time is an exclusive attribute to Order so there's that
        String sql = "INSERT INTO Orders (process_time, id_Staff, id_Customer, status) VALUES (?, ?, ?, ?)";

        try (
            Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            //data loading
            //1,2,3,4 corresponding to the order in sql
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(order.getProcess_time()));
            stmt.setInt(2, order.getStaff().getId());

            if (order.getCustomer() != null) {
                stmt.setInt(3, order.getCustomer().getId());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }

            stmt.setString(4, order.getStatus().name());

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

    //the method that do the actual search work
    private static List<Order> executeQuery_Order(String sql, int searchPara){
        //create the empty list to hold (and later return) all records that fit the criteria
        List<Order> orderList = new ArrayList<>();

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            //prevent SQL injection by making it an int
            stmt.setInt(1, searchPara);

            try (ResultSet rs = stmt.executeQuery()){
                while (rs.next()){
                    //setting the 3 keys or Order
                    Order order = new Order();
                    order.setId(rs.getInt("id_Order"));
                    order.setProcess_time(rs.getTimestamp("process_time").toLocalDateTime());

                    Staff staff = new Staff();
                    staff.setId(rs.getInt("id_Staff"));
                    order.setStaff(staff);

                    Customer customer = new Customer();
                    customer.setId(rs.getInt("id_Customer"));
                    order.setCustomer(customer);

                    orderList.add(order);
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return orderList;
    }

    //same exact thing as above, but now search para is String instead of int
    private static List<Order> executeQuery_Order(String sql, String searchPara){
        //create the empty list to hold (and later return) all records that fit the criteria
        List<Order> orderList = new ArrayList<>();

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            //prevent SQL injection by making it an int
            stmt.setString(1, searchPara);

            try (ResultSet rs = stmt.executeQuery()){
                while (rs.next()){
                    //setting the 3 keys or Order
                    Order order = new Order();
                    order.setId(rs.getInt("id_Order"));
                    order.setProcess_time(rs.getTimestamp("process_time").toLocalDateTime());

                    Staff staff = new Staff();
                    staff.setId(rs.getInt("id_Staff"));
                    order.setStaff(staff);

                    Customer customer = new Customer();
                    customer.setId(rs.getInt("id_Customer"));
                    order.setCustomer(customer);

                    orderList.add(order);
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return orderList;
    }


    //mutiple search methods (ID/Staff/Customer)
    public static Order searchOrder_ByID(int id_Order){
        String sql = "SELECT * FROM Orders WHERE id_Order = ?";
        List<Order> result = executeQuery_Order(sql, id_Order);

        if (result.isEmpty()){
            return null;
        }
        return result.get(0);
    }

    //why list for those 2?
    //because a staff can process multiple order
    //so can a customer visit and buy multiple time
    public static List<Order> searchOrder_ByStaffID(int id_Staff){
        String sql = "SELECT * FROM Orders WHERE id_Staff = ?";
        return executeQuery_Order(sql, id_Staff);
    }

    public static List<Order> searchOrder_ByCustomerPhone(String phone){
        String sql = "SELECT o.* FROM Orders o " +
                "JOIN Customer c ON o.id_Customer = c.id_Customer " +
                "WHERE c.phone LIKE ?";

        //so that incomplete phone number input can still produce result with the number in it
        String searchPattern = "%" + phone + "%";

        return executeQuery_Order(sql, searchPattern);
    }

    //same thing as addOrder, albeit changed slightly
    public static boolean updateOrder(Order order){
        String sql = "UPDATE Orders SET process_time = ?, id_Staff = ?, id_Customer = ?, status = ? WHERE id_Order = ?";

        try (
            Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            //foreign keys
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(order.getProcess_time()));
            stmt.setInt(2, order.getStaff().getId());
            stmt.setInt(3, order.getCustomer().getId());

            //the id of the Order that need to be updated and its status
            stmt.setString(4, order.getStatus().name());
            stmt.setInt(5, order.getId());

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
