USE PBL3;
CREATE TABLE Cart (
    id_Customer INT,
    id_Product INT,
    quantity INT,
    PRIMARY KEY (id_Customer, id_Product)
);
