/*Peak sale days in year*/
SELECT DATE(order_timestamp), SUM(order_total) AS total_sales
FROM orders
GROUP BY DATE(order_timestamp)
ORDER BY total_sales DESC
LIMIT 10;