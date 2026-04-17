USE PBL3;
CREATE TABLE Category(
	category_id   INT AUTO_INCREMENT PRIMARY KEY,
	category_name NVARCHAR(100) NOT NULL,
	status ENUM('Active', 'Inactive') DEFAULT 'Active'
);
    
INSERT INTO Category(CATEGORY_ID, CATEGORY_NAME) VALUES
(1, 'Bánh mì truyền thống'),
(2,'Đồ ăn nhanh'),
(3,'Bánh ngọt'),
(4,'Nước uống');
    
USE PBL3;
DROP TABLE Category;
