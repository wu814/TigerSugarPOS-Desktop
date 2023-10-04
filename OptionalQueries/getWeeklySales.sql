/*Display weekly sales*/

SELECT week, SUM(sales) AS total_sales
FROM sales_data
WHERE WEEK = 10

GROUP BY week;