package Entity;

public class Customer extends Person {
    private String user;
    private String password;
    private int point;

    public Customer() {}

    public Customer(int id, String phone, String name, String user, String password) {
        super(id, phone, name);
        this.user = user;
        this.password = password;
        this.point = 0;
    }

    public Customer(String phone, String name, String user, String password ) {
        super(phone, name);
        this.user = user;
        this.password = password;
        this.point = 0;
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

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
