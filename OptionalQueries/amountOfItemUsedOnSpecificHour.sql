/*Display the amount used for a specific item during a specific hour*/
SELECT year, month, day, hour, red_beans
FROM aggregated_inventory_data
WHERE month = 6
    AND year = 2024
    AND day = 1
    AND hour = 0
;

