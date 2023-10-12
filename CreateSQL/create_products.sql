CREATE TABLE products (
    product_id SERIAL PRIMARY KEY,
    drink_name VARCHAR(255) NOT NULL,
    price DECIMAL(3,2) NOT NULL,
    ingredients TEXT[] NOT NULL
);  