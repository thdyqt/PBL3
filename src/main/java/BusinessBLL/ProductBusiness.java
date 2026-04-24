package BusinessBLL;

import DataDAL.ProductData;
import EntityDTO.Product;

import java.util.List;

import static BusinessBLL.LogBusiness.saveLog;

public class ProductBusiness {
    public static List<Product> getAllProducts(){
        return ProductData.getAllProduct();
    }

    public static List<Product> getTopBestSellers(int quantity) {
        return ProductData.getTopBestSellers(quantity);
    }

    public static List<Product> getNewestProducts(int quantity) {
        return ProductData.getNewestProducts(quantity);
    }

    public static double getProductRating(int productID) {
        for (Product p : getAllProducts()) {
            if (p.getProductID() == productID) {
                return p.getRating();
            }
        }
        return 0.0;
    }

    public static String addProduct(Product product) {
        if (ProductData.isProductExist(product.getProductName())) {
            return "Sản phẩm đã tồn tại!";
        } else if (ProductData.addProduct(product)) {
            saveLog("Thêm sản phẩm " + product.getProductName());
            return "success";
        } else {
            return "Thêm sản phẩm thất bại, vui lòng thử lại!";
        }
    }

    // ===== SỬA SẢN PHẨM =====
    public static String updateProduct(Product product) {
        if (ProductData.getByID(product.getProductID()) == null) {
            return "Sản phẩm không tồn tại!";
        } else if (ProductData.updateProduct(product)) {
            saveLog("Chỉnh sửa sản phẩm " + product.getProductName());
            return "success";
        } else {
            return "Cập nhật sản phẩm thất bại, vui lòng thử lại!";
        }
    }

    // ===== NGỪNG KINH DOANH =====
    public static String stopBusiness(int productID) {
        Product p = ProductData.getByID(productID);
        if (p == null) {
            return "Sản phẩm không tồn tại!";
        } else if (ProductData.isInactive(productID)) {
            return "Sản phẩm đã ngừng kinh doanh trước đó!";
        } else if (ProductData.stopBusiness(productID)) {
            saveLog("Ngừng kinh doanh sản phẩm " + p.getProductName());
            return "success";
        } else {
            return "Ngừng kinh doanh thất bại, vui lòng thử lại!";
        }
    }

    // ===== MỞ LẠI KINH DOANH =====
    public static String restartBusiness(int productID) {
        Product p = ProductData.getByID(productID);
        if (p == null) {
            return "Sản phẩm không tồn tại!";
        } else if (!ProductData.isInactive(productID)) {
            return "Sản phẩm đang được kinh doanh rồi!";
        } else if (ProductData.restartBusiness(productID)) {
            saveLog("Mở lại kinh doanh sản phẩm " + p.getProductName());
            return "success";
        } else {
            return "Mở lại kinh doanh thất bại, vui lòng thử lại!";
        }
    }
}