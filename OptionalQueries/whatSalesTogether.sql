/* displays what sells together */
WITH OrderItems AS (
    SELECT DISTINCT
        unnest(order_items) AS item,
        order_timestamp
    FROM orders WHERE order_timestamp BETWEEN '2023-10-15' AND '2023-10-17'
)
SELECT
    a.item AS item1,
    b.item AS item2,
    COUNT(*) AS frequency
FROM
    OrderItems a
JOIN
    OrderItems b ON a.order_timestamp = b.order_timestamp AND a.item < b.item
GROUP BY
    item1, item2
ORDER BY
    frequency DESC;