import csv
import random
from datetime import datetime, timedelta

# Dictionary of drinks and their prices
drink_prices = {
    "Classic Brown Sugar Boba Milk Tea": 6.25,
    "Taro Bubble Tea": 6.75,
    "Matcha Black Sugar Boba Milk": 6.75,
    "Black Sugar Coffee Jelly": 6.50,
    "Strawberry Milk": 8,
    "Tiger Mango Sago": 8,
    "Passion Fruit Tea": 5.50,
    "Golden Oolong Tea": 5.50,
    "Red Bean Matcha Milk": 6.75,
    "Jasmine Green Tea": 4.75,
}

# Total sales target for the year
total_sales_target = 1000000  # Might be unused

start_date = datetime(2023, 6, 1, 0, 0)  # Start date with hour component

end_date = datetime(2024, 6, 1, 0, 0)  # End date with hour component

# Initialize total sales variable
total_sales = 0

drinkMaxSales = 7

# Initialize CSV file
with open("C:/Users/arnel/OneDrive/Documents/GitHub/project-2-910_10g/csvFiles/sales_data.csv", mode="w", newline="") as file:
    writer = csv.writer(file)
    writer.writerow(["Year", "Month", "Day", "Hour", "Week", "Drink", "Number of Drinks Sold", "Sales ($)"])

    current_week = 1
    # Generate sales data for each hour
    while start_date < end_date:
        current_date = start_date
        day_sales = 0

        for _ in range(0, 12):
            if current_date >= end_date:
                break

            for drink, price in drink_prices.items():
                max_sales = min(drinkMaxSales, drinkMaxSales)  # Maximum sales for the hour per drink

                # Check for day exceptions (8/21/2024 and 1/16/2025)
                if current_date.date() == datetime(2023, 8, 21).date() or current_date.date() == datetime(2024, 1, 16).date():
                    num_sold = random.randint(5, max_sales * 5)  # Higher sales on exceptions
                else:
                    num_sold = random.randint(1, max_sales)  # Random number of drinks sold (1 to max_sales)

                sales = round(num_sold * price, 2)  # Calculate sales and round to two decimal places
                day_sales += sales
                total_sales += sales  # Update total sales
                total_sales_target -= sales

                writer.writerow([current_date.year, current_date.month, current_date.day, current_date.hour, current_week, drink, num_sold, sales])

            current_date += timedelta(hours=1)

        day_end_date = current_date - timedelta(hours=24)
        print(f"Sales for {day_end_date.strftime('%Y-%m-%d')} (Day): ${day_sales}")
        start_date += timedelta(days=1)

        if (start_date.weekday()) == 5: # on saturdays
            current_week += 1

# Print total sales
print(f"Total sales: ${total_sales}")
