package EntityDTO;

public class Customer extends Person {
    private String address;
    private int point;
    public enum rank {
        Bronze, Silver, Gold, Diamond, Emerald
    }
    private rank Customer_rank;


    public Customer() {}

    public Customer(String phone, String name, String user, String password, String address, int point) {
        super(phone, name, user, password);
        setAddress(address);
        setPoint(point);
    }

    public Customer(int id, String phone, String name, String user, String address, int point) {
        super(id, phone, name, user);
        setAddress(address);
        setPoint(point);
    }

    public Customer(int id, String phone, String name, String user, String password, String address, int point) {
        super(id, phone, name, user, password);
        setAddress(address);
        setPoint(point);

    }

    public rank getCustomer_rank() {
        return Customer_rank;
    }

    public void setCustomer_rank(rank customer_rank) {
        this.Customer_rank = customer_rank;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
        UpdatedRank(this);
    }

    private void UpdatedRank(Customer c) {
        if (c.getPoint() < 100) {
            c.setCustomer_rank(rank.Bronze);
        } else if (c.getPoint() < 500) {
            c.setCustomer_rank(rank.Silver);
        } else if (c.getPoint() < 1000) {
            c.setCustomer_rank(rank.Gold);
        } else if (c.getPoint() < 2000) {
            c.setCustomer_rank(rank.Diamond);
        } else {
            c.setCustomer_rank(rank.Emerald);
        }
    }
}