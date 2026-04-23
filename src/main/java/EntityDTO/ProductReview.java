package EntityDTO;

import java.sql.Timestamp;

public class ProductReview {
    private int reviewID;
    private int productID;
    private int customerID;
    private String customerName;
    private int rating;
    private String comment;
    private Timestamp reviewDate;

    public ProductReview() {}

    public ProductReview(int reviewID, int productID, int customerID, String customerName, int rating, String comment, Timestamp reviewDate) {
        this.reviewID = reviewID;
        this.productID = productID;
        this.customerID = customerID;
        this.customerName = customerName;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }

    public ProductReview(int productID, int customerID, int rating, String comment) {
        this.productID = productID;
        this.customerID = customerID;
        this.rating = rating;
        this.comment = comment;
    }

    public int getReviewID() { return reviewID; }
    public void setReviewID(int reviewID) { this.reviewID = reviewID; }

    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Timestamp getReviewDate() { return reviewDate; }
    public void setReviewDate(Timestamp reviewDate) { this.reviewDate = reviewDate; }
}