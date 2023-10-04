/*Display weekly sales*/

SELECT SUM(sales) AS total_sales
FROM sales_data
WHERE WEEK = 1 AND month = 6 AND year = 2024;

