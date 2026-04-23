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
        String sql = "SELECT o.*, s.full_name AS staff_name, s.username AS staff_username, " +
                "c.full_name AS customer_name, c.phone AS customer_phone, c.point AS customer_point " +
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
                order.setOrderTime(rs.getTimestamp("order_time").toLocalDateTime());
                order.setSubTotal(rs.getInt("subtotal"));
                order.setDiscountAmount(rs.getInt("discount_amount"));
                order.setAppliedCode(rs.getString("applied_promo_code"));
                order.setFinalAmount(rs.getInt("final_total"));
                order.setAddress(rs.getString("address"));
                order.setCancelReason(rs.getString("cancel_reason"));

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
                    int customerPoint = rs.getInt("customer_point");
                    if (customerPoint < 100) {
                        customer.setCustomerRank(Customer.Rank.Bronze);
                    } else if (customerPoint < 500) {
                        customer.setCustomerRank(Customer.Rank.Silver);
                    } else if (customerPoint < 1000) {
                        customer.setCustomerRank(Customer.Rank.Gold);
                    } else if (customerPoint < 2000) {
                        customer.setCustomerRank(Customer.Rank.Diamond);
                    } else {
                        customer.setCustomerRank(Customer.Rank.Emerald);
                    }
                    order.setCustomer(customer);
                    customer.setPoint(rs.getInt("customer_point"));
                }

                order.setStatus(Order.OrderStatus.valueOf(rs.getString("status")));
                order.setType(Order.OrderType.valueOf(rs.getString("type")));
                order.setPayment(Order.OrderPayment.valueOf(rs.getString("payment")));

                list.add(order);
            }
        //shits fucked, aint it?
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Order> getOnlineOrders() {
        List<Order> list = new ArrayList<>();

        // 1. Chuyển 'JOIN Staff' thành 'LEFT JOIN Staff' để giữ lại các đơn chưa có nhân viên
        // 2. Thêm mệnh đề WHERE để chỉ lấy đơn hàng Online
        String sql = "SELECT o.*, s.full_name AS staff_name, s.username AS staff_username, " +
                "c.full_name AS customer_name, c.phone AS customer_phone, c.point AS customer_point " +
                "FROM Orders o " +
                "LEFT JOIN Staff s ON o.id_Staff = s.id_nhan_vien " +
                "LEFT JOIN Customer c ON o.id_Customer = c.id_khach_hang " +
                "WHERE o.type = 'ONLINE'"; // Lưu ý: Điều chỉnh 'ONLINE' sao cho khớp với giá trị Enum/String lưu trong DB của bạn

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id_Order"));
                order.setOrderTime(rs.getTimestamp("order_time").toLocalDateTime());
                order.setSubTotal(rs.getInt("subtotal"));
                order.setDiscountAmount(rs.getInt("discount_amount"));
                order.setAppliedCode(rs.getString("applied_promo_code"));
                order.setFinalAmount(rs.getInt("final_total"));
                order.setAddress(rs.getString("address"));
                order.setCancelReason(rs.getString("cancel_reason"));

                // Bổ sung kiểm tra rs.wasNull() cho Staff giống như Customer
                int id_Staff = rs.getInt("id_Staff");
                if (!rs.wasNull()) {
                    Staff staff = new Staff();
                    staff.setId(id_Staff);
                    staff.setName(rs.getString("staff_name"));
                    staff.setUser(rs.getString("staff_username"));
                    order.setStaff(staff);
                }
                // Nếu rs.wasNull() là true -> Không set Staff, order.getStaff() sẽ trả về null (hợp lý với đơn chờ)

                // Phần xử lý Customer cũ đang làm rất tốt, giữ nguyên
                int id_Customer = rs.getInt("id_Customer");
                if (!rs.wasNull()){
                    Customer customer = new Customer();
                    customer.setId(id_Customer);
                    customer.setName(rs.getString("customer_name"));
                    customer.setPhone(rs.getString("customer_phone"));
                    int customerPoint = rs.getInt("customer_point");
                    if (customerPoint < 100) {
                        customer.setCustomerRank(Customer.Rank.Bronze);
                    } else if (customerPoint < 500) {
                        customer.setCustomerRank(Customer.Rank.Silver);
                    } else if (customerPoint < 1000) {
                        customer.setCustomerRank(Customer.Rank.Gold);
                    } else if (customerPoint < 2000) {
                        customer.setCustomerRank(Customer.Rank.Diamond);
                    } else {
                        customer.setCustomerRank(Customer.Rank.Emerald);
                    }
                    customer.setPoint(customerPoint); // Dòng này lúc nãy bạn để dưới cùng, tôi đưa vào trong if luôn cho gọn
                    order.setCustomer(customer);
                }

                order.setStatus(Order.OrderStatus.valueOf(rs.getString("status")));
                order.setType(Order.OrderType.valueOf(rs.getString("type")));
                order.setPayment(Order.OrderPayment.valueOf(rs.getString("payment")));

                list.add(order);
            }
        } catch (SQLException e) {
            // Tốt nhất ở môi trường thực tế bạn nên dùng Logger thay vì in ra console thế này
            e.printStackTrace();
        }
        return list;
    }

    public static int addOrder(Order order) {
        Connection conn = null;
        int generatedOrderId = -1;

        try {
            conn = Util.DBConnection.getConnection();
            conn.setAutoCommit(false);

            generatedOrderId = insertOrderRecord(conn, order);
            order.setId(generatedOrderId);

            OrderDetailData.addOrderDetailsBatch(conn, generatedOrderId, order.getOrderDetail());

            ProductData.reduceStockBatch(conn, order.getOrderDetail());

            if (order.getCustomer() != null && order.getStatus() == Order.OrderStatus.Finished) {
                int pointsEarned = order.getFinalAmount() / 1000;
                if (pointsEarned > 0) {
                    CustomerData.addRewardPoints(conn, order.getCustomer().getId(), pointsEarned);
                }
            }

            conn.commit();
            return generatedOrderId;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Đã Rollback Transaction do lỗi hệ thống!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return -1;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static int insertOrderRecord(Connection conn, Order order) throws SQLException {
        String sqlOrder = "INSERT INTO Orders (order_time, id_Staff, id_Customer, status, type, payment, subtotal, discount_amount, final_total, applied_promo_code, address) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmtOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
            stmtOrder.setTimestamp(1, Timestamp.valueOf(order.getOrderTime()));

            if (order.getStaff() != null) stmtOrder.setInt(2, order.getStaff().getId());
            else stmtOrder.setNull(2, Types.INTEGER);

            if (order.getCustomer() != null) stmtOrder.setInt(3, order.getCustomer().getId());
            else stmtOrder.setNull(3, Types.INTEGER);

            stmtOrder.setString(4, order.getStatus().name());
            stmtOrder.setString(5, order.getType().name());
            stmtOrder.setString(6, order.getPayment().name());
            stmtOrder.setInt(7, order.getSubTotal());
            stmtOrder.setInt(8, order.getDiscountAmount());
            stmtOrder.setInt(9, order.getFinalAmount());

            if (order.getAppliedCode() != null) stmtOrder.setString(10, order.getAppliedCode());
            else stmtOrder.setNull(10, Types.VARCHAR);

            if (order.getAddress() != null) stmtOrder.setString(11, order.getAddress());
            else stmtOrder.setNull(11, Types.VARCHAR);

            stmtOrder.executeUpdate();

            try (ResultSet rs = stmtOrder.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Không thể lấy ID của đơn hàng mới!");
                }
            }
        }
    }

    //mutiple search methods (ID/Staff/Customer)
    public static Order searchOrder_ByID(int id_Order){
        String sql = "SELECT o.*, s.full_name AS staff_name, s.username AS staff_username, c.full_name AS customer_name, c.phone AS customer_phone " +
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
                order.setOrderTime(rs.getTimestamp("order_time").toLocalDateTime());
                order.setStatus(Order.OrderStatus.valueOf(rs.getString("status")));
                order.setType(Order.OrderType.valueOf(rs.getString("type")));
                order.setPayment(Order.OrderPayment.valueOf(rs.getString("payment")));
                order.setSubTotal(rs.getInt("subtotal"));
                order.setDiscountAmount(rs.getInt("discount_amount"));
                order.setAppliedCode(rs.getString("applied_promo_code"));
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
        String sql = "UPDATE Orders SET order_time = ?, id_Staff = ?, id_Customer = ?, status = ?, type = ?, payment = ?, subtotal = ?, discount_amount = ?, applied_promo_code = ?, final_total = ?, address = ?, cancel_reason = ? WHERE id_Order = ?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            // Parameters 1, 2, 3: Timestamp and Foreign Keys
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(order.getOrderTime()));
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

            // Parameters 7, 8, 9, 10: The new math values
            stmt.setInt(7, order.getSubTotal());
            stmt.setInt(8, order.getDiscountAmount());
            stmt.setString(9, order.getAppliedCode());
            stmt.setInt(10, order.getFinalAmount());

            //the 2 motherfucking new values
            stmt.setString(11, order.getAddress());
            stmt.setString(12, order.getCancelReason());

            // Parameter 13: The Order ID used in the WHERE clause
            stmt.setInt(13, order.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
