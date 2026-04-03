-- Mock data 2 cho WebCafe
-- Chạy sau WebCafe.sql trên DB trống.
-- Mật khẩu mặc định cho toàn bộ users bên dưới: 123456

USE WebCafe;

-- =========================
-- 1) USERS: 5 STAFF + 1 ADMIN + 10 CUSTOMERS
-- =========================
INSERT INTO users (username, password, full_name, phone, created_at) VALUES
('staff01', '$2b$10$ZcGXIBExA3Ssv88VsoosOerIi9JVzzO/rWTmPjk20NxdjQ3DwYReO', 'Nguyen Van Minh', '0901000001', '2025-01-05 08:00:00'),
('staff02', '$2b$10$ZcGXIBExA3Ssv88VsoosOerIi9JVzzO/rWTmPjk20NxdjQ3DwYReO', 'Tran Thi Hoa', '0901000002', '2025-01-06 08:10:00'),
('staff03', '$2b$10$ZcGXIBExA3Ssv88VsoosOerIi9JVzzO/rWTmPjk20NxdjQ3DwYReO', 'Le Quoc Dat', '0901000003', '2025-01-07 08:20:00'),
('staff04', '$2b$10$ZcGXIBExA3Ssv88VsoosOerIi9JVzzO/rWTmPjk20NxdjQ3DwYReO', 'Pham Ngoc Anh', '0901000004', '2025-01-08 08:30:00'),
('staff05', '$2b$10$ZcGXIBExA3Ssv88VsoosOerIi9JVzzO/rWTmPjk20NxdjQ3DwYReO', 'Do Minh Khang', '0901000005', '2025-01-09 08:40:00'),
('admin01', '$2b$10$QEWMj.bSXKSfdcBYITwa4e81ElAXSzD7K0ZAV.jG.sa5qpKIKUobS', 'Nguyen Thi Lan', '0901000099', '2025-01-01 07:30:00'),

('cust01', '$2b$10$ZcGXIBExA3Ssv88VsoosOerIi9JVzzO/rWTmPjk20NxdjQ3DwYReO', 'Le Van A', '0902000001', '2025-02-01 09:00:00'),
('cust02', '$2b$10$ZcGXIBExA3Ssv88VsoosOerIi9JVzzO/rWTmPjk20NxdjQ3DwYReO', 'Pham Thi B', '0902000002', '2025-02-02 09:10:00'),
('cust03', '$2b$10$ZcGXIBExA3Ssv88VsoosOerIi9JVzzO/rWTmPjk20NxdjQ3DwYReO', 'Hoang Van C', '0902000003', '2025-02-03 09:20:00'),
('cust04', '$2b$10$ZcGXIBExA3Ssv88VsoosOerIi9JVzzO/rWTmPjk20NxdjQ3DwYReO', 'Bui Thi D', '0902000004', '2025-03-10 10:00:00'),
('cust05', '$2b$10$ZcGXIBExA3Ssv88VsoosOerIi9JVzzO/rWTmPjk20NxdjQ3DwYReO', 'Dang Van E', '0902000005', '2025-04-12 10:15:00'),
('cust06', '$2b$10$ZcGXIBExA3Ssv88VsoosOerIi9JVzzO/rWTmPjk20NxdjQ3DwYReO', 'Vo Thi F', '0902000006', '2025-06-18 11:00:00'),
('cust07', '$2b$10$ZcGXIBExA3Ssv88VsoosOerIi9JVzzO/rWTmPjk20NxdjQ3DwYReO', 'Huynh Van G', '0902000007', '2025-08-01 11:10:00'),
('cust08', '$2b$10$ZcGXIBExA3Ssv88VsoosOerIi9JVzzO/rWTmPjk20NxdjQ3DwYReO', 'Tran Thi H', '0902000008', '2025-11-25 12:00:00'),
('cust09', '$2b$10$ZcGXIBExA3Ssv88VsoosOerIi9JVzzO/rWTmPjk20NxdjQ3DwYReO', 'Nguyen Van I', '0902000009', '2026-01-05 12:20:00'),
('cust10', '$2b$10$ZcGXIBExA3Ssv88VsoosOerIi9JVzzO/rWTmPjk20NxdjQ3DwYReO', 'Le Thi K', '0902000010', '2026-03-01 13:00:00');

