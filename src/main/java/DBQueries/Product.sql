USE PBL3;



CREATE TABLE Product (
                         ProductID    INT AUTO_INCREMENT PRIMARY KEY,
                         ProductName  NVARCHAR(100) NOT NULL,
                         CategoryID   INT NOT NULL,
                         ProductPrice INT NOT NULL CHECK (ProductPrice >= 0),
                         quantity     INT NOT NULL DEFAULT 0 CHECK (quantity >= 0),
                         isAvailable BOOLEAN GENERATED ALWAYS AS (quantity > 0) STORED,
                         status ENUM('Active', 'Inactive') DEFAULT 'Active',
                         FOREIGN KEY (CategoryID) REFERENCES Category(category_id)
);

INSERT INTO Product (ProductName, CategoryID, ProductPrice, quantity)
VALUES
    (N'Bánh mì thịt',      1, 20000, 50),
    (N'Bánh mì chay',      2, 15000, 30),
    (N'Bánh mì trứng',     1, 18000, 40),
    (N'Hamburger bò',      2, 35000, 20),
    (N'Pizza phô mai',     3, 45000,  0),
    (N'Nước cam ép',       4, 15000, 25);
ALTER TABLE Product
    ADD COLUMN image VARCHAR(255) DEFAULT NULL;
USE PBL3;
DROP  TABLE Product;