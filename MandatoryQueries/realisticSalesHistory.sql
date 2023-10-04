-- Retrieving realistic sales history by date, ordercount, and total sales
SELECT
    year,
    month,
    day,
    hour,
    SUM(number_of_drinks_sold) AS order_count,
    SUM(sales) AS total_sales
FROM
    sales_data
GROUP BY
    year,
    month,
    day,
    hour
ORDER BY
    year ASC,
    month ASC,
    day ASC,
    hour ASC;