package BusinessBLL;

import DataDAL.CategoryData;
import EntityDTO.Category;

import java.util.List;

public class CategoryBusiness {
    public static List<Category> getAllCategories(){
        return CategoryData.getAll();
    }

    // ===== THÊM DANH MỤC =====
    public static String addCategory(Category category) {
        if (CategoryData.isCategoryExist(category.getCategoryName())) {
            return "Danh mục đã tồn tại!";
        } else if (CategoryData.addCategory(category)) {
            CategoryData.addLog(category.getCategoryID(),
                    category.getCategoryName(),
                    "ADD", "Thêm danh mục mới");
            return "success";
        } else {
            return "Thêm danh mục thất bại, vui lòng thử lại!";
        }
    }

    // ===== SỬA DANH MỤC =====
    public static String updateCategory(Category category) {
        if (CategoryData.getByID(category.getCategoryID()) == null) {
            return "Danh mục không tồn tại!";
        } else if (CategoryData.updateCategory(category)) {
            CategoryData.addLog(category.getCategoryID(),
                    category.getCategoryName(),
                    "UPDATE", "Cập nhật thông tin danh mục");
            return "success";
        } else {
            return "Cập nhật danh mục thất bại, vui lòng thử lại!";
        }
    }

    // ===== NGỪNG KINH DOANH =====
    public static String stopBusiness(int categoryID) {
        Category c = CategoryData.getByID(categoryID);
        if (c == null) {
            return "Danh mục không tồn tại!";
        } else if (CategoryData.isInactive(categoryID)) {
            return "Danh mục đã ngừng kinh doanh trước đó!";
        } else if (CategoryData.stopBusiness(categoryID)) {
            CategoryData.addLog(categoryID,
                    c.getCategoryName(),
                    "STOP_BUSINESS", "Ngừng kinh doanh danh mục");
            return "success";
        } else {
            return "Ngừng kinh doanh thất bại, vui lòng thử lại!";
        }
    }

    // ===== MỞ LẠI KINH DOANH =====
    public static String restartBusiness(int categoryID) {
        Category c = CategoryData.getByID(categoryID);
        if (c == null) {
            return "Danh mục không tồn tại!";
        } else if (!CategoryData.isInactive(categoryID)) {
            return "Danh mục đang được kinh doanh rồi!";
        } else if (CategoryData.restartBusiness(categoryID)) {
            CategoryData.addLog(categoryID,
                    c.getCategoryName(),
                    "RESTART_BUSINESS", "Mở lại kinh doanh danh mục");
            return "success";
        } else {
            return "Mở lại kinh doanh thất bại, vui lòng thử lại!";
        }
    }

    public static int getCategoryIDByName(String categoryName) {
        List<Category> categories = CategoryData.getAll();
        for (Category c : categories) {
            if (c.getCategoryName().equals(categoryName)) {
                return c.getCategoryID();
            }
        }
        return -1;
    }

    public static Category getCategoryByID(int ID) {
        return CategoryData.getByID(ID);
    }
}