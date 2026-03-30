USE PBL3;
    CREATE TABLE Category(
        category_id   INT AUTO_INCREMENT PRIMARY KEY,
        category_name NVARCHAR(100) NOT NULL
    );
    INSERT INTO Category(CATEGORY_ID, CATEGORY_NAME) VALUES
    (1, 'Bánh mỳ'  ),
    (2,'Hamberger'),
    (3,'Pizza'),
    (4,'Đồ uống');
USE PBL3;
DROP TABLE Category;
ALTER TABLE Category
    ADD COLUMN status ENUM('Active', 'Inactive') DEFAULT 'Active';