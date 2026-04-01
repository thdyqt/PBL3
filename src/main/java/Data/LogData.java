package Data;

import Util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class LogData {
    // LUÔN SỬ DỤNG HÀM NÀY ĐỂ GHI NHẬT KÝ HOẠT ĐỘNG VÀO DATABASE
    public static void insertLog(String username, String action){
        String sql = "INSERT INTO ActivityLog (username, action, created_at) VALUES (?, ?, ?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, username);
            stmt.setString(2, action);

            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            stmt.setTimestamp(3, Timestamp.valueOf(now));

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Lỗi ghi log: " + e.getMessage());
        }
    }
}
