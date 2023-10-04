/*display the lowest sale drink on a specific month */
SELECT drink, SUM(sales) AS lowestSales
FROM sales_data 
WHERE month = 7 AND year = 2024

GROUP BY drink
ORDER BY lowestSales ASC
LIMIT 1;