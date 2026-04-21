package DataDAL;

import EntityDTO.OrderDetail;
import EntityDTO.Product;
import EntityDTO.Order;

import Util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailData {
    public static void addOrderDetailsBatch(Connection conn, int orderId, List<OrderDetail> details) throws SQLException {
        String sql = "INSERT INTO OrderDetail (id_Order, id_Product, quantity, price, totalPrice) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (OrderDetail item : details) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, item.getProduct().getProductID());
                stmt.setInt(3, item.getQuantity());
                stmt.setInt(4, item.getPrice());
                stmt.setInt(5, item.getTotalPrice());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    //method that does the actual work
    private static List<OrderDetail> executeQuery_OrderDetail(String sql, int searchPara){
        List<OrderDetail> orderDetailList = new ArrayList<>();

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setInt(1, searchPara);

            try (ResultSet rs = stmt.executeQuery()){
                while (rs.next()){
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setQuantity(rs.getInt("quantity"));
                    orderDetail.setPrice(rs.getInt("price"));
                    orderDetail.setTotalPrice(rs.getInt("totalPrice"));

                    Order order = new Order();
                    order.setId(rs.getInt("id_Order"));
                    orderDetail.setOrder(order);

                    Product product = new Product();
                    product.setProductID(rs.getInt("id_Product"));
                    product.setProductName(rs.getString("ProductName"));
                    product.setCategoryID(rs.getInt("CategoryID"));
                    orderDetail.setProduct(product);

                    orderDetailList.add(orderDetail);
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return orderDetailList;
    }

    //mutiple search methods (id_Order/id_Product)
    public static List<OrderDetail> searchOrderDetail_ById_Order(int id_Order){
        String sql = "SELECT od.*, p.ProductName, p.CategoryID " +
                "FROM OrderDetail od " +
                "JOIN Product p ON od.id_Product = p.ProductID " +
                "WHERE od.id_Order = ?";
        return executeQuery_OrderDetail(sql, id_Order);
    }

    public static List<OrderDetail> searchOrderDetail_ById_Product(int id_Product){
        String sql = "SELECT od.*, p.ProductName, p.CategoryID " +
                "FROM OrderDetail od " +
                "JOIN Product p ON od.id_Product = p.ProductID " +
                "WHERE od.id_Product = ?";
        return executeQuery_OrderDetail(sql, id_Product);
    }

    //this just change the amount of a product
    public static boolean updateOrderDetail(int newQuantity, int id_Order, int id_Product){
        String sql = "UPDATE OrderDetail SET quantity = ? WHERE id_Order = ? AND id_Product = ?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setInt(1, newQuantity);

            stmt.setInt(2, id_Order);
            stmt.setInt(3, id_Product);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
