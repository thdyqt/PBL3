package Business;

import Data.CustomerData;
import Entity.Customer;

public class CustomerBusiness {
    public static int login(String username, String password) {
        return CustomerData.checkLogin(username, password);
    }

    public static int register(String phone, String name, String username, String password) {
        if (CustomerData.isAccountExist(username, phone)) return -1;

        if (CustomerData.addCustomer(new Customer(phone, name, username, password))){
            return 1;
        }
        return 0;
    }
}
