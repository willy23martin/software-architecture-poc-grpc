drop table dishes if exists;
drop table customers if exists;

CREATE TABLE customers (
                      id int(11) NOT NULL AUTO_INCREMENT,
                      name varchar(50) DEFAULT NULL,
                      gender varchar(50) DEFAULT NULL,
                      age int(11) DEFAULT NULL,
                      PRIMARY KEY (id)
);


INSERT INTO customers VALUES (1,'william', 'MALE', 29);


CREATE TABLE dishes (
                        dish_id int(11) NOT NULL AUTO_INCREMENT,
                        customer_id int(11) DEFAULT NULL,
                        no_of_items int(11) DEFAULT NULL,
                        total_amount double DEFAULT NULL,
                        date date DEFAULT NULL,
                        PRIMARY KEY (dish_id),
                        foreign key (customer_id) references customers(id)
);


INSERT INTO dishes VALUES (1,1,3,1250,'2023-09-13'),(2,1,2,780,'2023-10-12');
