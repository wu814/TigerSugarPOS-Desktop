-- This wlil select fifty two weeks of sales history (count of orders grouped by week)

SELECT week, SUM(number_of_drinks_sold) AS orderCount
FROM sales_data
GROUP BY week
ORDER BY week;