-- =========================
-- 2) STAFF / ADMIN / CUSTOMERS
-- =========================
INSERT INTO staff (user_id, gender, age, is_active)
SELECT id, 'MALE', 24, TRUE FROM users WHERE username = 'staff01';
INSERT INTO staff (user_id, gender, age, is_active)
SELECT id, 'FEMALE', 22, TRUE FROM users WHERE username = 'staff02';
INSERT INTO staff (user_id, gender, age, is_active)
SELECT id, 'MALE', 26, TRUE FROM users WHERE username = 'staff03';
INSERT INTO staff (user_id, gender, age, is_active)
SELECT id, 'FEMALE', 21, TRUE FROM users WHERE username = 'staff04';
INSERT INTO staff (user_id, gender, age, is_active)
SELECT id, 'MALE', 28, TRUE FROM users WHERE username = 'staff05';

INSERT INTO admins (user_id)
SELECT id FROM users WHERE username = 'admin01';

INSERT INTO customers (user_id, address, is_active)
SELECT id, '123 Nguyen Trai, Quan 1, TP.HCM', TRUE FROM users WHERE username = 'cust01';
INSERT INTO customers (user_id, address, is_active)
SELECT id, '456 Le Loi, Quan 1, TP.HCM', TRUE FROM users WHERE username = 'cust02';
INSERT INTO customers (user_id, address, is_active)
SELECT id, '789 Tran Hung Dao, Quan 5, TP.HCM', TRUE FROM users WHERE username = 'cust03';
INSERT INTO customers (user_id, address, is_active)
SELECT id, '15 Pasteur, Quan 3, TP.HCM', TRUE FROM users WHERE username = 'cust04';
INSERT INTO customers (user_id, address, is_active)
SELECT id, '88 Vo Van Tan, Quan 3, TP.HCM', TRUE FROM users WHERE username = 'cust05';
INSERT INTO customers (user_id, address, is_active)
SELECT id, '22 Dien Bien Phu, Binh Thanh, TP.HCM', TRUE FROM users WHERE username = 'cust06';
INSERT INTO customers (user_id, address, is_active)
SELECT id, '200 Phan Xich Long, Phu Nhuan, TP.HCM', TRUE FROM users WHERE username = 'cust07';
INSERT INTO customers (user_id, address, is_active)
SELECT id, '19 Nguyen Hue, Quan 1, TP.HCM', TRUE FROM users WHERE username = 'cust08';
INSERT INTO customers (user_id, address, is_active)
SELECT id, '453 To Ngoc Van, Thu Duc, TP.HCM', TRUE FROM users WHERE username = 'cust09';
INSERT INTO customers (user_id, address, is_active)
SELECT id, '300 Cong Hoa, Tan Binh, TP.HCM', TRUE FROM users WHERE username = 'cust10';

-- =========================
-- 3) STAFF SHIFTS (đủ ca, phân bổ thực tế)
-- =========================
-- staff01: MON-SAT MORNING
INSERT INTO staff_shifts (staff_id, shift_id)
SELECT s.user_id, sh.id
FROM staff s
JOIN users u ON u.id = s.user_id
JOIN shifts sh ON sh.day_of_week IN ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY')
             AND sh.shift_time = 'MORNING'
WHERE u.username = 'staff01';

-- staff02: MON-FRI AFTERNOON
INSERT INTO staff_shifts (staff_id, shift_id)
SELECT s.user_id, sh.id
FROM staff s
JOIN users u ON u.id = s.user_id
JOIN shifts sh ON sh.day_of_week IN ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY')
             AND sh.shift_time = 'AFTERNOON'
WHERE u.username = 'staff02';

-- staff03: MON-SUN EVENING
INSERT INTO staff_shifts (staff_id, shift_id)
SELECT s.user_id, sh.id
FROM staff s
JOIN users u ON u.id = s.user_id
JOIN shifts sh ON sh.shift_time = 'EVENING'
WHERE u.username = 'staff03';

