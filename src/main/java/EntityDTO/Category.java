package EntityDTO;

public class Category {
    // ===== ATTRIBUTES =====
    private int categoryID;
    private String categoryName;
    private String status;


    // ===== CONSTRUCTORS =====

    // Constructor đầy đủ (load từ database)
    public Category(int CategoryID, String categoryName, String status) {
        this.categoryID = CategoryID;
        this.categoryName = categoryName;
        this.status = status;

    }

    // Constructor không có ID (tạo mới, ID do DB tự sinh)
    public Category(String categoryName) {
        this(0, categoryName,"Active");
    }

    // ===== GETTERS & SETTERS =====

    public int getCategoryID()          { return categoryID; }
    public void setCategoryID(int id)   { this.categoryID = id; }
    public String getStatus(){return status;}

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategoryName()     { return categoryName; }
    public void setCategoryName(String categoryName) {
        if (categoryName == null || categoryName.isBlank())
            throw new IllegalArgumentException("Tên danh mục không được để trống!");
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return this.categoryName;
    }
}
