package Entity;

public class Category {
    // ===== ATTRIBUTES =====
    private int CategoryID;
    private String categoryName;


    // ===== CONSTRUCTORS =====

    // Constructor đầy đủ (load từ database)
    public Category(int CategoryID, String categoryName) {
        this.CategoryID   = CategoryID;
        this.categoryName = categoryName;

    }

    // Constructor không có ID (tạo mới, ID do DB tự sinh)
    public Category(String categoryName) {
        this(0, categoryName);
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



}