-- staff04: WEEKEND MORNING + AFTERNOON
INSERT INTO staff_shifts (staff_id, shift_id)
SELECT s.user_id, sh.id
FROM staff s
JOIN users u ON u.id = s.user_id
JOIN shifts sh ON sh.day_of_week IN ('SATURDAY','SUNDAY')
             AND sh.shift_time IN ('MORNING','AFTERNOON')
WHERE u.username = 'staff04';

-- staff05: THU-FRI-SAT AFTERNOON + SUNDAY EVENING
INSERT INTO staff_shifts (staff_id, shift_id)
SELECT s.user_id, sh.id
FROM staff s
JOIN users u ON u.id = s.user_id
JOIN shifts sh ON (
      (sh.day_of_week IN ('THURSDAY','FRIDAY','SATURDAY') AND sh.shift_time = 'AFTERNOON')
   OR (sh.day_of_week = 'SUNDAY' AND sh.shift_time = 'EVENING')
)
WHERE u.username = 'staff05';

-- =========================
-- 4) CATEGORIES + PRODUCTS (giữ nguyên bản đang dùng)
-- =========================
INSERT INTO categories (name, image_url) VALUES
('Cà phê', 'https://cdn-icons-png.flaticon.com/128/16228/16228473.png'),
('Trà', 'https://cdn-icons-png.flaticon.com/128/15150/15150965.png'),
('Bánh ngọt', 'https://cdn-icons-png.flaticon.com/128/792/792815.png'),
('Đồ uống khác', 'https://cdn-icons-png.flaticon.com/128/814/814644.png');

