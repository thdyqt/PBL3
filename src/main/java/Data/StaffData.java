package Data;
import Entity.Staff;
import Util.DBConnection;
import java.sql.*;
import java.util.*;

public class StaffData {
    public List<Staff> getAllStaff(){
        List<Staff> list = new ArrayList<>();
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM staff");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                list.add(new Staff(rs.getString(1), rs.getString(2),
                        rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getTimestamp(6).toLocalDateTime()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
