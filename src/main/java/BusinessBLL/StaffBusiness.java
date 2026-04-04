package BusinessBLL;

import DataDAL.StaffData;
import EntityDTO.Staff;
import Util.UserSession;

import java.sql.Date;

public class StaffBusiness {
    public static int login(String username, String password) {
        if (StaffData.checkLogin(username, password) == 1){
            LogBusiness.saveLog("Đăng nhập vào hệ thống");
            return 1;
        }
        return StaffData.checkLogin(username, password);
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
