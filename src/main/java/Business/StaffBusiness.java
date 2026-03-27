package Business;

import Data.StaffData;

public class StaffBusiness {
    public static int login(String username, String password) {
        return StaffData.checkLogin(username, password);
    }
}
