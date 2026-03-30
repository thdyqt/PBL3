package Entity;

//ok this might sounds counterintuitive
//but OrderDetail is like, fucking how many of a product is there
//thus the Order contain many OrderDetail
public class OrderDetail {
    //attributes
    private int orderId;
    private Product product;
    private int quantity;
    private int totalPrice;

    //constructors
    public OrderDetail(){};
    public OrderDetail(int orderId, Product product, int quantity, int totalPrice){
        this.orderId = orderId;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    //get-set
    public int getId(){return orderId;}
    public void setId(int orderId){
        this.orderId = orderId;
    }

    public Product getProduct(){return product;}
    public void setProduct(Product product){
        this.product = product;
    }

    public int getQuantity(){return quantity;}
    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public int getTotalPrice(){return totalPrice;}
    public void setTotalPrice(double totalPrice){
        this.totalPrice = this.quantity * this.product.getProductPrice();
    }
}
