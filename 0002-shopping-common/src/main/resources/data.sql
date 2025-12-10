INSERT INTO t_department (code, name, discount_rate, tax_rate, department_sales) VALUES
(1, '文房具', 0.00, 0.10, 0),
(2, '生活雑貨', 0.00, 0.10, 0);

INSERT INTO t_product
(id, name, price, stock, department_code, tax_rate, discount_rate, point_mag) VALUES
('p01', '消しゴム', 100, 10, 1, 0.10, 0.00, 0.00)
,('p02', 'ノート', 200, 20, 1, 0.10, 0.00, 0.00)
,('p03', 'pname03', 300, 30, 2, 0.10, 0.00, 0.00)
,('p04', 'pname04', 400, 40, 2, 0.10, 0.00, 0.00)
,('p05', 'pname05', 500, 50, 1, 0.10, 0.00, 0.00)
,('p06', 'pname06', 600, 60, 1, 0.10, 0.00, 0.00)
,('p07', 'pname07', 700, 70, 2, 0.10, 0.00, 0.00)
,('p08', 'pname08', 800, 80, 2, 0.10, 0.00, 0.00)
,('p09', 'pname09', 900, 90, 1, 0.10, 0.00, 0.00)
,('p10', 'pname10', 1000, 100, 1, 0.10, 0.00, 0.00)
,('p11', 'pname11', 1100, 110, 2, 0.10, 0.00, 0.00)
,('p12', 'pname12', 1200, 120, 2, 0.10, 0.00, 0.00)
,('p13', 'pname13', 1300, 130, 1, 0.10, 0.00, 0.00)
;

INSERT INTO t_order
(id, order_date_time, billing_amount, customer_name, customer_address, customer_phone, customer_email_address, payment_method, points_used, points_earned) VALUES
('o01', '2025-10-31', 770, 'cname01', 'caddress01', '090-0000-0001', 'cname01@example.com', 'BANK', 0, 0)
,('o02', '2025-10-31', 2000, 'cname02', 'caddress02', '090-0000-0002', 'cname02@example.com', 'CONVENIENCE_STORE', 0, 0)
;

INSERT INTO t_order_item
(id, order_id, product_id, price_at_order, quantity) VALUES
('i01', 'o01', 'p01', 100, 3)
,('i02', 'o01', 'p02', 200, 2)
;

INSERT INTO t_user (id, username, password, role, items_purchased, purchase_count, total_spent, points, point_rate) VALUES
('u01', 'admin', '$2a$10$7q9D1qjJjVl3eRUs7ZfvUuNTZ0Ae5GIlisPoC8fPWexZf3vV2qLAy', 'ADMIN', 0, 0, 0, 1000, 0.02)
