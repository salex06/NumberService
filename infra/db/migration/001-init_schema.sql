CREATE TABLE users (
    id UUID             PRIMARY KEY,
    username            VARCHAR(64) UNIQUE NOT NULL,
    password_hash       VARCHAR(255) NOT NULL
);