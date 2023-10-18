/*Number of sold drinks on a specific day*/
SELECT DATE(order_timestamp), COUNT(*) AS total_drinks_sold
FROM orders, unnest(order_items) AS drink
WHERE DATE(order_timestamp) = '2024-06-02'
GROUP BY DATE(order_timestamp);
