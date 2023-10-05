CREATE TABLE employees (
    employee_id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    wage DECIMAL(5,2) NOT NULL,
    hours_worked INT NOT NULL,
    position VARCHAR(255) NOT NULL
);