/*Display the list of inventory items whose current inventory is less than the inventory item's minimum amount*/
SELECT supply, stock_remaining, minimum_stock FROM inventory
WHERE stock_remaining < minimum_stock;

