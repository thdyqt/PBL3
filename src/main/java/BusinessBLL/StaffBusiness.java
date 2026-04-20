package BusinessBLL;

import DataDAL.StaffData;
import EntityDTO.Staff;
import Util.CartManager;
import Util.UserSession;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Date;
import java.util.List;

public class StaffBusiness {
    public static List<Staff> getAllStaff() {
        return StaffData.getAllStaff();
    }

    public static String login(String username, String password) {
        try {
            Staff dbStaff = StaffData.getStaffByUsernameOrPhone(username);

            if (dbStaff == null) {
                return "NOT FOUND";
            }

            if (BCrypt.checkpw(password, dbStaff.getPassword())) {
                UserSession.getInstance().setStaff(
                        dbStaff.getId(),
                        dbStaff.getPhone(),
                        dbStaff.getName(),
                        dbStaff.getUser(),
                        password,
                        dbStaff.getRole(),
                        dbStaff.getHireDate()
                );

                LogBusiness.saveLog("Đăng nhập vào hệ thống");
                return "SUCCESS";
            }
            else {
                return "WRONG PASSWORD";
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi BCrypt: Mật khẩu dưới DB chưa được mã hóa đúng chuẩn!");
            return "SERVER ERROR";
        } catch (Exception e) {
            e.printStackTrace();
            return "SERVER ERROR";
        }
    }

    public static void logout() {
        if (UserSession.getInstance().getId() != -1) LogBusiness.saveLog("Đăng xuất khỏi hệ thống");
        CartManager.getInstance().clearPosCart();
        UserSession.getInstance().clearSession();
    }

    public static int register(String phone, String name, String username, String password, String position, Date hire_date, String status) {
        if (StaffData.isAccountExist(username, phone, -1)) return -1;

        if (StaffData.addStaff(new Staff(phone, name, username, password, position, hire_date, status))){
            LogBusiness.saveLog("Thêm nhân viên " + name + " (" + username + ") vào hệ thống");
            return 1;
        }
        return 0;
    }

    public static int updateStaff(int id, String phone, String name, String username, String password, String position, Date hire_date, String status) {
        if (StaffData.isAccountExist(username, phone, id)) return -1;

        if (StaffData.updateStaff(new Staff(id, phone, name, username, password, position, hire_date, status))){
            LogBusiness.saveLog("Chỉnh sửa nhân viên " + name + " (" + username + ")");
            return 1;
        }
        return 0;
    }

    public static int updateStaffStatus(int id, String name, String username, String status){
        if (StaffData.updateStaffStatus(id, status)) {
            String actionName = "Active".equalsIgnoreCase(status) ? "Khôi phục làm việc" : "Thôi việc";
            LogBusiness.saveLog(actionName + " nhân viên " + name + " (" + username + ")");
            return 1;
        }
        return 0;
    }
}
