SELECT
    drink,
    SUM(number_of_drinks_sold) AS number_of_drinks_sold
FROM sales_data
WHERE year = 2024 AND month = 8 AND day = 14
AND drink IN ('Tiger Mango Sago', 'Red Bean Matcha Milk', 'Strawberry Milk', 'Classic Brown Sugar Boba Milk Tea', 'Taro Bubble Tea', 'Matcha Black Sugar Boba Milk', 'Passion Fruit Tea', 'Black Sugar Coffee Jelly', 'Jasmine Green Tea', 'Golden Oolong Tea')
GROUP BY drink;