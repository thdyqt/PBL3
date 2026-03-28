package Business;

import Data.StaffData;

public class StaffBusiness {
    public static int login(String username, String password) {
        if (StaffData.checkLogin(username, password) == 1){
            LogBusiness.saveLog(username, password);
            return 1;
        }
        return StaffData.checkLogin(username, password);
    }
}
