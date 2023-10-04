SELECT
    year,
    month,
    day,
    hour,
    SUM(number_of_drinks_sold) AS orderCount,
    SUM(Sales) AS TotalSales
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