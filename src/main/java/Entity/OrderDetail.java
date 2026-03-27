package Entity;

public class OrderDetail {
    //attributes
    private int orderId;
    private Product product;
    private int quantity;
    private double price;

    //constructors
    public OrderDetail(){};
    public OrderDetail(int orderId, Product product, int quantity, double price){
        this.orderId = orderId;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
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

    public double getPrice(){return price;}
    public void setPrice(double price){
        this.price = price;
    }
}
