USE PBL3;
CREATE TABLE Staff (
	id_nhan_vien INT AUTO_INCREMENT PRIMARY KEY,
	phone VARCHAR(10) UNIQUE NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    username VARCHAR(20) UNIQUE NOT NULL,
    pass_word VARCHAR(255) NOT NULL,
    position NVARCHAR(30) NOT NULL,
    hire_date DATE NOT NULL,
    PromoCodePromoCodestatus ENUM('Active', 'Inactive') DEFAULT 'Active'
);

INSERT INTO Staff (phone, full_name, username, pass_word, position, hire_date) 
VALUES 
('0905383132', 'Phan Thanh Duy', 'thdyqt', '$2a$10$teJrCEnsxNT49ZpXU7n22O27aCGbVYYe/RG6/XxdWPJbOLZubLIi2', 'Admin', '2026-03-26'),
('0766606051', 'Nguyễn Hoàng Hiếu', 'nhhieu', '$2a$10$teJrCEnsxNT49ZpXU7n22O27aCGbVYYe/RG6/XxdWPJbOLZubLIi2', 'Admin', '2026-03-26'),
('1', 'Huỳnh Hiếu Nghĩa', 'hhnghia', '$2a$10$teJrCEnsxNT49ZpXU7n22O27aCGbVYYe/RG6/XxdWPJbOLZubLIi2', 'Admin', '2026-03-26'),
('2', 'Nguyễn Hữu Trọng', 'nhtrong', '$2a$10$teJrCEnsxNT49ZpXU7n22O27aCGbVYYe/RG6/XxdWPJbOLZubLIi2', 'Admin', '2026-03-26');
# Mật khẩu bị mã hóa nên nhập mật khẩu là 123456

USE PBL3;
DROP TABLE Staff;