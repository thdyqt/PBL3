package BusinessBLL;

import DataDAL.CustomerData;
import EntityDTO.Customer;
import Util.UserSession;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class CustomerBusiness {
    public static List<Customer> getAllCustomers() {
        return CustomerData.getAllCustomers();
    }

    public static String login(String username, String password) {
        try {
            Customer dbCustomer = CustomerData.getCustomerByUsernameOrPhone(username);

            if (dbCustomer == null) {
                return "NOT FOUND";
            }

            if (BCrypt.checkpw(password, dbCustomer.getPassword())) {
                UserSession.getInstance().setCustomer(
                        dbCustomer.getId(),
                        dbCustomer.getPhone(),
                        dbCustomer.getName(),
                        dbCustomer.getUser(),
                        password,
                        dbCustomer.getPoint()
                );
                return "SUCCESS";
            } else {
                return "WRONG PASSWORD";
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi BCrypt: Mật khẩu dưới DB chưa được mã hóa đúng chuẩn!");
            return "SERVER ERROR";
        } catch (Exception e) {
            e.printStackTrace();
            return "SERVER ERROR";
        }
    }

    public static int register(String phone, String name, String username, String password) {
        if (CustomerData.isAccountExist(username, phone, -1)) return -1;

        if (CustomerData.addCustomer(new Customer(phone, name, username, password, 0))){
            LogBusiness.saveLog("Thêm khách hàng " + name + " (" + phone + ") vào hệ thống");
            return 1;
        }
        return 0;
    }

    public static Customer findCustomer(String text){
        return CustomerData.getCustomerByUsernameOrPhone(text);
    }

    public static int updateCustomer(int id, String phone, String name, String username, String password, int point) {
        if (CustomerData.isAccountExist(username, phone, id)) return -1;

        if (CustomerData.updateCustomer(new Customer(id, phone, name, username, password, point))){
            LogBusiness.saveLog("Cập nhật thông tin khách hàng " + name + " (" + phone + ")");
            return 1;
        }
        return 0;
    }

    public static int getDiscountPercent (Customer c) {
        if (c == null) return 0;

        Customer.rank rank = c.getCustomer_rank();
        return switch (rank) {
            case Silver -> 1;
            case Gold -> 2;
            case Diamond -> 5;
            case Emerald -> 8;
            default -> 0;
        };
    }
}