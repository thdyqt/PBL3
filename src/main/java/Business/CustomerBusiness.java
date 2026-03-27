package Business;

import Data.CustomerData;

public class CustomerBusiness {
    public static int login(String username, String password) {
        return CustomerData.checkLogin(username, password);
    }
}
