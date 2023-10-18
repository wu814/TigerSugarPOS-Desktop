/* Display the number of drinks sold for each drink in descending order in a month*/
SELECT 
drink, 
EXTRACT(YEAR FROM order_timestamp) AS year_value,
EXTRACT(MONTH FROM order_timestamp) AS month_value,
COUNT(drink) AS total_drinks_sold
FROM orders, unnest(order_items) AS drink
WHERE EXTRACT(YEAR FROM order_timestamp) = '2024'
    AND EXTRACT(MONTH FROM order_timestamp) = '06'
GROUP BY drink, year_value, month_value
ORDER BY total_drinks_sold DESC;

