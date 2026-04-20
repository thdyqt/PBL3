package EntityDTO;

public class Customer extends Person {
    private String address;
    private int point;
    public enum Rank {
        Bronze, Silver, Gold, Diamond, Emerald
    }
    private Rank CustomerRank;


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

    public Rank getCustomerRank() {
        return CustomerRank;
    }

    public void setCustomerRank(Rank customer_rank) {
        this.CustomerRank = customer_rank;
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
            c.setCustomerRank(Rank.Bronze);
        } else if (c.getPoint() < 500) {
            c.setCustomerRank(Rank.Silver);
        } else if (c.getPoint() < 1000) {
            c.setCustomerRank(Rank.Gold);
        } else if (c.getPoint() < 2000) {
            c.setCustomerRank(Rank.Diamond);
        } else {
            c.setCustomerRank(Rank.Emerald);
        }
    }
}