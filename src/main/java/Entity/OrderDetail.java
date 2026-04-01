package Entity;

//ok this might sounds counterintuitive
//but OrderDetail is like, how many of a product is there
//thus the Order contain many OrderDetail
public class OrderDetail {
    //attributes
    private int orderId;
    private Product product;
    private int quantity;
    private int price;
    private int totalPrice;
    //price is the price of a singular unit of that product, totalPrice is the value of it * the quantity of it
    //also you dont arbitrarily set the value of totalPrice, it is calculated via price and quantity

    //constructors
    public OrderDetail(){};
    public OrderDetail(int orderId, Product product, int quantity, int price){
        this.orderId = orderId;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = this.price * this.quantity;
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

    public int getPrice() {return price;}
    public void setPrice(int price) {
        this.price = price;
        this.totalPrice = this.price * this.quantity;
    }

    public int getTotalPrice(){return totalPrice;}
    public void setTotalPrice(double totalPrice){
        this.totalPrice = this.quantity * this.product.getProductPrice();
    }
}
