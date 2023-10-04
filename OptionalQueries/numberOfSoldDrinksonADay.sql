/*Number of sold drinks on a specific day*/
SELECT year, month, day, SUM(number_of_drinks_sold) AS total_drinks_sold
FROM sales_data
WHERE Month = 6
    AND Day = 1
    AND Year = 2024

GROUP BY year, month, day;