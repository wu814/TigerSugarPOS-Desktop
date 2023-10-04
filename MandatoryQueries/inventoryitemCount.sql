/* Counting number of items in the inventory, offset by 5 due to year, month, year, and hour, week columns */ 
SELECT COUNT(*) - 5 AS column_count
FROM information_schema.columns
WHERE table_name = 'aggregated_inventory_data';