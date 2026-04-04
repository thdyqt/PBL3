package EntityDTO;

public class Customer extends Person {
    private int point;
    public enum rank {
        Bronze, Silver, Gold, Platinum, Diamond;
    }
    private rank Customer_rank;

    public Customer() {}

    // Constructor dùng khi thêm mới khách hàng (chưa có điểm)
    public Customer(String phone, String name, String user, String password) {
        super(phone, name, user, password);
        this.setPoint(0); // Gọi setter để tự động set rank Bronze
    }

    // Constructor dùng khi lấy dữ liệu từ DB lên (đã có id và point)
    public Customer(int id, String phone, String name, String user, String password, int point) {
        super(id, phone, name, user, password);
        this.setPoint(point); // Gọi setter để gán điểm và tự động tính toán lại Rank
    }

    public rank getCustomer_rank() {
        return Customer_rank;
    }

    public void setCustomer_rank(rank customer_rank) {
        this.Customer_rank = customer_rank;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
        UpdatedRank(this);
    }

    public void UpdatedRank(Customer c) {
        if (c.getPoint() < 100) {
            c.setCustomer_rank(rank.Bronze);
        } else if (c.getPoint() < 200) {
            c.setCustomer_rank(rank.Silver);
        } else if (c.getPoint() < 500) {
            c.setCustomer_rank(rank.Gold);
        } else if (c.getPoint() < 1000) {
            c.setCustomer_rank(rank.Platinum);
        } else {
            c.setCustomer_rank(rank.Diamond);
        }
    }
}