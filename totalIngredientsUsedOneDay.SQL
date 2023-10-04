/*Find total used that day for one ingredient*/

SELECT SUM(taro) AS total_taro
FROM aggregated_inventory_data
WHERE year = 2024 AND month = 6 AND day = 3;


