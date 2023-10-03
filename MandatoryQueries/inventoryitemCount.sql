/* Counting number of items in the inventory, offset by 4 due to year, month, year, and hour columns */ 
SELECT COUNT(*) - 4 AS column_count
FROM information_schema.columns
WHERE table_name = 'aggregated_inventory_data'
OFFSET 4;