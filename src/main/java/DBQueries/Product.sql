USE PBL3;
DROP TABLE IF EXISTS Product;

CREATE TABLE Product (
    ProductID       INT AUTO_INCREMENT PRIMARY KEY,
    ProductName     VARCHAR(100) NOT NULL,
    CategoryID      INT NOT NULL,
    ProductPrice    INT NOT NULL CHECK (ProductPrice >= 0),
    quantity        INT NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    status          ENUM('Active', 'Inactive') DEFAULT 'Active',
    description     TEXT,             
    ingredients     TEXT,            
    rating          FLOAT DEFAULT 5.0,
    image           VARCHAR(255),

    FOREIGN KEY (CategoryID) REFERENCES Category(category_id)
);

-- THÊM DỮ LIỆU THỰC ĐƠN ĐA DẠNG KÈM THEO FILE ẢNH
INSERT INTO Product (ProductName, CategoryID, ProductPrice, quantity, description, ingredients, image)
VALUES
    -- Danh mục 1: BÁNH MÌ TRUYỀN THỐNG
    (N'Bánh mì thịt nướng', 1, 20000, 50, 
     N'Bánh mì nóng giòn kẹp thịt heo nướng than hoa thơm lừng, rưới nước sốt đậm đà đặc trưng của tiệm.', 
     N'Bánh mì, thịt heo nướng, pate gan, dưa leo, ngò rí, tương ớt', 'banh_mi_thit.jpg'),
     
    (N'Bánh mì chay', 1, 15000, 30, 
     N'Lựa chọn thanh đạm nhưng vẫn đầy đủ dinh dưỡng, vị đậm đà không kém bánh mì mặn.', 
     N'Bánh mì, chả lụa chay, bì chay, đậu khuôn, nước tương, rau sống', 'banh_mi_chay.jpg'),
     
    (N'Bánh mì trứng', 1, 18000, 20, 
     N'Bánh mì nóng giòn kẹp trứng ốp la lòng đào béo ngậy, rưới nước sốt đậm đà đặc trưng của tiệm.', 
     N'Bánh mì, trứng ốp la, pate gan, dưa leo, ngò rí, tương ớt', 'banh_mi_trung.jpg'),

    -- Danh mục 2: ĐỒ ĂN NHANH / FAST FOOD
    (N'Hamburger bò phô mai', 2, 40000, 25, 
     N'Burger chuẩn vị Mỹ với lớp thịt bò mọng nước và phô mai tan chảy béo ngậy.', 
     N'Vỏ bánh burger mềm, thịt bò băm nướng, phô mai Cheddar, cà chua, xà lách', 'hamburger_bo.jpg'),
     
    (N'Pizza Hải Sản đút lò (Size M)', 2, 85000, 10, 
     N'Pizza đế mỏng giòn tan, phủ đầy hải sản tươi ngon và phô mai kéo sợi cực hấp dẫn.', 
     N'Bột mì, tôm, mực, sốt cà chua, ớt chuông, phô mai Mozzarella', 'pizza_haisan.jpg'),

    -- Danh mục 3: BÁNH NGỌT / PASTRY
    (N'Croissant Bơ tỏi phô mai', 3, 25000, 40, 
     N'Bánh sừng trâu nướng ngập ngụa bơ tỏi thơm nức mũi, lớp vỏ ngoài giòn rụm.', 
     N'Bột mì ngàn lớp, bơ lạt, tỏi băm, ngò tây, phô mai kéo sợi', 'croissant_botoi.jpg'),
     
    (N'Bánh Tiramisu Ý', 3, 35000, 15, 
     N'Bánh ngọt tráng miệng trứ danh của Ý, mang hương vị cà phê và cacao vô cùng quyến rũ.', 
     N'Phô mai Mascarpone, bột cacao nguyên chất, cà phê espresso, bánh sâm panh', 'tiramisu.jpg'),
     
    (N'Mousse Chanh Dây', 3, 30000, 0, 
     N'Bánh Mousse chua ngọt thanh mát, tan ngay trong miệng. Giải ngấy cực kỳ hiệu quả.', 
     N'Kem tươi whipping, cốt chanh dây, đường, gelatin, đế bánh quy bơ', 'mousse_chanhday.jpg'),

    -- Danh mục 4: NƯỚC UỐNG / BEVERAGE
    (N'Trà đào cam sả', 4, 25000, 50, 
     N'Thức uống giải khát quốc dân, sự kết hợp hoàn hảo giữa vị chua, ngọt và hương sả thơm lừng.', 
     N'Trà đen, đào ngâm sệt, cam vàng tươi, sả cây, đá viên', 'tra_dao_cam_sa.jpg'),
     
    (N'Cà phê sữa đá truyền thống', 4, 20000, 40, 
     N'Cà phê pha phin đậm đặc, bừng tỉnh sự tập trung cho một ngày làm việc.', 
     N'Cà phê Robusta nguyên chất, sữa đặc có đường, đá', 'cafe_sua_da.jpg'),
     
    (N'Trà sữa Trân châu đường đen', 4, 30000, 20, 
     N'Sữa tươi thanh trùng béo ngậy kết hợp cùng trân châu nấu đường đen dẻo dai sần sật.', 
     N'Sữa tươi không đường, hồng trà, trân châu đen, siro đường đen Hàn Quốc', 'tra_sua_tcdd.jpg'),
     
    (N'Matcha đá xay phủ kem', 4, 38000, 15, 
     N'Trà xanh đá xay mát lạnh, đắng nhẹ hậu ngọt, phủ lên trên là lớp kem béo ngậy.', 
     N'Bột trà xanh Matcha Nhật Bản, sữa tươi, kem tươi whipping, đá', 'matcha_daxay.jpg');