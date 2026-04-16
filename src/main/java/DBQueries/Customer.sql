USE PBL3;
CREATE TABLE Customer (
	id_khach_hang INT AUTO_INCREMENT PRIMARY KEY,
	phone VARCHAR(10) UNIQUE NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    username VARCHAR(20),
    pass_word VARCHAR(255) NOT NULL,
    point int NOT NULL,
    status ENUM('Active', 'Inactive') DEFAULT 'Active'
);

INSERT INTO Customer (phone, full_name, username, pass_word, point)
VALUES
    ('0905383132', 'Phan Thanh Duy', 'thdyqt', '$2a$10$teJrCEnsxNT49ZpXU7n22O27aCGbVYYe/RG6/XxdWPJbOLZubLIi2', 100 ),
    ('0766606051', 'Nguyễn Hoàng Hiếu', 'nhhieu', '$2a$10$teJrCEnsxNT49ZpXU7n22O27aCGbVYYe/RG6/XxdWPJbOLZubLIi2', 200 ),
    ('1', 'Huỳnh Hiếu Nghĩa', 'hhnghia', '$2a$10$teJrCEnsxNT49ZpXU7n22O27aCGbVYYe/RG6/XxdWPJbOLZubLIi2', 300 ),
    ('2', 'Nguyễn Hữu Trọng', 'nhtrong', '$2a$10$teJrCEnsxNT49ZpXU7n22O27aCGbVYYe/RG6/XxdWPJbOLZubLIi2', 400 );
# Mật khẩu bị mã hóa nên nhập mật khẩu là 123456

USE PBL3;
DROP TABLE Customer;