/*display the lowest sale drink on a specific month */
SELECT year, month, drink, SUM(sales) AS lowest_sales
FROM sales_data 
WHERE month = 7 AND year = 2024

GROUP BY year, month, drink
ORDER BY lowestSales ASC
LIMIT 1;