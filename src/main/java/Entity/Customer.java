package Entity;

public class Customer extends Person {
    private int point;

    public Customer() {}

    public Customer(int id, String phone, String name, String user, String password) {
        super(id, phone, name, user, password);
        this.point = 0;
    }

    public Customer(String phone, String name, String user, String password ) {
        super(phone, name, user, password);
        this.point = 0;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
