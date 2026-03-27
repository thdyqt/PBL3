package Business;

import Data.StaffData;

public class StaffBusiness {
    public static int login(String username, String password) {
        username = username.trim();
        password = password.trim();
        if (!username.matches("^[a-zA-Z0-9]+$") || !password.matches("^[a-zA-Z0-9]+$")) {
            return -2;
        }
        return StaffData.checkLogin(username, password);
    }
}
