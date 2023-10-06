/*Showing realistic sales history by selecting order count and sales by hour of each day*/
SELECT
    DATE(order_timestamp) AS order_date,
    EXTRACT(HOUR FROM order_timestamp) AS hour,
    COUNT(*) AS order_count,
    SUM(order_total) AS total_sales
FROM
    orders
GROUP BY
    order_date,
    hour
ORDER BY
    order_date;