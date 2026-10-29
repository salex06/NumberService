CREATE TABLE IF NOT EXISTS user_profiles (
    user_id             UUID PRIMARY KEY,
    name                VARCHAR(64) NOT NULL,
    surname             VARCHAR(64) NOT NULL,

    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);