USE PBL3;
CREATE TABLE OrderDetail(
                            id_Order INT NOT NULL,
                            id_Product INT NOT NULL,
                            quantity INT NOT NULL,
                            price INT NOT NULL,

                            PRIMARY KEY (id_Order,id_Product),

                            -- foreign keys
                            FOREIGN KEY (id_Order) REFERENCES `Orders`(id_Order),
                            FOREIGN KEY (id_Product) REFERENCES `Product`(ProductID)
);
USE PBL3;
DROP TABLE OrderDetail;