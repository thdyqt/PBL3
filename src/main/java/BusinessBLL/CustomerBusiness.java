package BusinessBLL;

import DataDAL.CustomerData;
import EntityDTO.Customer;

public class CustomerBusiness {

    public static int login(String username, String password) {
        return CustomerData.checkLogin(username, password);
    }

    // Hàm Thêm mới
    public static int register(String phone, String name, String username, String password) {
        if (CustomerData.isAccountExist(username, phone, -1)) return -1;

        if (CustomerData.addCustomer(new Customer(phone, name, username, password))){
            LogBusiness.saveLog("Thêm khách hàng " + name + " (" + phone + ") vào hệ thống");
            return 1;
        }
        return 0;
    }

    public static int updateCustomer(int id, String phone, String name, String username, String password, int point) {
        if (CustomerData.isAccountExist(username, phone, id)) return -1;

        if (CustomerData.updateCustomer(new Customer(id, phone, name, username, password, point))){
            LogBusiness.saveLog("Cập nhật thông tin khách hàng " + name + " (" + phone + ")");
            return 1;
        }
        return 0;
    }
}