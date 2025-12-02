CREATE DATABASE sportystore COLLATE Vietnamese_CI_AS;
GO
USE sportystore;
GO

CREATE TABLE role (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  name NVARCHAR(50) NOT NULL UNIQUE
);
GO

CREATE TABLE user_account (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  name NVARCHAR(150) NOT NULL,
  email NVARCHAR(200) NOT NULL UNIQUE,
  password NVARCHAR(255) NOT NULL,
  phone NVARCHAR(50),
  created_at DATETIME2 DEFAULT SYSUTCDATETIME()
);
GO

CREATE TABLE user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  CONSTRAINT PK_user_role PRIMARY KEY (user_id, role_id),
  CONSTRAINT FK_userrole_user FOREIGN KEY (user_id) REFERENCES user_account(id),
  CONSTRAINT FK_userrole_role FOREIGN KEY (role_id) REFERENCES role(id)
);
GO

CREATE TABLE category (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  name NVARCHAR(120) NOT NULL UNIQUE
);
GO

CREATE TABLE product (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  name NVARCHAR(255) NOT NULL,
  description NVARCHAR(MAX),
  price DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  sku NVARCHAR(100),
  category_id BIGINT NULL,
  active BIT NOT NULL DEFAULT 1,
  created_at DATETIME2 DEFAULT SYSUTCDATETIME(),
  CONSTRAINT FK_product_category FOREIGN KEY (category_id) REFERENCES category(id)
);
GO

CREATE TABLE product_variant (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  product_id BIGINT NOT NULL,
  sku NVARCHAR(150),
  size NVARCHAR(50),
  color NVARCHAR(80),
  stock INT NOT NULL DEFAULT 0,
  price DECIMAL(12,2) NULL,
  CONSTRAINT FK_variant_product FOREIGN KEY (product_id) REFERENCES product(id)
);
GO

CREATE TABLE address (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  user_id BIGINT NOT NULL,
  full_name NVARCHAR(150),
  street NVARCHAR(255),
  city NVARCHAR(100),
  province NVARCHAR(100),
  postal_code NVARCHAR(30),
  phone NVARCHAR(50),
  is_default BIT DEFAULT 0,
  CONSTRAINT FK_address_user FOREIGN KEY (user_id) REFERENCES user_account(id)
);
GO

CREATE TABLE orders (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  user_id BIGINT NOT NULL,
  status NVARCHAR(50) NOT NULL DEFAULT 'CREATED',
  total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  payment_method NVARCHAR(80),
  shipping_address_id BIGINT NULL,
  created_at DATETIME2 DEFAULT SYSUTCDATETIME(),
  CONSTRAINT FK_orders_user FOREIGN KEY (user_id) REFERENCES user_account(id),
  CONSTRAINT FK_orders_address FOREIGN KEY (shipping_address_id) REFERENCES address(id)
);
GO

CREATE TABLE order_item (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  order_id BIGINT NOT NULL,
  product_variant_id BIGINT NOT NULL,
  qty INT NOT NULL DEFAULT 1,
  price DECIMAL(12,2) NOT NULL,
  CONSTRAINT FK_orderitem_order FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT FK_orderitem_variant FOREIGN KEY (product_variant_id) REFERENCES product_variant(id)
);
GO

CREATE TABLE review (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  user_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  rating INT NOT NULL,
  comment NVARCHAR(MAX),
  created_at DATETIME2 DEFAULT SYSUTCDATETIME(),
  CONSTRAINT FK_review_user FOREIGN KEY (user_id) REFERENCES user_account(id),
  CONSTRAINT FK_review_product FOREIGN KEY (product_id) REFERENCES product(id),
  CONSTRAINT CHK_review_rating CHECK (rating BETWEEN 1 AND 5)
);
GO

CREATE TABLE product_image (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  product_id BIGINT NOT NULL,
  url NVARCHAR(500),
  is_primary BIT DEFAULT 0,
  CONSTRAINT FK_image_product FOREIGN KEY (product_id) REFERENCES product(id)
);
GO

-- Seed
INSERT INTO role (name) VALUES (N'ROLE_USER'), (N'ROLE_ADMIN');
GO

INSERT INTO user_account (name, email, password, phone) VALUES
(N'Admin Demo', N'admin@example.com', N'admin123', N'0123456789'),
(N'Nguyen Khach', N'khach1@example.com', N'pass123', N'0987654321'),
(N'Le Customer', N'khach2@example.com', N'pass123', N'0911222333');
GO

INSERT INTO user_role (user_id, role_id) VALUES
(1, 2), (1, 1), (2, 1), (3, 1);
GO

INSERT INTO category (name) VALUES
(N'Áo thun'), (N'Qu?n short'), (N'Giày th? thao'), (N'Ph? ki?n');
GO

INSERT INTO product (name, description, price, sku, category_id, active) VALUES
(N'Áo thun th? thao DryFit - Xanh', N'Áo thun ch?t li?u DryFit.', 299000, N'AT-DRY-001', 1, 1),
(N'Qu?n short nam - ?en', N'Qu?n short co giãn.', 199000, N'QS-BLK-001', 2, 1),
(N'Giày ch?y b? Lightning', N'Giày ch?y nh?.', 1290000, N'SH-LIGHT-001', 3, 1),
(N'M? l??i trai th? thao', N'M? th? thao.', 150000, N'ACC-HAT-001', 4, 1),
(N'Áo thun th? thao DryFit - ??', N'Phiên b?n màu ??.', 299000, N'AT-DRY-002', 1, 1);
GO

INSERT INTO product_variant (product_id, sku, size, color, stock, price) VALUES
(1, N'AT-DRY-001-S-BL', N'S', N'Xanh', 20, 299000),
(1, N'AT-DRY-001-M-BL', N'M', N'Xanh', 35, 299000),
(5, N'AT-DRY-002-M-RED', N'M', N'??', 15, 299000),
(2, N'QS-BLK-001-M', N'M', N'?en', 40, 199000),
(3, N'SH-LIGHT-001-42', N'42', N'Tr?ng', 10, 1290000),
(4, N'ACC-HAT-001-ONE', N'ONE', N'?en', 50, 150000);
GO

INSERT INTO orders (user_id, status, total_amount, payment_method, shipping_address_id)
VALUES (2, N'PAID', 598000, N'COD', NULL);
GO

INSERT INTO order_item (order_id, product_variant_id, qty, price)
VALUES (1, 1, 2, 299000);
GO

INSERT INTO review (user_id, product_id, rating, comment) VALUES
(2, 1, 5, N'Good!'),
(3, 3, 4, N'Khá ?n.');
GO

drop database sportystore

SELECT * FROM 

UPDATE user_account
SET password = '$2a$10$Tc4Xjm7iPZQfg.yCLZ8ONeQAp6LkVRCj9S0KpkCDS8SnOUBKKuIry'
WHERE username = 'user1';


SELECT id, username, password, LEN(password) AS pwd_len
FROM user_account
WHERE username = 'user1';

ALTER TABLE product ADD image_url NVARCHAR(500);
GO

-- (Tùy ch?n) C?p nh?t ?nh m?u cho các s?n ph?m c? ?? test cho ??p
UPDATE product SET image_url = 'https://via.placeholder.com/300' WHERE image_url IS NULL;
GO

ALTER TABLE product ADD sale_price DECIMAL(10, 2);
GO

UPDATE product 
SET image_url = 'https://placehold.co/300x300?text=No+Image'
WHERE image_url LIKE '%via.placeholder.com%';
GO