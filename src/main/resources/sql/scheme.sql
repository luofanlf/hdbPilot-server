use hdbPilot;

--user table
CREATE TABLE if not exists users (
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

--property table
CREATE TABLE property (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    seller_id BIGINT,
    month DATE,
    town VARCHAR(255),
    flat_type VARCHAR(255),
    block VARCHAR(50),
    street_name VARCHAR(255),
    storey_range VARCHAR(50),
    floor_area_sqm FLOAT,
    lease_commence_date INT,
    flat_model VARCHAR(255),
    resale_price FLOAT,
    forecast_price FLOAT,
    status VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

--mockdata
INSERT INTO property (seller_id, month, town, flat_type, block, street_name, storey_range, floor_area_sqm, lease_commence_date,  flat_model, resale_price, forecast_price, status, created_at, updated_at) VALUES
(101, '2024-05-01', 'Ang Mo Kio', '4 ROOM', '210', 'Ang Mo Kio Ave 3', '07 TO 09', 92.0, 1986,  'New Generation', 550000.00, 560000.00, 'available', NOW(), NOW()),
(102, '2024-05-01', 'Bedok', '5 ROOM', '506', 'Bedok North St 3', '04 TO 06', 121.0, 1980,  'Improved', 680000.00, 695000.00, 'available', NOW(), NOW()),
(103, '2024-06-01', 'Clementi', '3 ROOM', '372', 'Clementi Ave 4', '10 TO 12', 67.0, 1982,  'Model A', 420000.00, 425000.00, 'sold', NOW(), NOW()),
(104, '2024-06-01', 'Jurong West', 'EXECUTIVE', '987', 'Jurong West St 93', '01 TO 03', 142.0, 1995,  'Maisonette', 810000.00, 820000.00, 'available', NOW(), NOW()),
(105, '2024-07-01', 'Tampines', '4 ROOM', '833', 'Tampines St 83', '13 TO 15', 104.0, 1992,  'Model A', 610000.00, 615000.00, 'available', NOW(), NOW()),
(106, '2024-07-01', 'Yishun', '5 ROOM', '759', 'Yishun St 72', '07 TO 09', 122.0, 1988,  'Improved', 595000.00, 600000.00, 'sold', NOW(), NOW()),
(107, '2024-08-01', 'Bishan', '4 ROOM', '191', 'Bishan St 13', '19 TO 21', 84.0, 1987,  'Model A', 730000.00, 740000.00, 'available', NOW(), NOW()),
(108, '2024-08-01', 'Punggol', '5 ROOM', '673A', 'Edgefield Plains', '16 TO 18', 112.0, 2015,  'Premium Apartment', 750000.00, 760000.00, 'available', NOW(), NOW()),
(109, '2024-09-01', 'Sengkang', '4 ROOM', '291B', 'Compassvale Street', '04 TO 06', 93.0, 2012,  'Model A', 580000.00, 585000.00, 'available', NOW(), NOW()),
(110, '2024-09-01', 'Toa Payoh', '3 ROOM', '125', 'Lorong 1 Toa Payoh', '07 TO 09', 68.0, 1972,  'Standard', 380000.00, 390000.00, 'sold', NOW(), NOW());