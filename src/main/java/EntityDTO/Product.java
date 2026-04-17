package EntityDTO;

public class Product {
    private int productID;
    private String productName;
    private int categoryID;
    private int productPrice;
    private int quantity;
    private String status;
    private String description;
    private String ingredients;
    private double rating;
    private String image;

    // Constructor rỗng
    public Product() {}

    // Constructor đầy đủ dùng khi lấy dữ liệu từ Database lên
    public Product(int productID, String productName, int categoryID, int productPrice,
                   int quantity, String status, String description, String ingredients,
                   double rating, String image) {
        this.productID = productID;
        this.productName = productName;
        this.categoryID = categoryID;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.status = status;
        this.description = description;
        this.ingredients = ingredients;
        this.rating = rating;
        this.image = image;
    }

    // Constructor không có ID dùng khi thêm sản phẩm mới (ID tự tăng)
    public Product(String productName, int categoryID, int productPrice, int quantity,
                   String status, String description, String ingredients, double rating, String image) {
        this.productName = productName;
        this.categoryID = categoryID;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.status = status;
        this.description = description;
        this.ingredients = ingredients;
        this.rating = rating;
        this.image = image;
    }

    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getCategoryID() { return categoryID; }
    public void setCategoryID(int categoryID) { this.categoryID = categoryID; }

    public int getProductPrice() { return productPrice; }
    public void setProductPrice(int productPrice) { this.productPrice = productPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isAvailable() {
        return this.quantity > 0;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}