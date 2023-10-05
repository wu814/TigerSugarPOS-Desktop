CREATE TABLE inventory (
    inventory_id SERIAL PRIMARY KEY,
    supply VARCHAR(255) NOT NULL,
    stock_remaining INT NOT NULL
    CHECK (stock_remaining >= 0)
);