INSERT INTO products (name, description, price, image_url, quantity, is_available, category_id) VALUES
('Cà phê sữa đá', 'Cà phê phin, sữa đặc, đá', 29000.00, 'https://images.unsplash.com/photo-1517701604599-bb29b565090c', 260, TRUE, 1),
('Americano', 'Espresso pha loãng nóng', 35000.00, 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085', 180, TRUE, 1),
('Latte', 'Espresso và sữa tươi, lớp sữa mịn', 45000.00, 'https://images.unsplash.com/photo-1509042239860-f550ce710b93', 150, TRUE, 1),
('Bạc xỉu', 'Cà phê sữa ít đá, vị ngọt vừa', 32000.00, 'https://images.unsplash.com/photo-1461023058943-07fcbe16d735', 200, TRUE, 1),
('Trà đào cam sả', 'Trà đen, đào, cam, sả', 39000.00, 'https://images.unsplash.com/photo-1556679343-c7306c1976bc', 130, TRUE, 2),
('Trà sữa ô long', 'Ô long, sữa, trân châu', 42000.00, 'https://images.unsplash.com/photo-1743310835056-5c6b110158be?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 120, TRUE, 2),
('Trà matcha latte', 'Matcha Nhật, sữa', 48000.00, 'https://images.unsplash.com/photo-1515823064-d6e0c04616a7', 90, TRUE, 2),
('Croissant bơ', 'Bánh sừng bò Pháp', 35000.00, 'https://images.unsplash.com/photo-1509440159596-0249088772ff', 80, TRUE, 3),
('Tiramisu', 'Bánh tiramisu cổ điển', 55000.00, 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9', 70, TRUE, 3),
('Cold brew', 'Cà phê ủ lạnh 12h', 40000.00, 'https://images.unsplash.com/photo-1461988320302-91bde64fc8e4', 95, TRUE, 4),
('Nước cam ép', 'Cam tươi ép', 35000.00, 'https://images.unsplash.com/photo-1631018572225-67bec2d7aa38?q=80&w=686&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 85, TRUE, 4);

-- =========================
-- 5) 10 TABLES
-- =========================
INSERT INTO cafe_tables (table_number, status) VALUES
(1, 'OCCUPIED'),
(2, 'OCCUPIED'),
(3, 'EMPTY'),
(4, 'EMPTY'),
(5, 'OCCUPIED'),
(6, 'EMPTY'),
(7, 'EMPTY'),
(8, 'OCCUPIED'),
(9, 'EMPTY'),
(10, 'EMPTY');

-- =========================
-- 6) ORDERS (2024 -> 2026)
-- =========================
INSERT INTO orders (table_id, status, created_at) VALUES
(1,  'PAID',      '2025-05-15 09:10:00'),
(2,  'PAID',      '2025-06-20 14:25:00'),
(5,  'DONE',      '2025-08-11 19:40:00'),
(8,  'PREPARING', '2025-10-02 20:05:00'),
(3,  'PAID',      '2025-12-24 08:30:00'),
(4,  'PENDING',   '2026-01-10 15:10:00'),
(1,  'PAID',      '2026-02-14 21:00:00'),
(6,  'DONE',      '2026-03-03 10:12:00'),
(7,  'PAID',      '2026-03-15 18:45:00'),
(2,  'PREPARING', '2026-03-22 09:50:00'),
(5,  'PAID',      '2026-03-25 17:30:00'),
(8,  'PENDING',   '2026-03-26 11:15:00'),
(3,  'PAID',      '2025-07-05 09:20:00'),
(4,  'PAID',      '2025-09-18 16:05:00'),
(6,  'DONE',      '2025-11-03 12:40:00'),
(7,  'PREPARING', '2025-12-11 18:20:00'),
(10, 'PAID',      '2026-01-21 20:10:00'),
(3,  'PENDING',   '2026-02-02 08:45:00'),
(4,  'PAID',      '2026-02-28 14:30:00'),
(6,  'DONE',      '2026-03-08 11:25:00'),
(7,  'PAID',      '2026-03-18 19:15:00'),
(9,  'PREPARING', '2026-03-21 10:05:00'),
(10, 'PAID',      '2026-03-24 17:40:00'),
(1,  'PENDING',   '2026-03-26 20:05:00'),
(1,  'PAID',      '2024-01-12 08:05:00'),
(2,  'PAID',      '2024-02-03 09:15:00'),
(3,  'DONE',      '2024-02-20 11:20:00'),
(4,  'PAID',      '2024-03-05 14:30:00'),
(5,  'PREPARING', '2024-03-29 16:10:00'),
(6,  'PAID',      '2024-04-11 10:20:00'),
(7,  'DONE',      '2024-05-07 18:05:00'),
(8,  'PAID',      '2024-06-16 09:40:00'),
(9,  'PENDING',   '2024-07-02 12:25:00'),
(10, 'PAID',      '2024-08-19 15:35:00'),
(1,  'DONE',      '2024-09-25 19:10:00'),
(2,  'PAID',      '2024-10-14 07:55:00'),
(3,  'PREPARING', '2024-11-03 08:40:00'),
(4,  'PAID',      '2024-11-18 17:25:00'),
(5,  'PENDING',   '2024-12-09 20:05:00'),
(6,  'PAID',      '2024-12-28 21:10:00'),
(7,  'PAID',      '2026-03-27 08:15:00'),
(8,  'PREPARING', '2026-03-27 10:35:00'),
(9,  'PAID',      '2026-03-27 14:20:00'),
(10, 'PENDING',   '2026-03-27 19:00:00');

-- =========================
-- 7) ORDER ITEMS
-- =========================
INSERT INTO order_items (order_id, product_id, quantity, price, updated) VALUES
(1, 1, 2, 29000, '2025-05-15 09:11:00'),
(1, 8, 1, 35000, '2025-05-15 09:12:00'),

(2, 3, 1, 45000, '2025-06-20 14:27:00'),
(2, 5, 2, 39000, '2025-06-20 14:28:00'),

(3, 4, 2, 32000, '2025-08-11 19:42:00'),
(3, 9, 1, 55000, '2025-08-11 19:43:00'),

(4, 6, 2, 42000, '2025-10-02 20:06:00'),
(4, 10,1, 40000, '2025-10-02 20:08:00'),

(5, 2, 1, 35000, '2025-12-24 08:32:00'),
(5, 11,2, 35000, '2025-12-24 08:34:00'),

(6, 7, 1, 48000, '2026-01-10 15:12:00'),
(6, 8, 2, 35000, '2026-01-10 15:14:00'),

(7, 1, 1, 29000, '2026-02-14 21:02:00'),
(7, 6, 1, 42000, '2026-02-14 21:03:00'),

(8, 3, 1, 45000, '2026-03-03 10:14:00'),
(8, 5, 1, 39000, '2026-03-03 10:15:00'),

(9, 4, 1, 32000, '2026-03-15 18:46:00'),
(9, 9, 1, 55000, '2026-03-15 18:47:00'),

(10,2, 1, 35000, '2026-03-22 09:51:00'),
(10,10,1, 40000, '2026-03-22 09:52:00'),

(11,6, 2, 42000, '2026-03-25 17:32:00'),
(11,9, 1, 55000, '2026-03-25 17:34:00'),

(12,1, 1, 29000, '2026-03-26 11:16:00'),
(12,11,1, 35000, '2026-03-26 11:17:00'),

(13,1, 2, 29000, '2025-07-05 09:21:00'),
(13,5, 1, 39000, '2025-07-05 09:22:00'),

(14,3, 1, 45000, '2025-09-18 16:07:00'),
(14,6, 2, 42000, '2025-09-18 16:08:00'),

(15,2, 1, 35000, '2025-11-03 12:42:00'),
(15,8, 1, 35000, '2025-11-03 12:43:00'),

(16,4, 2, 32000, '2025-12-11 18:21:00'),
(16,10,1, 40000, '2025-12-11 18:22:00'),

(17,7, 1, 48000, '2026-01-21 20:12:00'),
(17,9, 1, 55000, '2026-01-21 20:13:00'),

(18,1, 1, 29000, '2026-02-02 08:46:00'),
(18,11,2, 35000, '2026-02-02 08:47:00'),

(19,5, 2, 39000, '2026-02-28 14:31:00'),
(19,6, 1, 42000, '2026-02-28 14:32:00'),

(20,3, 1, 45000, '2026-03-08 11:26:00'),
(20,10,1, 40000, '2026-03-08 11:27:00'),

(21,2, 1, 35000, '2026-03-18 19:16:00'),
(21,9, 1, 55000, '2026-03-18 19:17:00'),

(22,4, 1, 32000, '2026-03-21 10:06:00'),
(22,8, 1, 35000, '2026-03-21 10:07:00'),

(23,6, 2, 42000, '2026-03-24 17:41:00'),
(23,1, 1, 29000, '2026-03-24 17:42:00'),

(24,11,1, 35000, '2026-03-26 20:06:00'),
(24,7, 1, 48000, '2026-03-26 20:07:00'),

(25,1, 1, 29000, '2024-01-12 08:06:00'),
(25,8, 2, 35000, '2024-01-12 08:07:00'),
(26,3, 2, 45000, '2024-02-03 09:16:00'),
(26,5, 1, 39000, '2024-02-03 09:17:00'),
(27,4, 1, 32000, '2024-02-20 11:22:00'),
(27,9, 1, 55000, '2024-02-20 11:23:00'),
(28,6, 1, 42000, '2024-03-05 14:33:00'),
(28,10,2, 40000, '2024-03-05 14:34:00'),
(29,2, 2, 35000, '2024-03-29 16:11:00'),
(29,11,1, 35000, '2024-03-29 16:12:00'),
(30,7, 1, 48000, '2024-04-11 10:21:00'),
(30,8, 1, 35000, '2024-04-11 10:22:00'),
(31,1, 2, 29000, '2024-05-07 18:06:00'),
(31,6, 1, 42000, '2024-05-07 18:07:00'),
(32,3, 1, 45000, '2024-06-16 09:41:00'),
(32,5, 1, 39000, '2024-06-16 09:42:00'),
(33,4, 1, 32000, '2024-07-02 12:27:00'),
(33,9, 2, 55000, '2024-07-02 12:28:00'),
(34,2, 2, 35000, '2024-08-19 15:37:00'),
(34,10,1, 40000, '2024-08-19 15:38:00'),
(35,6, 1, 42000, '2024-09-25 19:11:00'),
(35,9, 1, 55000, '2024-09-25 19:12:00'),
(36,1, 1, 29000, '2024-10-14 07:56:00'),
(36,11,2, 35000, '2024-10-14 07:57:00'),
(37,1, 1, 29000, '2024-11-03 08:41:00'),
(37,5, 1, 39000, '2024-11-03 08:42:00'),
(38,3, 2, 45000, '2024-11-18 17:26:00'),
(38,6, 1, 42000, '2024-11-18 17:27:00'),
(39,2, 1, 35000, '2024-12-09 20:06:00'),
(39,8, 1, 35000, '2024-12-09 20:07:00'),
(40,7, 1, 48000, '2024-12-28 21:13:00'),
(40,9, 1, 55000, '2024-12-28 21:14:00'),
(41,4, 2, 32000, '2026-03-27 08:16:00'),
(41,10,1, 40000, '2026-03-27 08:17:00'),
(42,2, 1, 35000, '2026-03-27 10:36:00'),
(42,1, 1, 29000, '2026-03-27 10:37:00'),
(43,6, 1, 42000, '2026-03-27 14:21:00'),
(43,8, 2, 35000, '2026-03-27 14:22:00'),
(44,3, 1, 45000, '2026-03-27 19:03:00'),
(44,9, 1, 55000, '2026-03-27 19:04:00');

-- =========================
-- 8) PAYMENTS (cho các order đã PAID)
-- =========================
INSERT INTO payments (order_id, method, total_amount, paid_at) VALUES
(1,  'CASH',    93000,  '2025-05-15 09:20:00'),
(2,  'BANKING', 123000, '2025-06-20 14:40:00'),
(5,  'CASH',    105000, '2025-12-24 08:50:00'),
(7,  'BANKING', 71000,  '2026-02-14 21:20:00'),
(9,  'CASH',    87000,  '2026-03-15 19:00:00'),
(11, 'BANKING', 139000, '2026-03-25 17:50:00'),
(13, 'CASH',    97000,  '2025-07-05 09:35:00'),
(14, 'BANKING', 129000, '2025-09-18 16:25:00'),
(17, 'CASH',    103000, '2026-01-21 20:30:00'),
(19, 'BANKING', 120000, '2026-02-28 14:50:00'),
(21, 'CASH',    90000,  '2026-03-18 19:30:00'),
(23, 'BANKING', 113000, '2026-03-24 18:00:00'),
(25, 'CASH',    99000,  '2024-01-12 08:25:00'),
(26, 'BANKING', 129000, '2024-02-03 09:35:00'),
(28, 'CASH',    122000, '2024-03-05 14:55:00'),
(30, 'BANKING', 83000,  '2024-04-11 10:45:00'),
(32, 'CASH',    84000,  '2024-06-16 10:00:00'),
(34, 'BANKING', 110000, '2024-08-19 16:00:00'),
(36, 'CASH',    99000,  '2024-10-14 08:15:00'),
(38, 'BANKING', 132000, '2024-11-18 17:45:00'),
(40, 'CASH',    103000, '2024-12-28 21:30:00'),
(41, 'BANKING', 104000, '2026-03-27 08:35:00'),
(43, 'CASH',    112000, '2026-03-27 14:45:00');

