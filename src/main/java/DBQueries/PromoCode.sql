USE PBL3;
CREATE TABLE PromoCode (
    Code VARCHAR(50) PRIMARY KEY,       -- Tên mã (VD: GIAM10K, TET2026)
    Description NVARCHAR(255),          -- Mô tả (VD: Giảm 10k cho đơn từ...)
    DiscountValue INT NOT NULL,       -- Giá trị giảm (VD: 10 hoặc 15)
    DiscountType VARCHAR(20) NOT NULL,  -- Kiểu giảm: 'PERCENT' (Phần trăm) hoặc 'AMOUNT' (Tiền mặt)
    MinOrderValue INT DEFAULT 0,      -- Đơn hàng tối thiểu để áp dụng
    ValidFrom DATE,                     -- Ngày bắt đầu
    ValidTo DATE,                       -- Ngày kết thúc
    status ENUM('Active', 'Inactive') DEFAULT 'Active'
);

INSERT INTO PromoCode (Code, Description, DiscountValue, DiscountType, MinOrderValue, ValidFrom, ValidTo)
VALUES 
('CHAO2026', N'Giảm 10% mừng năm mới', 10, 'PERCENT', 0, '2026-01-01', '2026-12-31'),
('GIAM20K', N'Giảm 20.000đ cho đơn từ 100k', 20000, 'AMOUNT', 100000, '2026-01-01', '2026-12-31'),
('FREESHIP', N'Giảm 10.000đ', 10000, 'AMOUNT', 0, '2026-04-01', '2026-05-01');

USE PBL3;
DROP TABLE PromoCode;