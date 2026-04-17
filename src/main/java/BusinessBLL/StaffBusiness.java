package BusinessBLL;

import DataDAL.StaffData;
import EntityDTO.Staff;
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
                        dbStaff.getHire_date()
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
        UserSession.getInstance().clearSession();
    }

    public static int register(String phone, String name, String username, String password, String position, Date hire_date) {
        if (StaffData.isAccountExist(username, phone, -1)) return -1;

        if (StaffData.addStaff(new Staff(phone, name, username, password, position, hire_date))){
            LogBusiness.saveLog("Thêm nhân viên " + name + " (" + username + ") vào hệ thống");
            return 1;
        }
        return 0;
    }

    public static int updateStaff(int id, String phone, String name, String username, String password, String position, Date hire_date) {
        if (StaffData.isAccountExist(username, phone, id)) return -1;

        if (StaffData.updateStaff(new Staff(id, phone, name, username, password, position, hire_date))){
            LogBusiness.saveLog("Chỉnh sửa nhân viên " + name + " (" + username + ")");
            return 1;
        }
        return 0;
    }

    public static int resignStaff(int id, String name, String username){
        if (StaffData.resignStaff(id)) {
            LogBusiness.saveLog("Thôi việc nhân viên " + name + " (" + username + ")");
            return 1;
        }
        return 0;
    }
}
