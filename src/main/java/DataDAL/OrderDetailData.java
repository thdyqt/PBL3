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

    public static List<OrderDetail> searchOrderDetail(int id_Order){
        List<OrderDetail> found = new ArrayList<>();
        String sql = "SELECT * FROM `OrderDetail` WHERE id_Order = ?";
        try(
                Connection connection = DBConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ){

            stmt.setInt(1, id_Order);

            try(ResultSet rs = stmt.executeQuery()){
                while (rs.next()){
                    int id_Product = rs.getInt("id_Product");
                    int quantity = rs.getInt("quantity");
                    int price = rs.getInt("price");

                    Product product = new Product();
                    product.setProductID(id_Product);

                    OrderDetail item = new OrderDetail(id_Order, product, quantity, price);

                    found.add(item);
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return found;
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
