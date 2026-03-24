-- Mock data cho WebCafe — chạy sau WebCafe.sql (đã có bảng).
-- Thứ tự: danh mục → sản phẩm (FK category_id); bàn (cafe_tables) độc lập.
-- Chạy một lần trên DB trống; nạp lại: xóa theo thứ tự phụ thuộc FK (orders/order_items trước nếu có).

USE WebCafe;

-- ========== Tài khoản hệ thống ==========
-- Mật khẩu mặc định cho các tài khoản bên dưới: 123456
INSERT INTO users (username, password, full_name, phone) VALUES
('staff01', '$2b$10$ZcGXIBExA3Ssv88VsoosOerIi9JVzzO/rWTmPjk20NxdjQ3DwYReO', 'Nguyen Van Staff', '0901000001'),
('admin01', '$2b$10$QEWMj.bSXKSfdcBYITwa4e81ElAXSzD7K0ZAV.jG.sa5qpKIKUobS', 'Nguyen Van Admin', '0901000002');

INSERT INTO staff (user_id, gender, age, is_active)
SELECT id, 'MALE', 24, TRUE FROM users WHERE username = 'staff01';

INSERT INTO admins (user_id)
SELECT id FROM users WHERE username = 'admin01';

-- ========== Danh mục ==========
INSERT INTO categories (name) VALUES
('Cà phê'),
('Trà'),
('Bánh ngọt'),
('Đồ uống khác');

-- ========== Sản phẩm ==========
-- category_id: 1=Cà phê, 2=Trà, 3=Bánh ngọt, 4=Đồ uống khác
INSERT INTO products (name, description, price, image_url, quantity, is_available, category_id) VALUES
('Cà phê sữa đá', 'Cà phê phin, sữa đặc, đá', 29000.00, 'https://images.unsplash.com/photo-1517701604599-bb29b565090c', 200, TRUE, 1),
('Americano', 'Espresso pha loãng nóng', 35000.00, 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085', 150, TRUE, 1),
('Latte', 'Espresso và sữa tươi, lớp sữa mịn', 45000.00, 'https://images.unsplash.com/photo-1509042239860-f550ce710b93', 120, TRUE, 1),
('Bạc xỉu', 'Cà phê sữa ít đá, vị ngọt vừa', 32000.00, 'https://images.unsplash.com/photo-1461023058943-07fcbe16d735', 180, TRUE, 1),
('Trà đào cam sả', 'Trà đen, đào, cam, sả', 39000.00, 'https://images.unsplash.com/photo-1556679343-c7306c1976bc', 100, TRUE, 2),
('Trà sữa ô long', 'Ô long, sữa, trân châu', 42000.00, 'https://images.unsplash.com/photo-1558857563-c0c9d9a8e7d7', 90, TRUE, 2),
('Trà matcha latte', 'Matcha Nhật, sữa', 48000.00, 'https://images.unsplash.com/photo-1515823064-d6e0c04616a7', 80, TRUE, 2),
('Croissant bơ', 'Bánh sừng bò Pháp', 35000.00, 'https://images.unsplash.com/photo-1509440159596-0249088772ff', 50, TRUE, 3),
('Tiramisu', 'Bánh tiramisu cổ điển', 55000.00, 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9', 40, TRUE, 3),
('Cold brew', 'Cà phê ủ lạnh 12h', 40000.00, 'https://images.unsplash.com/photo-1461988320302-91bde64fc8e4', 70, TRUE, 4),
('Nước cam ép', 'Cam tươi ép', 35000.00, 'https://images.unsplash.com/photo-1571689930408-1bde7a67cfd1', 60, TRUE, 4);

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
