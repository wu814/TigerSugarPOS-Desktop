CREATE TABLE sales_data (
    year INT,
    month INT,
    day INT,
    hour INT,
    WEEK INT,
    drink VARCHAR(255),
    number_of_drinks_sold INT,
    sales FLOAT,
    PRIMARY KEY (year, month, day, hour, week, drink)
);