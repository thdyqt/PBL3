USE PBL3;
CREATE TABLE staff (
	id_nhan_vien INT AUTO_INCREMENT PRIMARY KEY,
	phone VARCHAR(20) UNIQUE NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    username VARCHAR(20) UNIQUE NOT NULL,
    pass_word VARCHAR(20) NOT NULL,
    position NVARCHAR(30) NOT NULL,
    hire_date DATE NOT NULL,
    status ENUM('Active', 'Inactive') DEFAULT 'Active'
);

INSERT INTO staff (phone, full_name, username, pass_word, position, hire_date) 
VALUES 
('0905383132', 'Phan Thanh Duy', 'thdyqt', '123456', 'Admin', '2026-03-26'),
('0', 'Nguyễn Hoàng Hiếu', 'nhhieu', '123456', 'Admin', '2026-03-26'),
('1', 'Huỳnh Hiếu Nghĩa', 'hhnghia', '123456', 'Admin', '2026-03-26'),
('2', 'Nguyễn Hữu Trọng', 'nhtrong', '123456', 'Admin', '2026-03-26');

USE PBL3;
DROP TABLE staff;