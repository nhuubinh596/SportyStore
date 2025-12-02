CREATE DATABASE sportystore COLLATE Vietnamese_CI_AS;
GO

USE sportystore;
GO

-- =============================================
-- 1. XÓA B?NG C? (Theo th? t? Con tr??c -> Cha sau)
-- =============================================
-- Dùng l?nh DROP IF EXISTS (??n gi?n, không báo l?i n?u b?ng ch?a có)
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS wishlist;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS voucher;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS user_account;
DROP TABLE IF EXISTS address;
GO

-- =============================================
-- 2. T?O 10 B?NG C? B?N
-- =============================================

-- B?NG 1: USER (Tài kho?n)
CREATE TABLE user_account (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(100),
    email NVARCHAR(100),
    phone NVARCHAR(20),
    created_at DATETIME2 DEFAULT GETDATE()
);

-- B?NG 2: ROLE (Quy?n)
CREATE TABLE role (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL UNIQUE
);

-- B?NG 3: USER_ROLE (B?ng n?i User-Role)
CREATE TABLE user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user_account(id),
    FOREIGN KEY (role_id) REFERENCES role(id)
);

-- B?NG 4: CATEGORY (Danh m?c)
CREATE TABLE category (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL UNIQUE
);

-- B?NG 5: PRODUCT (S?n ph?m)
CREATE TABLE product (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    price DECIMAL(12,2) NOT NULL DEFAULT 0,
    sale_price DECIMAL(12,2) NULL,
    description NVARCHAR(MAX),
    image_url NVARCHAR(500),
    category_id BIGINT,
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);

-- B?NG 6: ORDERS (??n hàng)
CREATE TABLE orders (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT,
    status NVARCHAR(50) DEFAULT 'PENDING',
    total_amount DECIMAL(12,2) DEFAULT 0,
    full_name NVARCHAR(100),
    phone NVARCHAR(20),
    address NVARCHAR(500),
    payment_method NVARCHAR(50),
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES user_account(id)
);

-- B?NG 8: VOUCHER (Mã gi?m giá)
CREATE TABLE voucher (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    discount_amount DECIMAL(12,2) NOT NULL,
    expiration_date DATETIME2
);

-- B?NG 9: REVIEW (?ánh giá)
CREATE TABLE review (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment NVARCHAR(MAX),
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES user_account(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE order_item (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL, -- B?t bu?c tên là product_id
    qty INT NOT NULL DEFAULT 1,
    price DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE wishlist (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL, -- B?t bu?c tên là product_id
    created_at DATETIME2 DEFAULT GETDATE(),
    UNIQUE(user_id, product_id), -- Ch?n trùng l?p (1 ng??i ko like 2 l?n 1 món)
    FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

-- =============================================
-- 3. CHÈN D? LI?U (INSERT TH? CÔNG)
-- =============================================

-- ROLE
INSERT INTO role (name) VALUES ('ROLE_ADMIN'), ('ROLE_USER');

-- USER (Pass: 123)
INSERT INTO user_account (username, password, full_name, email, phone) VALUES 
('admin', '123', N'Qu?n Tr? Viên', 'admin@sporty.com', '0909111222'),
('user1', '123', N'Nguy?n V?n A', 'user1@gmail.com', '0912333444'),
('user2', '123', N'Tr?n Th? B', 'user2@gmail.com', '0912555666');

-- USER_ROLE
INSERT INTO user_role (user_id, role_id) VALUES (1, 1), (1, 2), (2, 2), (3, 2);

-- CATEGORY
INSERT INTO category (name) VALUES 
(N'Áo thun'), (N'Qu?n short'), (N'Giày th? thao'), (N'Ph? ki?n'), (N'?? t?p Gym');

-- PRODUCT (Kho?ng 15 s?n ph?m m?u ??i di?n)
INSERT INTO product (name, price, sale_price, category_id, image_url, description) VALUES
(N'Áo Thun DryFit Cool', 299000, 250000, 1, 'https://placehold.co/300x300?text=Ao+Thun', N'Thoáng mát.'),
(N'Áo Polo Th? Thao', 450000, NULL, 1, 'https://placehold.co/300x300?text=Polo', N'L?ch s?.'),
(N'Áo Tanktop Gym', 150000, 120000, 1, 'https://placehold.co/300x300?text=Tanktop', N'Khoe c? b?p.'),
(N'Qu?n Short 2 L?p', 220000, NULL, 2, 'https://placehold.co/300x300?text=Short', N'Có l?p lót.'),
(N'Qu?n Jogger Thun', 320000, 280000, 2, 'https://placehold.co/300x300?text=Jogger', N'N?ng ??ng.'),
(N'Giày Nike Pegasus', 3500000, 2990000, 3, 'https://placehold.co/300x300?text=Nike', N'Ch?y b?.'),
(N'Giày Adidas Ultra', 4200000, NULL, 3, 'https://placehold.co/300x300?text=Das', N'Êm ái.'),
(N'Giày Bitis Hunter', 890000, 790000, 3, 'https://placehold.co/300x300?text=Bitis', N'Hàng Vi?t.'),
(N'Bình N??c 1L', 90000, NULL, 4, 'https://placehold.co/300x300?text=Binh', N'Nh?a an toàn.'),
(N'G?ng Tay Th? Môn', 300000, NULL, 4, 'https://placehold.co/300x300?text=Gang', N'B?t dính.'),
(N'Túi Tr?ng Gym', 350000, 299000, 4, 'https://placehold.co/300x300?text=Tui', N'??ng ??.'),
(N'Th?m Yoga', 199000, NULL, 5, 'https://placehold.co/300x300?text=Tham', N'Ch?ng tr??t.'),
(N'Dây Nh?y', 120000, NULL, 5, 'https://placehold.co/300x300?text=Day', N'T?c ?? cao.'),
(N'?ai L?ng C?ng', 450000, 400000, 5, 'https://placehold.co/300x300?text=Dai', N'H? tr? l?ng.'),
(N'Áo Khoác Gió', 550000, NULL, 1, 'https://placehold.co/300x300?text=Khoac', N'Ch?ng n??c.');

-- ORDERS
INSERT INTO orders (user_id, status, total_amount, full_name, phone, address, payment_method, created_at) VALUES
(2, 'PENDING', 299000, N'Nguy?n V?n A', '0912333444', N'Hà N?i', 'COD', GETDATE()),
(3, 'COMPLETED', 3500000, N'Tr?n Th? B', '0912555666', N'?à N?ng', 'BANK', GETDATE());

-- ORDER_ITEM
INSERT INTO order_item (order_id, product_id, qty, price) VALUES
(1, 1, 1, 299000),
(2, 6, 1, 3500000);

-- VOUCHER
INSERT INTO voucher (code, discount_amount, expiration_date) VALUES 
('SPORTY50', 50000, '2025-12-31'),
('VIP', 100000, '2025-12-31');

-- REVIEW
INSERT INTO review (user_id, product_id, rating, comment) VALUES
(2, 1, 5, N'Áo m?c mát l?m!'),
(3, 6, 4, N'Giày êm nh?ng giao h?i lâu.');

-- WISHLIST
INSERT INTO wishlist (user_id, product_id) VALUES (2, 6), (2, 7);

GO