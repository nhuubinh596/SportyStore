CREATE DATABASE sportystore COLLATE Vietnamese_CI_AS;
GO

USE sportystore;
GO

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

CREATE TABLE user_account (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(100),
    email NVARCHAR(100),
    phone NVARCHAR(20),
    created_at DATETIME2 DEFAULT GETDATE()
);

CREATE TABLE role (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user_account(id),
    FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE category (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL UNIQUE
);

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

CREATE TABLE voucher (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    discount_amount DECIMAL(12,2) NOT NULL,
    expiration_date DATETIME2
);

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
    product_id BIGINT NOT NULL, -- Bắt buộc tên là product_id
    qty INT NOT NULL DEFAULT 1,
    price DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE wishlist (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL, -- Bắt buộc tên là product_id
    created_at DATETIME2 DEFAULT GETDATE(),
    UNIQUE(user_id, product_id), -- Chặn trùng lặp (1 người ko like 2 lần 1 món)
    FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

INSERT INTO role (name) VALUES ('ROLE_ADMIN'), ('ROLE_USER');

INSERT INTO user_account (username, password, full_name, email, phone) VALUES 
('admin', '123', N'Quản Trị Viên', 'admin@sporty.com', '0909111222'),
('user1', '123', N'Nguyễn Văn A', 'user1@gmail.com', '0912333444'),
('user2', '123', N'Trần Thị B', 'user2@gmail.com', '0912555666');

INSERT INTO user_role (user_id, role_id) VALUES (1, 1), (1, 2), (2, 2), (3, 2);

INSERT INTO category (name) VALUES 
(N'Áo thun'), (N'Quần short'), (N'Giày thể thao'), (N'Phụ kiện'), (N'Đồ tập Gym');

INSERT INTO product (name, price, sale_price, category_id, image_url, description) VALUES
(N'Áo Thun DryFit Cool', 299000, 250000, 1, 'https://placehold.co/300x300?text=Ao+Thun', N'Thoáng mát.'),
(N'Áo Polo Thể Thao', 450000, NULL, 1, 'https://placehold.co/300x300?text=Polo', N'Lịch sự.'),
(N'Áo Tanktop Gym', 150000, 120000, 1, 'https://placehold.co/300x300?text=Tanktop', N'Khoe cơ bắp.'),
(N'Quần Short 2 Lớp', 220000, NULL, 2, 'https://placehold.co/300x300?text=Short', N'Có lớp lót.'),
(N'Quần Jogger Thun', 320000, 280000, 2, 'https://placehold.co/300x300?text=Jogger', N'Năng động.'),
(N'Giày Nike Pegasus', 3500000, 2990000, 3, 'https://placehold.co/300x300?text=Nike', N'Chạy bộ.'),
(N'Giày Adidas Ultra', 4200000, NULL, 3, 'https://placehold.co/300x300?text=Das', N'Êm ái.'),
(N'Giày Bitis Hunter', 890000, 790000, 3, 'https://placehold.co/300x300?text=Bitis', N'Hàng Việt.'),
(N'Bình Nước 1L', 90000, NULL, 4, 'https://placehold.co/300x300?text=Binh', N'Nhựa an toàn.'),
(N'Găng Tay Thủ Môn', 300000, NULL, 4, 'https://placehold.co/300x300?text=Gang', N'Bắt dính.'),
(N'Túi Trống Gym', 350000, 299000, 4, 'https://placehold.co/300x300?text=Tui', N'Đựng đồ.'),
(N'Thảm Yoga', 199000, NULL, 5, 'https://placehold.co/300x300?text=Tham', N'Chống trượt.'),
(N'Dây Nhảy', 120000, NULL, 5, 'https://placehold.co/300x300?text=Day', N'Tốc độ cao.'),
(N'Đai Lưng Cứng', 450000, 400000, 5, 'https://placehold.co/300x300?text=Dai', N'Hỗ trợ lưng.'),
(N'Áo Khoác Gió', 550000, NULL, 1, 'https://placehold.co/300x300?text=Khoac', N'Chống nước.');

INSERT INTO orders (user_id, status, total_amount, full_name, phone, address, payment_method, created_at) VALUES
(2, 'PENDING', 299000, N'Nguyễn Văn A', '0912333444', N'Hà Nội', 'COD', GETDATE()),
(3, 'COMPLETED', 3500000, N'Trần Thị B', '0912555666', N'Đà Nẵng', 'BANK', GETDATE());

INSERT INTO order_item (order_id, product_id, qty, price) VALUES
(1, 1, 1, 299000),
(2, 6, 1, 3500000);

INSERT INTO voucher (code, discount_amount, expiration_date) VALUES 
('SPORTY50', 50000, '2025-12-31'),
('VIP', 100000, '2025-12-31');

INSERT INTO review (user_id, product_id, rating, comment) VALUES
(2, 1, 5, N'Áo mặc mát lắm!'),
(3, 6, 4, N'Giày êm nhưng giao hơi lâu.');

INSERT INTO wishlist (user_id, product_id) VALUES (2, 6), (2, 7);

GO