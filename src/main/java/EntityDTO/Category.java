package EntityDTO;

public class Category {
    // ===== ATTRIBUTES =====
    private int CategoryID;
    private String categoryName;
    private String status;


    // ===== CONSTRUCTORS =====

    // Constructor đầy đủ (load từ database)
    public Category(int CategoryID, String categoryName, String status) {
        this.CategoryID   = CategoryID;
        this.categoryName = categoryName;
        this.status = status;

    }

    // Constructor không có ID (tạo mới, ID do DB tự sinh)
    public Category(String categoryName,String status) {
        this(0, categoryName,status);
    }

    // ===== GETTERS & SETTERS =====

    public int getCategoryID()          { return CategoryID; }
    public void setCategoryID(int id)   { this.CategoryID = id; }

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
