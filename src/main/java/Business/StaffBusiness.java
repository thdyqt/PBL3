package Business;

import Data.StaffData;
import Entity.Staff;
import Util.UserSession;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Date;

public class StaffBusiness {
    public static int login(String username, String password) {
        if (StaffData.checkLogin(username, password) == 1){
            LogBusiness.saveLoginLog();
            return 1;
        }
        return StaffData.checkLogin(username, password);
    }

    public static void logout() {
        if (UserSession.getInstance().getId() != -1) LogBusiness.saveLogoutLog();
        UserSession.getInstance().clearSession();
    }

    public static int register(String phone, String name, String username, String password, String position, Date hire_date) {
        if (StaffData.isAccountExist(username, phone)) return -1;

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        if (StaffData.addStaff(new Staff(phone, name, username, hashedPassword, position, hire_date))){
            return 1;
        }
        return 0;
    }
}
