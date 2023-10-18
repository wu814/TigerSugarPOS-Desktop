/*Display all order items from a specific customer id*/
SELECT customer_id, order_items
FROM orders
WHERE customer_id = 17;