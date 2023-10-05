CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    order_timestamp TIMESTAMP NOT NULL,
    employee_id INT REFERENCES employees (employee_id) NOT NULL,
    customer_id INT REFERENCES customers (customer_id) NOT NULL,
    order_items TEXT[] NOT NULL,
    order_total DECIMAL(5,2) NOT NULL
);