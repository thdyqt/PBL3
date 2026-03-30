package Data;

import Entity.OrderDetail;
import Util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
