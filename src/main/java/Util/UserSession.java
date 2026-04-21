package Util;

import EntityDTO.Staff;

import java.sql.Date;

// LẤY THÔNG TIN NGƯỜI DÙNG ĐĂNG NHẬP HIỆN TẠI
public class UserSession {
    private static UserSession instance;

    private int id;
    private String phone;
    private String name;
    private String username;
    private String password;
    private String position;
    private Date hire_date;
    private String address;
    private int point;
    private boolean isGuest;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setStaff(int id, String phone, String name, String username, String password, String position, Date hire_date) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.username = username;
        this.password = password;
        this.position = position;
        this.hire_date = hire_date;
    }

    public void setCustomer(int id, String phone, String name, String username, String password, String address, int point) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.username = username;
        this.password = password;
        this.address = address;
        this.point = point;
    }

    public void setGuest() {
        clearSession();
        this.id = 0;
        this.name = "Khách vãng lai";
        this.isGuest = true;
    }

    public void clearSession() {
        this.id = -1;
        this.phone = null;
        this.name = null;
        this.username = null;
        this.password = null;
        this.position = null;
        this.hire_date = null;
        this.address = null;
        this.point = 0;
        this.isGuest = false;
    }

    public int getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPosition() {
        return position;
    }

    public Date getHire_date() {
        return hire_date;
    }

    public String getAddress() {
        return address;
    }

    public int getPoint() {
        return point;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public Staff getStaff() {
        return new Staff(id, phone, name, username, password, position, hire_date, "Active");
    }
}