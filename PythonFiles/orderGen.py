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

start_date = datetime(2022, 10, 2, 12, 0)
end_date = datetime(2023, 10, 2, 9, 0)
total_sales = 0

# Sales Randomization VARIABLE
drink_sales_range = (8, 35)

# Define options for drink attributes and addons
dairy_free_alternatives = ["None", "Oat", "Soy", "Lactose Free"]
sweetness_levels = ["50%", "100%"]
ice_levels = ["Less Ice", "Normal", "None"]
cup_sizes = ["Regular", "Regular Hot", "XL"]

addons_options = ["Added", "None"]

# Initialize CSV file
with open("C:/Users/arnel/OneDrive/Documents/GitHub/project-2-910_10g/csvFiles/orders_test.csv", mode="w", newline="") as file:
    writer = csv.writer(file)
    writer.writerow(["order_timestamp", "employee_id", "customer_id", "order_items", "order_total", "drink_attributes", "drink_addons"])

    current_week = 1

    # Generate sales data for each hour
    while start_date < end_date:
        current_date = start_date
        day_sales = 0

        for _ in range(0, 12):
            if current_date >= end_date:
                break

            min_sales, max_sales = drink_sales_range
            num_sold = random.randint(min_sales, max_sales)  # Random orders within the range
            if current_date.strftime("%Y-%m-%d") == "2022-12-25" or current_date.strftime("%Y-%m-%d") == "2023-02-03":
                num_sold *= 2

            if num_sold > 0:
                for _ in range(num_sold):
                    rand_num_drinks = random.randint(1, 3)  # Randomize between 1 to 3 drinks per order
                    sales = 0

                    drinks_list = []
                    drink_attributes_list = []
                    drink_addons_list = []

                    for _ in range(rand_num_drinks):
                        drink = random.choice(list(drink_prices.keys()))
                        drinks_list.append(drink)
                        sales += drink_prices[drink]

                        # Generate random drink attributes
                        drink_attributes = {
                            "Dairy Free Alternative": random.choice(dairy_free_alternatives),
                            "Sweetness Level": random.choice(sweetness_levels),
                            "Ice Level": random.choice(ice_levels),
                            "Cup Size": random.choice(cup_sizes),
                            "Special Instructions": "None"
                        }
                        drink_attributes_str = ", ".join([f"{key}: {value}" for key, value in drink_attributes.items()])
                        drink_attributes_list.append(drink_attributes_str)

                        # Generate random drink addons
                        drink_addons = {
                            "Extra Boba": random.choice(addons_options),
                            "Tiger Pearls": random.choice(addons_options),
                            "Cream Mousse": random.choice(addons_options),
                            "Taro": random.choice(addons_options),
                            "Red Bean": random.choice(addons_options),
                            "Pudding": random.choice(addons_options),
                            "Mochi": random.choice(addons_options)
                        }
                        drink_addons_str = ", ".join([f"{key}: {value}" for key, value in drink_addons.items()])
                        drink_addons_list.append(drink_addons_str)

                    drinks = "{" + ", ".join(drinks_list) + "}"
                    drink_attributes_str = "{" + ", ".join(['"' + attr + '"' for attr in drink_attributes_list]) + "}"
                    drink_addons_str = "{" + ", ".join([addon for addon in drink_addons_list]) + "}"


                    sales = round(sales, 2)
                    day_sales += sales
                    total_sales += sales

                    employee = random.randint(1, 5)
                    customer = random.randint(1, 20)

                    # Write a row for each drink sold with a unique orderID
                    writer.writerow([current_date, employee, customer, drinks, sales, drink_attributes_str, drink_addons_str])

            current_date = current_date + timedelta(hours=1)  # Move to next hour

        start_date = start_date + timedelta(days=1)  # Move to next day

# Print total sales
print(f"Total sales: ${total_sales}")
