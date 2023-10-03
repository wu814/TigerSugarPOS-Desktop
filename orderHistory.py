import csv
import random
from datetime import date, timedelta

# Dictionary of drinks and their prices
drink_prices = {
    "Classic Brown Sugar Boba Milk Tea": 6.25,
    "Taro Bubble Tea": 6.75,
    "Matcha Black Sugar": 6.75,
    "Black Sugar Coffee Jelly": 6.50,
    "Strawberry Milk": 8,
    "Mango Sago": 8,
    "Passion Fruit Tea": 5.50,
    "Golden Oolong Tea": 5.50,
    "Red Bean Matcha Milk": 6.75,
    "Jasmine Green Tea": 4.75,
}

# Total sales target for the year
total_sales_target = 1000000 # Might be unused

start_date = date(2024, 6, 1)

end_date = date(2025, 6, 1)

# Initialize total sales variable
total_sales = 0

drinkMaxSales = 100

# Initialize CSV file
with open("sales_data.csv", mode="w", newline="") as file:
    writer = csv.writer(file)
    writer.writerow(["Month", "Day", "Year", "Drink", "Number of Drinks Sold", "Sales ($)"])

    # Generate sales data for each day
    while start_date < end_date:
        current_date = start_date
        week_sales = 0

        for _ in range(7):
            if current_date >= end_date:
                break

            for drink, price in drink_prices.items():
                max_sales = min(drinkMaxSales, drinkMaxSales)  # Maximum sales for the day per drink

                # Check for day exceptions (8/21/2024 and 1/16/2025)
                if current_date == date(2024, 8, 21) or current_date == date(2025, 1, 16):
                    num_sold = random.randint(5, max_sales*5)  # Higher sales on exceptions
                else:
                    num_sold = random.randint(1, max_sales)  # Random number of drinks sold (1 to max_sales)

                sales = round(num_sold * price, 2)  # Calculate sales and round to two decimal places
                week_sales += sales
                total_sales += sales  # Update total sales
                total_sales_target -= sales

                writer.writerow([current_date.month, current_date.day, current_date.year, drink, num_sold, sales])

            current_date += timedelta(days=1)

        week_end_date = current_date - timedelta(days=7)
        print(f"Sales for week starting {week_end_date.month}/{week_end_date.day}/{week_end_date.year} to {current_date.month}/{current_date.day}/{current_date.year}: ${week_sales}")
        start_date += timedelta(days=7)

# Print total sales
print(f"Total sales: ${total_sales}")
