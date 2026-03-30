package Data;

import Entity.OrderDetail;
import Entity.Product;
import Entity.Order;

import Util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//CRUD operations on the Product into Order
//basically adding/deleting items from a grocery bag
public class OrderDetailData {
    //CRUD operations
    public static boolean addProduct_OrderDetail(int id_Order, OrderDetail orderDetail){
        String sql = "INSERT INTO `OrderDetail` (id_Order, id_Product, quanity, price) VALUES (?, ?, ?, ?)";

        try (
            Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, id_Order);


            int rowsAffected = stmt.executeUpdate();
            //it added something -> return true
            return rowsAffected > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
