/* Display the decending order of the sales of drinks in a specific month*/
SELECT month, drink, SUM(sales) AS total_revenue
FROM sales_data
WHERE month = 7 
    And year = 2024 
GROUP BY month, drink
;
