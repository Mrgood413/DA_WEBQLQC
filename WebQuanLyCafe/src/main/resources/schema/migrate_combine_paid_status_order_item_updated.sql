-- Gộp thanh toán vào orders.status (thêm PAID, bỏ cột paid) + thêm order_items.updated.
-- Sao lưu DB trước khi chạy. Điều chỉnh từng bước nếu cột đã đúng schema mới.

USE WebCafe;

-- 1) Mở rộng ENUM status để có PAID
ALTER TABLE orders
    MODIFY COLUMN status ENUM('PENDING','PREPARING','DONE','PAID') NOT NULL DEFAULT 'PENDING';

-- 2) Nếu vẫn còn cột paid: đánh dấu đã thanh toán
-- UPDATE orders SET status = 'PAID' WHERE paid = TRUE;
-- ALTER TABLE orders DROP COLUMN paid;

-- 3) Dòng món: cờ chỉnh sửa
ALTER TABLE order_items
    ADD COLUMN updated BOOLEAN NOT NULL DEFAULT FALSE;
