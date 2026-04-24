package DataDAL;

import EntityDTO.DeliveryInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeliveryInfoData {
    public static DeliveryInfo getDeliveryInfoByOrderId(int orderId) {
        String sql = "SELECT * FROM DeliveryInfo WHERE id_Order = ?";
        try (Connection conn = Util.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new EntityDTO.DeliveryInfo(
                        rs.getInt("id_Order"),
                        rs.getString("receiver_name"),
                        rs.getString("receiver_phone"),
                        rs.getString("delivery_address")
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin giao hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void insertDeliveryInfo(Connection conn, int orderId, EntityDTO.DeliveryInfo info) throws SQLException {
        String sql = "INSERT INTO DeliveryInfo (id_Order, receiver_name, receiver_phone, delivery_address) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setString(2, info.getReceiverName());
            stmt.setString(3, info.getReceiverPhone());
            stmt.setString(4, info.getDeliveryAddress());
            stmt.executeUpdate();
        }
    }
}
