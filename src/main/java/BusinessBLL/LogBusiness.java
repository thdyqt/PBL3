package BusinessBLL;

import DataDAL.LogData;
import Util.UserSession;

public class LogBusiness {
    public static void saveLog(String message){
        final String currentUser = UserSession.getInstance().getUsername();
        new Thread(() -> {
            LogData.insertLog(currentUser, message);
        }).start();
    }
}
