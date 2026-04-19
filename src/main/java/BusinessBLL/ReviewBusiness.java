package BusinessBLL;

import DataDAL.ReviewData;
import EntityDTO.ProductReview;
import java.util.List;

public class ReviewBusiness {
    public static boolean canUserReview(int customerID, int productID) {
        boolean purchased = ReviewData.hasPurchased(customerID, productID);
        boolean reviewed = ReviewData.hasReviewed(customerID, productID);

        return purchased && !reviewed;
    }

    public static String addReview(ProductReview review) {
        if (review.getRating() < 1 || review.getRating() > 5) {
            return "Số sao không hợp lệ!";
        }

        if (ReviewData.addReview(review)) {
            return "success";
        }
        return "Lỗi kết nối cơ sở dữ liệu!";
    }

    public static List<ProductReview> getReviewsOfProduct(int productID) {
        return ReviewData.getReviewsByProduct(productID);
    }
}