/*Displays which day in the month had the most of a specific item used*/

SELECT year, month, day, SUM(Lychee_Jelly) AS Total
FROM aggregated_inventory_data
WHERE year = 2024 AND month = 6
GROUP BY year, month, day
ORDER BY Total DESC
LIMIT 1;