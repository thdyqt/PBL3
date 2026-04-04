package DataDAL;

import EntityDTO.OrderDetail;
import EntityDTO.Product;

import Util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//CRUD operations on the Product into Order
//basically adding/deleting items from a grocery bag
public class OrderDetailData {
    //CRUD operations
    public static boolean addProduct_OrderDetail(int id_Order, OrderDetail orderDetail){
        String sql = "INSERT INTO `OrderDetail` (id_Order, id_Product, quantity, price) VALUES (?, ?, ?, ?)";

        try (
            Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, id_Order);
            stmt.setInt(2, orderDetail.getProduct().getProductID());
            stmt.setInt(3, orderDetail.getProduct().getQuantity());
            stmt.setInt(4, orderDetail.getTotalPrice());

            int rowsAffected = stmt.executeUpdate();
            //it added something -> return true
            return rowsAffected > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
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
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setQuantity(rs.getInt("quanity"));

                Order order = new Order();
                order.setId(rs.getInt("id_Order"));
                orderDetail.setOrder(order);

                Product product = new Product();
                product.setProductID(rs.getInt("id_Product"));
                orderDetail.setProduct(product);

                orderDetailList.add(orderDetail);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return orderDetailList;
    }

    //mutiple search methods (id_Order/id_Product)
    public static List<OrderDetail> searchOrderDetail_ById_Order(int id_Order){
        String sql = "SELECT * FROM OrderDetail WHERE id_order = ?";
        return executeQuery_OrderDetail(sql, id_Order);
    }

    public static List<OrderDetail> searchOrderDetail_ById_Product(int id_Product){
        String sql = "SELECT * FROM OrderDetail WHERE id_Product = ?";
        return executeQuery_OrderDetail(sql, id_Product);
    }

    //this just change the amount of a product
    public static boolean updateOrderDetail(int newQuantity, int id_Order, int id_Product){
        String sql = "UPDATE `OrderDetail` SET quantity = ? WHERE id_Order = ? AND id_Product = ?";
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
        }
        return false;
    }

    public static boolean deleteOrderDetail(int id_Order, int id_Product){
        String sql = "DELETE FROM `OrderDetail` WHERE id_Order = ? AND id_Product = ?";
        try(
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setInt(1, id_Order);
            stmt.setInt(2, id_Product);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
