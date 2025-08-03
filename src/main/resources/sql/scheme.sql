use hdbPilot;

-- user table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255)  UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    avatar_url VARCHAR(255),
    bio TEXT,    -- 个人简介
    user_role     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- property table
CREATE TABLE IF NOT EXISTS property (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    listing_title VARCHAR(255),
    seller_id BIGINT,
    town VARCHAR(255),
    postal_code VARCHAR(10),
    bedroom_number INT,
    bathroom_number INT,
    block VARCHAR(50),
    street_name VARCHAR(255),
    storey VARCHAR(50),
    floor_area_sqm FLOAT,
    top_year INT,
    flat_model VARCHAR(255),
    resale_price FLOAT,
    forecast_price FLOAT,
    status VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- mockdata
INSERT INTO property (listing_title, seller_id, town, postal_code, bedroom_number, bathroom_number, block, street_name, storey, floor_area_sqm, top_year, flat_model, resale_price, forecast_price, status, created_at, updated_at) VALUES
('Ang Mo Kio 4-Room HDB Flat', 101, 'Ang Mo Kio', '560210', 4, 2, '210', 'Ang Mo Kio Ave 3', '07 TO 09', 92.0, 1986, 'New Generation', 550000.00, 560000.00, 'available', NOW(), NOW()),
('Bedok 5-Room HDB Flat', 102, 'Bedok', '460506', 5, 2, '506', 'Bedok North St 3', '04 TO 06', 121.0, 1980, 'Improved', 680000.00, 695000.00, 'available', NOW(), NOW()),
('Clementi 3-Room HDB Flat', 103, 'Clementi', '120372', 3, 1, '372', 'Clementi Ave 4', '10 TO 12', 67.0, 1982, 'Model A', 420000.00, 425000.00, 'sold', NOW(), NOW()),
('Jurong West Executive HDB', 104, 'Jurong West', '640987', 5, 3, '987', 'Jurong West St 93', '01 TO 03', 142.0, 1995, 'Maisonette', 810000.00, 820000.00, 'available', NOW(), NOW()),
('Tampines 4-Room HDB Flat', 105, 'Tampines', '520833', 4, 2, '833', 'Tampines St 83', '13 TO 15', 104.0, 1992, 'Model A', 610000.00, 615000.00, 'available', NOW(), NOW()),
('Yishun 5-Room HDB Flat', 106, 'Yishun', '760759', 5, 2, '759', 'Yishun St 72', '07 TO 09', 122.0, 1988, 'Improved', 595000.00, 600000.00, 'sold', NOW(), NOW()),
('Bishan 4-Room HDB Flat', 107, 'Bishan', '570191', 4, 2, '191', 'Bishan St 13', '19 TO 21', 84.0, 1987, 'Model A', 730000.00, 740000.00, 'available', NOW(), NOW()),
('Punggol 5-Room HDB Flat', 108, 'Punggol', '820673', 5, 2, '673A', 'Edgefield Plains', '16 TO 18', 112.0, 2015, 'Premium Apartment', 750000.00, 760000.00, 'available', NOW(), NOW()),
('Sengkang 4-Room HDB Flat', 109, 'Sengkang', '540291', 4, 2, '291B', 'Compassvale Street', '04 TO 06', 93.0, 2012, 'Model A', 580000.00, 585000.00, 'available', NOW(), NOW()),
('Toa Payoh 3-Room HDB Flat', 110, 'Toa Payoh', '310125', 3, 1, '125', 'Lorong 1 Toa Payoh', '07 TO 09', 68.0, 1972, 'Standard', 380000.00, 390000.00, 'sold', NOW(), NOW());

-- comment
CREATE TABLE IF NOT EXISTS comment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  rating INT NOT NULL,
  content TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE comment ADD COLUMN property_id BIGINT;



-- property_image table
CREATE TABLE IF NOT EXISTS property_image (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '图片主键',
    property_id BIGINT NOT NULL COMMENT '房源ID',
    image_url VARCHAR(1024) NOT NULL COMMENT '图片URL地址',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '房源图片表';