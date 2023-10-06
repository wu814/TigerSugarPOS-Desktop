SELECT
    EXTRACT(WEEK FROM order_timestamp) AS week_number,
    COUNT(*) AS order_count
FROM
    orders
GROUP BY
    week_number
ORDER BY
    week_number;
