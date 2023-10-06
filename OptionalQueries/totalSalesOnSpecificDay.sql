/*Retrieve total sales on a specific day*/
SELECT DATE(order_timestamp), SUM(order_total) AS total_sales
FROM orders
WHERE DATE(order_timestamp) = '2024-06-02'
GROUP BY DATE(order_timestamp);