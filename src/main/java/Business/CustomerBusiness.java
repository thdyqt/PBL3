package Business;

import Data.CustomerData;
import Entity.Customer;
import org.mindrot.jbcrypt.BCrypt;

public class CustomerBusiness {
    public static int login(String username, String password) {
        return CustomerData.checkLogin(username, password);
    }

    public static int register(String phone, String name, String username, String password) {
        if (CustomerData.isAccountExist(username, phone)) return -1;

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        if (CustomerData.addCustomer(new Customer(phone, name, username, hashedPassword))){
            return 1;
        }
        return 0;
    }
}