-- =========================
-- 9) DELIVERIES (gắn với 1 số order đã thanh toán)
-- =========================
INSERT INTO deliveries (order_id, payment_id, customer_id, status, created_at, delivered_at) VALUES
(1,  (SELECT id FROM payments WHERE order_id = 1),  (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust01')), 'DELIVERED', '2025-05-15 09:22:00', '2025-05-15 10:00:00'),
(2,  (SELECT id FROM payments WHERE order_id = 2),  (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust02')), 'DELIVERED', '2025-06-20 14:42:00', '2025-06-20 15:20:00'),
(5,  (SELECT id FROM payments WHERE order_id = 5),  (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust03')), 'DELIVERED', '2025-12-24 08:52:00', '2025-12-24 09:40:00'),
(7,  (SELECT id FROM payments WHERE order_id = 7),  (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust09')), 'SHIPPING',  '2026-02-14 21:22:00', NULL),
(9,  (SELECT id FROM payments WHERE order_id = 9),  (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust10')), 'CONFIRMED', '2026-03-15 19:02:00', NULL),
(11, (SELECT id FROM payments WHERE order_id = 11), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust09')), 'PENDING',   '2026-03-25 17:55:00', NULL),
(13, (SELECT id FROM payments WHERE order_id = 13), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust04')), 'DELIVERED', '2025-07-05 09:37:00', '2025-07-05 10:20:00'),
(14, (SELECT id FROM payments WHERE order_id = 14), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust05')), 'DELIVERED', '2025-09-18 16:27:00', '2025-09-18 17:00:00'),
(17, (SELECT id FROM payments WHERE order_id = 17), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust06')), 'SHIPPING',  '2026-01-21 20:32:00', NULL),
(19, (SELECT id FROM payments WHERE order_id = 19), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust07')), 'CONFIRMED', '2026-02-28 14:52:00', NULL),
(21, (SELECT id FROM payments WHERE order_id = 21), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust08')), 'DELIVERED', '2026-03-18 19:32:00', '2026-03-18 20:10:00'),
(23, (SELECT id FROM payments WHERE order_id = 23), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust10')), 'PENDING',   '2026-03-24 18:02:00', NULL),
(25, (SELECT id FROM payments WHERE order_id = 25), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust01')), 'DELIVERED', '2024-01-12 08:27:00', '2024-01-12 09:05:00'),
(26, (SELECT id FROM payments WHERE order_id = 26), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust02')), 'DELIVERED', '2024-02-03 09:37:00', '2024-02-03 10:20:00'),
(28, (SELECT id FROM payments WHERE order_id = 28), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust03')), 'DELIVERED', '2024-03-05 14:57:00', '2024-03-05 15:40:00'),
(30, (SELECT id FROM payments WHERE order_id = 30), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust04')), 'DELIVERED', '2024-04-11 10:47:00', '2024-04-11 11:30:00'),
(32, (SELECT id FROM payments WHERE order_id = 32), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust05')), 'DELIVERED', '2024-06-16 10:02:00', '2024-06-16 10:50:00'),
(34, (SELECT id FROM payments WHERE order_id = 34), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust06')), 'CONFIRMED', '2024-08-19 16:02:00', NULL),
(36, (SELECT id FROM payments WHERE order_id = 36), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust07')), 'DELIVERED', '2024-10-14 08:17:00', '2024-10-14 09:00:00'),
(38, (SELECT id FROM payments WHERE order_id = 38), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust08')), 'SHIPPING',  '2024-11-18 17:47:00', NULL),
(40, (SELECT id FROM payments WHERE order_id = 40), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust09')), 'DELIVERED', '2024-12-28 21:32:00', '2024-12-28 22:20:00'),
(41, (SELECT id FROM payments WHERE order_id = 41), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust10')), 'PENDING',   '2026-03-27 08:37:00', NULL),
(43, (SELECT id FROM payments WHERE order_id = 43), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust02')), 'CONFIRMED', '2026-03-27 14:47:00', NULL);

-- =========================
-- 10) 10 đơn giao hàng (ship): table_id NULL, delivered_at từ 2026-04-04 xuống 2026-03-20
--     (order_id 45–54 sau batch orders phía trên)
-- =========================
INSERT INTO orders (table_id, status, created_at) VALUES
(NULL, 'PAID', '2026-04-03 09:00:00'),
(NULL, 'PAID', '2026-04-01 09:15:00'),
(NULL, 'PAID', '2026-03-31 10:20:00'),
(NULL, 'PAID', '2026-03-28 08:40:00'),
(NULL, 'PAID', '2026-03-26 11:10:00'),
(NULL, 'PAID', '2026-03-24 08:50:00'),
(NULL, 'PAID', '2026-03-23 14:25:00'),
(NULL, 'PAID', '2026-03-21 09:45:00'),
(NULL, 'PAID', '2026-03-20 10:30:00'),
(NULL, 'PAID', '2026-03-19 11:05:00');

INSERT INTO order_items (order_id, product_id, quantity, price, updated) VALUES
(45, 1, 2, 29000, '2026-04-03 09:01:00'),
(45, 5, 1, 39000, '2026-04-03 09:02:00'),
(46, 3, 1, 45000, '2026-04-01 09:16:00'),
(46, 8, 1, 35000, '2026-04-01 09:17:00'),
(47, 2, 2, 35000, '2026-03-31 10:21:00'),
(47, 6, 1, 42000, '2026-03-31 10:22:00'),
(48, 4, 2, 32000, '2026-03-28 08:41:00'),
(48, 9, 1, 55000, '2026-03-28 08:42:00'),
(49, 7, 1, 48000, '2026-03-26 11:11:00'),
(49, 10, 1, 40000, '2026-03-26 11:12:00'),
(50, 1, 1, 29000, '2026-03-24 08:51:00'),
(50, 11, 2, 35000, '2026-03-24 08:52:00'),
(51, 5, 2, 39000, '2026-03-23 14:26:00'),
(51, 3, 1, 45000, '2026-03-23 14:27:00'),
(52, 6, 2, 42000, '2026-03-21 09:46:00'),
(52, 8, 1, 35000, '2026-03-21 09:47:00'),
(53, 4, 1, 32000, '2026-03-20 10:31:00'),
(53, 10, 1, 40000, '2026-03-20 10:32:00'),
(54, 2, 1, 35000, '2026-03-19 11:06:00'),
(54, 9, 1, 55000, '2026-03-19 11:07:00');

INSERT INTO payments (order_id, method, total_amount, paid_at) VALUES
(45, 'CASH',    97000,  '2026-04-03 09:30:00'),
(46, 'BANKING', 80000,  '2026-04-01 09:45:00'),
(47, 'CASH',    112000, '2026-03-31 10:40:00'),
(48, 'BANKING', 119000, '2026-03-28 09:00:00'),
(49, 'CASH',    88000,  '2026-03-26 11:30:00'),
(50, 'BANKING', 99000,  '2026-03-24 09:10:00'),
(51, 'CASH',    123000, '2026-03-23 14:45:00'),
(52, 'BANKING', 119000, '2026-03-21 10:00:00'),
(53, 'CASH',    72000,  '2026-03-20 10:50:00'),
(54, 'BANKING', 90000,  '2026-03-19 11:30:00');

INSERT INTO deliveries (order_id, payment_id, customer_id, status, created_at, delivered_at) VALUES
(45, (SELECT id FROM payments WHERE order_id = 45), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust01')), 'DELIVERED', '2026-04-03 09:35:00', '2026-04-04 18:00:00'),
(46, (SELECT id FROM payments WHERE order_id = 46), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust02')), 'DELIVERED', '2026-04-01 09:50:00', '2026-04-02 16:30:00'),
(47, (SELECT id FROM payments WHERE order_id = 47), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust03')), 'DELIVERED', '2026-03-31 10:45:00', '2026-04-01 14:00:00'),
(48, (SELECT id FROM payments WHERE order_id = 48), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust04')), 'DELIVERED', '2026-03-28 09:05:00', '2026-03-29 17:20:00'),
(49, (SELECT id FROM payments WHERE order_id = 49), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust05')), 'DELIVERED', '2026-03-26 11:35:00', '2026-03-27 12:45:00'),
(50, (SELECT id FROM payments WHERE order_id = 50), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust06')), 'DELIVERED', '2026-03-24 09:15:00', '2026-03-25 10:30:00'),
(51, (SELECT id FROM payments WHERE order_id = 51), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust07')), 'DELIVERED', '2026-03-23 14:50:00', '2026-03-24 19:00:00'),
(52, (SELECT id FROM payments WHERE order_id = 52), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust08')), 'DELIVERED', '2026-03-21 10:05:00', '2026-03-22 15:15:00'),
(53, (SELECT id FROM payments WHERE order_id = 53), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust09')), 'DELIVERED', '2026-03-20 10:55:00', '2026-03-21 11:30:00'),
(54, (SELECT id FROM payments WHERE order_id = 54), (SELECT user_id FROM customers WHERE user_id = (SELECT id FROM users WHERE username = 'cust10')), 'DELIVERED', '2026-03-19 11:35:00', '2026-03-20 14:00:00');

-- =========================
-- 11) Đồng bộ trạng thái bàn tham khảo
-- =========================
UPDATE cafe_tables
SET status = CASE
  WHEN table_number IN (1,2,3,4,5,6,7,8,9,10) THEN 'OCCUPIED'
  ELSE 'EMPTY'
END;

