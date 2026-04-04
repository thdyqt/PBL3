package EntityDTO;

public class Product {
    private int ProductID;
    public String ProductName;
    private int CategoryID;
    private int ProductPrice;
    private int quantity;
    private boolean isAvailable;

    //Constructor
    public Product() {}

    public Product(int id,String name,int cateid,int price,int quantity,boolean isA){
             this.CategoryID = id;
             this.ProductName = name;
             this.CategoryID = cateid;
             this.ProductPrice = price;
             this.quantity = quantity;
             this.isAvailable = isA;
         }

    public Product(String name,int cateid,int price,int quantity,boolean isA){
         this.ProductName = name;
         this.CategoryID = cateid;
         this.ProductPrice = price;
         this.quantity = quantity;
         this.isAvailable = isA;
    }
    //get;set;
    public int getProductID(){return this.ProductID;}
    public void setProductID(int productID) {
        this.ProductID = productID;
    }

    public String getProductName() {
        return ProductName;
    }
    public void setProductName(String productName) {
        ProductName = productName;
    }

    public int getCategoryID() {
        return CategoryID;
    }
    public void setCategoryID(int categoryID) {
        CategoryID = categoryID;
    }

    public int getProductPrice() {
        return ProductPrice;
    }
    public void setProductPrice(int productPrice) {
        ProductPrice = productPrice;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
