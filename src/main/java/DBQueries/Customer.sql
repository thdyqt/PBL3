USE PBL3;
CREATE TABLE Customer (
	id_khach_hang INT AUTO_INCREMENT PRIMARY KEY,
	phone VARCHAR(10) UNIQUE NOT NULL,
    full_name NVARCHAR(45) NOT NULL,
    username VARCHAR(45) UNIQUE NOT NULL,
    pass_word VARCHAR(45) NOT NULL,
    point int NOT NULL,
    status ENUM('Active', 'Inactive') DEFAULT 'Active'
);

INSERT INTO Customer (phone, full_name, username, pass_word, point)
VALUES
    ('0905383132', 'Phan Thanh Duy', 'thdyqt', '123456', 100 ),
    ('0766606051', 'Nguyễn Hoàng Hiếu', 'nhhieu', '123456', 200 ),
    ('1', 'Huỳnh Hiếu Nghĩa', 'hhnghia', '123456', 300 ),
    ('2', 'Nguyễn Hữu Trọng', 'nhtrong', '123456', 400 );

USE PBL3;
DROP TABLE Customer;