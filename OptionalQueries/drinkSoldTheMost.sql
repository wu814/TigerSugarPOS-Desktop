/*Display drink sold the most*/

SELECT drink, SUM(number_of_drinks_sold) AS total_drinks_sold
FROM sales_data
GROUP BY drink
ORDER BY total_drinks_sold DESC
LIMIT 1;