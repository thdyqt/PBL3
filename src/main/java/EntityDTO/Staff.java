package EntityDTO;

import java.sql.*;

public class Staff extends Person {
    private String role;
    private Date hireDate;
    private String status;

    public Staff() {}

    public Staff(int id, String phone, String name, String user, String password, String role, Date hireDate, String status) {
        super(id, phone, name, user, password);
        this.role = role;
        this.hireDate = hireDate;
        this.status = status;
    }

    public Staff(int id, String phone, String name, String user, String role, Date hireDate, String status) {
        super(id, phone, name, user);
        this.role = role;
        this.hireDate = hireDate;
        this.status = status;
    }

    public Staff(String phone, String name, String user, String password, String role, Date hireDate, String status) {
        super(phone, name, user, password);
        this.role = role;
        this.hireDate = hireDate;
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
