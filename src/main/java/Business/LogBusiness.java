package Business;

import Data.LogData;
import Util.UserSession;

public class LogBusiness {
    public static void saveLoginLog(){
        final String currentUser = UserSession.getInstance().getUsername();
        new Thread(() -> {
            LogData.insertLog(currentUser, "Đăng nhập vào hệ thống");
        }).start();
    }

    public static void saveLogoutLog(){
        final String currentUser = UserSession.getInstance().getUsername();
        new Thread(() -> {
            LogData.insertLog(currentUser, "Đăng xuất khỏi hệ thống");
        }).start();
    }
}
