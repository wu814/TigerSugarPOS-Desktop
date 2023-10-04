/*Retrieve total sales on a specific day*/
SELECT year, month, day, SUM(Sales) AS total_sales
FROM sales_data
WHERE Month = 6
    AND Day = 1
    AND Year = 2024

GROUP BY year, month, day
ORDER BY year ASC, month ASC, day ASC;