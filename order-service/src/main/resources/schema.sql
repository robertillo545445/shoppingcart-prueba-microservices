-- src/main/resources/schema.sql

-- Tabla para la entidad Customer
CREATE TABLE IF NOT EXISTS customers (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY, -- CORREGIDO: Cambiado de IDENTITY a AUTO_INCREMENT
                                         name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL
    );

-- Tabla para la entidad Order
CREATE TABLE IF NOT EXISTS orders ( -- Se recomienda "orders" en plural para seguir la convención de Spring Data
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY, -- CORREGIDO: Cambiado de IDENTITY a AUTO_INCREMENT
                                      customer_id BIGINT NOT NULL,
                                      total DECIMAL(10, 2) NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
    );

-- Tabla para la entidad OrderDetail
CREATE TABLE IF NOT EXISTS order_details ( -- Se recomienda "order_details" en plural
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- CORREGIDO: Cambiado de IDENTITY a AUTO_INCREMENT
                                             order_id BIGINT NOT NULL,
                                             product_id VARCHAR(255) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id)
    );