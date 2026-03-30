package Business;

import Data.StaffData;
import Util.UserSession;

public class StaffBusiness {
    public static int login(String username, String password) {
        if (StaffData.checkLogin(username, password) == 1){
            LogBusiness.saveLoginLog();
            return 1;
        }
        return StaffData.checkLogin(username, password);
    }

    public static void logout() {
        if (UserSession.getInstance().getStaffId() != -1) LogBusiness.saveLogoutLog();
        UserSession.getInstance().clearSession();
    }
}
