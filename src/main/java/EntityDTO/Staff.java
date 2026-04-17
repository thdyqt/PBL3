package EntityDTO;

import java.sql.*;

public class Staff extends Person {
    private String role;
    private Date hire_date;
    private String status;

    public Staff() {}

    public Staff(int id, String phone, String name, String user, String password, String role, Date hire_date, String status) {
        super(id, phone, name, user, password);
        this.role = role;
        this.hire_date = hire_date;
        this.status = status;
    }

    public Staff(int id, String phone, String name, String user, String role, Date hire_date, String status) {
        super(id, phone, name, user);
        this.role = role;
        this.hire_date = hire_date;
        this.status = status;
    }

    public Staff(String phone, String name, String user, String password, String role, Date hire_date, String status) {
        super(phone, name, user, password);
        this.role = role;
        this.hire_date = hire_date;
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getHire_date() {
        return hire_date;
    }

    public void setHire_date(Date hire_date) {
        this.hire_date = hire_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
