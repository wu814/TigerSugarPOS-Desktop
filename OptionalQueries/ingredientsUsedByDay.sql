-- This query gets the total use of all ingredients for a given day
SELECT
    year,
    month,
    day,
    SUM(Tapioca_Pearls_Boba) AS Total_Tapioca_Pearls_Boba,
    SUM(Lychee_Jelly) AS Total_Lychee_Jelly,
    SUM(Aloe_Vera_Bits) AS Total_Aloe_Vera_Bits,
    SUM(Grass_Jelly) AS Total_Grass_Jelly,
    SUM(Fresh_Milk) AS Total_Fresh_Milk,
    SUM(Red_Beans) AS Total_Red_Beans,
    SUM(Cups_Regular) AS Total_Cups_Regular,
    SUM(Straws_Regular) AS Total_Straws_Regular,
    SUM(Straws_Jumbo) AS Total_Straws_Jumbo,
    SUM(Napkins_Regular) AS Total_Napkins_Regular,
    SUM(To_Go_Bags_Small) AS Total_To_Go_Bags_Small,
    SUM(Lids_Dome) AS Total_Lids_Dome,
    SUM(Lids_Flat) AS Total_Lids_Flat,
    SUM(Condiment_Station_Supplies) AS Total_Condiment_Station_Supplies,
    SUM(Taro) AS Total_Taro,
    SUM(Matcha) AS Total_Matcha,
    SUM(Brown_Sugar) AS Total_Brown_Sugar,
    SUM(Black_Sugar) AS Total_Black_Sugar,
    SUM(Strawberry_Milk_Cream) AS Total_Strawberry_Milk_Cream,
    SUM(Mango_Milk_Cream) AS Total_Mango_Milk_Cream,
    SUM(Sago) AS Total_Sago,
    SUM(Crystal_Jelly) AS Total_Crystal_Jelly,
    SUM(Jasmine_Green_Tea_Leaves) AS Total_Jasmine_Green_Tea_Leaves,
    SUM(Passion_Fruit_Tea_Leaves) AS Total_Passion_Fruit_Tea_Leaves,
    SUM(Oolong_Tea_Leaves) AS Total_Oolong_Tea_Leaves
FROM aggregated_inventory_data
WHERE year = 2024 AND month = 06 AND day = 05
GROUP BY year, month, day;