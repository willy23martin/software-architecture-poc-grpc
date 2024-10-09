-- Switch to the desired database
USE trainingdb;
GO

-- Drop tables if they exist
IF OBJECT_ID('trainingdb.dbo.orders', 'U') IS NOT NULL
DROP TABLE trainingdb.dbo.orders;

IF OBJECT_ID('trainingdb.dbo.customers', 'U') IS NOT NULL
DROP TABLE trainingdb.dbo.customers;
GO

-- Create customers table
CREATE TABLE trainingdb.dbo.customers (
                                          id INT IDENTITY(1,1) NOT NULL,
                                          name VARCHAR(50) NULL,
                                          gender VARCHAR(50) NULL,
                                          age INT NULL,
                                          PRIMARY KEY (id)
);
GO

-- Insert data into customers table
INSERT INTO trainingdb.dbo.customers (name, gender, age) VALUES ('Luisa', 'MALE', 38);
INSERT INTO trainingdb.dbo.customers (name, gender, age) VALUES ('Victor', 'MALE', 30);
INSERT INTO trainingdb.dbo.customers (name, gender, age) VALUES ('William', 'FEMALE', 35);
GO

-- Create orders table
CREATE TABLE trainingdb.dbo.orders (
                                       product_id INT IDENTITY(1,1) NOT NULL,
                                       customer_id INT NULL,
                                       no_of_items INT NULL,
                                       total_amount FLOAT NULL,
                                       PRIMARY KEY (product_id),
                                       FOREIGN KEY (customer_id) REFERENCES trainingdb.dbo.customers(id)
);
GO

-- Insert data into orders table
INSERT INTO trainingdb.dbo.orders (customer_id, no_of_items, total_amount) VALUES (1, 3, 1250);
INSERT INTO trainingdb.dbo.orders (customer_id, no_of_items, total_amount) VALUES (1, 2, 780);
INSERT INTO trainingdb.dbo.orders (customer_id, no_of_items, total_amount) VALUES (2, 3, 1250);
INSERT INTO trainingdb.dbo.orders (customer_id, no_of_items, total_amount) VALUES (3, 2, 780);
INSERT INTO trainingdb.dbo.orders (customer_id, no_of_items, total_amount) VALUES (2, 3, 1250);
INSERT INTO trainingdb.dbo.orders (customer_id, no_of_items, total_amount) VALUES (3, 2, 780);
INSERT INTO trainingdb.dbo.orders (customer_id, no_of_items, total_amount) VALUES (2, 3, 1250);
INSERT INTO trainingdb.dbo.orders (customer_id, no_of_items, total_amount) VALUES (2, 2, 780);
GO
