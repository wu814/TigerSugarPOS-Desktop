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

start_date = datetime(2024, 6, 2, 12, 0)

end_date = datetime(2025, 6, 2, 9, 0)

total_sales = 0

# Sales Randomization VARIABLE
drink_sales_range = (8, 35)  # Adjust the range as needed (minimum, maximum)

# Initialize CSV file
with open("orders.csv", mode="w", newline="") as file:
    writer = csv.writer(file)
    writer.writerow(["order_timestamp", "employee_id", "customer_id", "order_items", "order_total"])

    current_week = 1

    # Generate sales data for each hour
    while start_date < end_date:
        current_date = start_date
        day_sales = 0

        for _ in range(0, 12):
            if current_date >= end_date:
                break

            min_sales, max_sales = drink_sales_range
            # if today is december 25th 2024, or february 3rd 2025, we modify as peak day
            num_sold = random.randint(min_sales, max_sales)  # Random orders within the range
            if current_date.strftime("%Y-%m-%d") == "2024-12-25" or current_date.strftime("%Y-%m-%d") == "2025-02-03":
                num_sold *= 2

            if num_sold > 0:
                for _ in range(num_sold):
                    
                    start_time = current_date.replace(hour=12, minute=0, second=0, microsecond=0)
                    end_time = current_date.replace(hour=21, minute=0, second=0, microsecond=0)

                    if current_date.strftime("%Y-%m-%d") == "2024-12-25" or current_date.strftime("%Y-%m-%d") == "2025-02-03":
                        current_date += timedelta(seconds = random.randint(30, 300))
                    else:
                        current_date += timedelta(seconds = random.randint(55, 550))  # Random time between orders (1-10 minutes)
                    if start_time <= current_date <= end_time:

                        rand_num_drinks = random.randint(1, 7)  # Random number of drinks per order (1-7)
                        sales = 0

                        drinks = "{"

                        for _ in range(rand_num_drinks):
                            drink = random.choice(list(drink_prices.keys()))

                            drinks += drink + ", "

                            sales += drink_prices[drink]

                        drinks = drinks[:-2] + "}"
                                                
                        sales = round(sales, 2)  # Calculate sales for a single drink and round to two decimal places
                        day_sales += sales
                        total_sales += sales  # Update total sales

                        # reformatting drinks so that it doesn't have , at the end
                        drinks = drinks[:-1] + "}"

                        employee = random.randint(1, 5)
                        customer = random.randint(1, 20)

                        # Write a row for each drink sold with a unique orderID
                        writer.writerow([current_date, employee, customer, drinks, sales])

                    else:
                        break


        day_end_date = current_date - timedelta(hours=24)
        # print(f"Sales for {day_end_date.strftime('%Y-%m-%d')} (Day): ${day_sales}")
        start_date += timedelta(days=1)
        start_date = start_date.replace(hour=12, minute=0, second=0, microsecond=0)

        # if (start_date.weekday()) == 5: # on Saturdays
        #     current_week += 1

# Print total sales
print(f"Total sales: ${total_sales}")
