CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(128) UNIQUE NOT NULL,
    encoded_password VARCHAR(32) NOT NULL,
    name VARCHAR(128) NOT NULL
);