package Business;

import Data.LogData;
import Util.UserSession;

public class LogBusiness {
    public static void saveLog(String username, String action){
        final String currentUser = UserSession.getInstance().getUsername();
        new Thread(() -> {
            LogData.insertLog(currentUser, "Đăng nhập vào hệ thống");
        }).start();
    }
}
