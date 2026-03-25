-- Chạy thủ công trên MySQL trước khi start app (ddl-auto=validate)
CREATE DATABASE IF NOT EXISTS WebCafe;
USE WebCafe;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(255),
    full_name VARCHAR(100),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE staff (
    user_id INT PRIMARY KEY,
    gender ENUM('MALE','FEMALE'),
    age INT,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE admins (
    user_id INT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE shifts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    day_of_week ENUM(
        'MONDAY','TUESDAY','WEDNESDAY',
        'THURSDAY','FRIDAY','SATURDAY','SUNDAY'
    ) NOT NULL,
    shift_time ENUM('MORNING','AFTERNOON','EVENING') NOT NULL
);

CREATE TABLE staff_shifts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    staff_id INT,
    shift_id INT,
    FOREIGN KEY (staff_id) REFERENCES staff(user_id),
    FOREIGN KEY (shift_id) REFERENCES shifts(id)
);

CREATE TABLE cafe_tables (
    id INT AUTO_INCREMENT PRIMARY KEY,
    table_number INT UNIQUE NOT NULL,
    status ENUM('EMPTY','OCCUPIED') DEFAULT 'EMPTY'
);

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    image_url VARCHAR(255)
);

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(255),
    quantity INT DEFAULT 0,
    is_available BOOLEAN DEFAULT TRUE,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- status: PENDING → PREPARING → DONE → PAID (PAID = đã thanh toán, kết thúc đơn).
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    table_id INT,
    status ENUM('PENDING','PREPARING','DONE','PAID') DEFAULT 'PENDING' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (table_id) REFERENCES cafe_tables(id)
);

CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    product_id INT,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    method ENUM('CASH','BANKING') NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    paid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

INSERT INTO shifts (day_of_week, shift_time) VALUES
('MONDAY','MORNING'),('MONDAY','AFTERNOON'),('MONDAY','EVENING'),
('TUESDAY','MORNING'),('TUESDAY','AFTERNOON'),('TUESDAY','EVENING'),
('WEDNESDAY','MORNING'),('WEDNESDAY','AFTERNOON'),('WEDNESDAY','EVENING'),
('THURSDAY','MORNING'),('THURSDAY','AFTERNOON'),('THURSDAY','EVENING'),
('FRIDAY','MORNING'),('FRIDAY','AFTERNOON'),('FRIDAY','EVENING'),
('SATURDAY','MORNING'),('SATURDAY','AFTERNOON'),('SATURDAY','EVENING'),
('SUNDAY','MORNING'),('SUNDAY','AFTERNOON'),('SUNDAY','EVENING');
