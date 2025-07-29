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
    seller_id INT,
    month DATE,
    town VARCHAR(255),
    flat_type VARCHAR(255),
    block VARCHAR(50),
    street_name VARCHAR(255),
    storey_range VARCHAR(50),
    floor_area_sqm FLOAT,
    lease_commence_date INT,
    remaining_lease VARCHAR(255),
    flat_model VARCHAR(255),
    resale_price FLOAT,
    forecast_price FLOAT,
    status VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
