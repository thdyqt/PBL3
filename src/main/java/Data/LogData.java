package Data;

import Util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogData {
    public static void insertLog(String username, String action){
        String sql = "INSERT INTO ActivityLog (username, action) VALUES (?, ?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, username);
            stmt.setString(2, action);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Lỗi ghi log: " + e.getMessage());
        }
    }
}
