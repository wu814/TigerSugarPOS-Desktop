/*Peak sale days in year*/
SELECT Month, Day, Year, SUM(Sales) AS total_sales
FROM sales_data
GROUP BY Month, Day, Year
ORDER BY total_sales DESC
LIMIT 10;