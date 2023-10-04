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

start_date = datetime(2024, 6, 1, 0, 0)

end_date = datetime(2025, 6, 1, 0, 0)

total_sales = 0

# Sales Randomization VARIABLE
drink_sales_range = (35, 50)  # Adjust the range as needed (minimum, maximum)

# Initialize CSV file
with open("orders.csv", mode="w", newline="") as file:
    writer = csv.writer(file)
    writer.writerow(["orderID", "Year", "Month", "Day", "Hour", "Week", "Drink", "Price"])

    current_week = 1
    order_id = 1

    # Generate sales data for each hour
    while start_date < end_date:
        current_date = start_date
        day_sales = 0

        for _ in range(0, 12):
            if current_date >= end_date:
                break

            min_sales, max_sales = drink_sales_range
            num_sold = random.randint(min_sales, max_sales)  # Random sales within the range

            if num_sold > 0:
                for _ in range(num_sold):
                    drink = random.choice(list(drink_prices.keys()))  # Randomly choose a drink
                    price = drink_prices[drink]
                    sales = round(price, 2)  # Calculate sales for a single drink and round to two decimal places
                    day_sales += sales
                    total_sales += sales  # Update total sales

                    # Write a row for each drink sold with a unique orderID
                    writer.writerow([order_id, current_date.year, current_date.month, current_date.day, current_date.hour, current_week, drink, price])
                    order_id += 1

            current_date += timedelta(hours=1)

        day_end_date = current_date - timedelta(hours=24)
        print(f"Sales for {day_end_date.strftime('%Y-%m-%d')} (Day): ${day_sales}")
        start_date += timedelta(days=1)

        if (start_date.weekday()) == 5: # on Saturdays
            current_week += 1

# Print total sales
print(f"Total sales: ${total_sales}")
