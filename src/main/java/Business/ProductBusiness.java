package Business;

import Data.ProductData;
import Entity.Product;

public class ProductBusiness {

    // ===== THÊM SẢN PHẨM =====
    public static String addProduct(Product product) {
        if (ProductData.isProductExist(product.getProductName())) {
            return "Sản phẩm đã tồn tại!";
        } else if (ProductData.addProduct(product)) {
            ProductData.addLog(product.getProductID(),
                    product.getProductName(),
                    "ADD", "Thêm sản phẩm mới");
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
            ProductData.addLog(product.getProductID(),
                    product.getProductName(),
                    "UPDATE", "Cập nhật thông tin sản phẩm");
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
            ProductData.addLog(productID,
                    p.getProductName(),
                    "STOP_BUSINESS", "Ngừng kinh doanh sản phẩm");
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
            ProductData.addLog(productID,
                    p.getProductName(),
                    "RESTART_BUSINESS", "Mở lại kinh doanh sản phẩm");
            return "success";
        } else {
            return "Mở lại kinh doanh thất bại, vui lòng thử lại!";
        }
    }
}