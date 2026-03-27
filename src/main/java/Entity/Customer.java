package Entity;

public class Customer extends Person {
    private int point;

    public Customer() {}

    public Customer(int id, String phone, String name, String user, String password, int point) {
        super(id, phone, name, user, password);
        this.point = point;
    }

    public Customer(String phone, String name, String user, String password, int point ) {
        super(phone, name, user, password);
        this.point = point;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
