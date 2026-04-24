USE PBL3;
CREATE TABLE DeliveryInfo (
    id_Order INT PRIMARY KEY,
    receiver_name NVARCHAR(255) NOT NULL, 
    receiver_phone VARCHAR(20) NOT NULL,  
    delivery_address NVARCHAR(255) NOT NULL, 
    FOREIGN KEY (id_Order) REFERENCES Orders(id_Order) ON DELETE CASCADE
);