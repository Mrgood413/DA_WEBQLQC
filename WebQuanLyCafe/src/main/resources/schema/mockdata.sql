-- Mock data cho WebCafe — chạy sau WebCafe.sql (đã có bảng).
-- Thứ tự: danh mục → sản phẩm (FK category_id); bàn (cafe_tables) độc lập.
-- Chạy một lần trên DB trống; nạp lại: xóa theo thứ tự phụ thuộc FK (orders/order_items trước nếu có).

USE WebCafe;

-- ========== Danh mục ==========
INSERT INTO categories (name) VALUES
('Cà phê'),
('Trà'),
('Bánh ngọt'),
('Đồ uống khác');

-- ========== Sản phẩm ==========
-- category_id: 1=Cà phê, 2=Trà, 3=Bánh ngọt, 4=Đồ uống khác
INSERT INTO products (name, description, price, image_url, quantity, is_available, category_id) VALUES
('Cà phê sữa đá', 'Cà phê phin, sữa đặc, đá', 29000.00, NULL, 200, TRUE, 1),
('Americano', 'Espresso pha loãng nóng', 35000.00, NULL, 150, TRUE, 1),
('Latte', 'Espresso và sữa tươi, lớp sữa mịn', 45000.00, NULL, 120, TRUE, 1),
('Bạc xỉu', 'Cà phê sữa ít đá, vị ngọt vừa', 32000.00, NULL, 180, TRUE, 1),
('Trà đào cam sả', 'Trà đen, đào, cam, sả', 39000.00, NULL, 100, TRUE, 2),
('Trà sữa ô long', 'Ô long, sữa, trân châu', 42000.00, NULL, 90, TRUE, 2),
('Trà matcha latte', 'Matcha Nhật, sữa', 48000.00, NULL, 80, TRUE, 2),
('Croissant bơ', 'Bánh sừng bò Pháp', 35000.00, NULL, 50, TRUE, 3),
('Tiramisu', 'Bánh tiramisu cổ điển', 55000.00, NULL, 40, TRUE, 3),
('Cold brew', 'Cà phê ủ lạnh 12h', 40000.00, NULL, 70, TRUE, 4),
('Nước cam ép', 'Cam tươi ép', 35000.00, NULL, 60, TRUE, 4);

-- ========== Bàn (cafe_tables) ==========
INSERT INTO cafe_tables (table_number, status) VALUES
(1, 'EMPTY'),
(2, 'EMPTY'),
(3, 'EMPTY'),
(4, 'EMPTY'),
(5, 'EMPTY'),
(6, 'EMPTY'),
(7, 'EMPTY'),
(8, 'EMPTY'),
(9, 'EMPTY'),
(10, 'EMPTY');
