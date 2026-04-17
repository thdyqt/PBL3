USE PBL3;

CREATE TABLE ProductReview (
    ReviewID        INT AUTO_INCREMENT PRIMARY KEY,
    ProductID       INT NOT NULL,          
    CustomerID      INT NOT NULL,        
    RatingValue     INT NOT NULL CHECK (RatingValue >= 1 AND RatingValue <= 5),
    Comment         TEXT,                  
    ReviewDate      DATETIME DEFAULT CURRENT_TIMESTAMP, 
    
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID),
    FOREIGN KEY (CustomerID) REFERENCES Customer(id_khach_hang),
    
    -- 1 Khách hàng chỉ được đánh giá 1 Sản phẩm đúng 1 lần duy nhất
    UNIQUE KEY unique_product_customer (ProductID, CustomerID)
);

INSERT INTO ProductReview (ProductID, CustomerID, RatingValue, Comment)
VALUES 
    (1, 1, 5, N'Bánh mì rất giòn, thịt nướng thơm lừng và nước sốt đậm đà. Sẽ ủng hộ quán dài dài!'),
    (1, 2, 4, N'Ngon nhưng hôm nay hơi ít pate một chút.'),
    (8, 1, 5, N'Bánh chua ngọt thanh mát, không bị ngấy, rất hợp ăn tráng miệng nha.'),
    (11, 2, 5, N'Trân châu dẻo, sữa tươi béo ngậy. Tuyệt vời!');