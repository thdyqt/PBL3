package Entity;
import java.time.*;

public class Staff extends Person{
    private String user;
    private String password;
    private String role;
    private LocalDateTime hire_date;

    public Staff() {}
    public Staff(String phone, String name, String user, String password, String role, LocalDateTime hire_date) {
        super(phone, name);
        this.user = user;
        this.password = password;
        this.role = role;
        this.hire_date = hire_date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getHire_date() {
        return hire_date;
    }

    public void setHire_date(LocalDateTime hire_date) {
        this.hire_date = hire_date;
    }
}
