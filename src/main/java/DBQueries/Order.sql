USE PBL3;
CREATE TABLE Orders(
                            id_Order INT AUTO_INCREMENT PRIMARY KEY,
                            process_time DATETIME NOT NULL,
                            id_Staff INT NOT NULL,
                            id_Customer INT NOT NULL,
                            status ENUM('Waiting_for_validation', 'Processing', 'Delivering', 'Finished', 'Cancelled') DEFAULT 'Waiting_for_validation',

                            -- foreign keys
                            FOREIGN KEY (id_Staff) REFERENCES `Staff`(id_nhan_vien),
                            FOREIGN KEY (id_Customer) REFERENCES `Customer`(id_khach_hang)

);
USE PBL3;
INSERT  INTO Orders (id_Order,process_time,id_Staff,id_Customer,status,type,payment,subtotal,discount_amount,final_total)
    VALUE(4,NOW(),4,4,'Processing','Online','Cash',90000,10000,80000);
DROP TABLE Orders;