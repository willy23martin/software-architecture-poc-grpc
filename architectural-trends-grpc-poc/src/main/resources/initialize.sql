CREATE TABLE customers (
                      id int(11) NOT NULL AUTO_INCREMENT,
                      name varchar(50) DEFAULT NULL,
                      gender varchar(50) DEFAULT NULL,
                      age int(11) DEFAULT NULL,
                      PRIMARY KEY (id)
);


INSERT INTO customers VALUES (1,'Luisa', 'MALE', 38);
INSERT INTO customers VALUES (2,'Victor', 'MALE', 30);
INSERT INTO customers VALUES (3,'William', 'FEMALE', 35);


CREATE TABLE orders (
                        product_id int(11) NOT NULL AUTO_INCREMENT,
                        customer_id int(11) DEFAULT NULL,
                        no_of_items int(11) DEFAULT NULL,
                        total_amount double DEFAULT NULL,
                        PRIMARY KEY (product_id),
                        foreign key (customer_id) references customers(id)
);


INSERT INTO orders VALUES (1,1,3,1250),(2,1,2,780);
INSERT INTO orders VALUES (3,2,3,1250),(4,3,2,780);
INSERT INTO orders VALUES (5,2,3,1250),(6,3,2,780);
INSERT INTO orders VALUES (7,2,3,1250),(8,2,2,780);
