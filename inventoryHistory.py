import csv
from datetime import datetime

# Assuming starting inventory amounts for each item
inventory = {
    "Tapioca Pearls (Boba)": 0,
    "Lychee Jelly": 0,
    "Aloe Vera Bits": 0,
    "Grass Jelly": 0,
    "Fresh Milk": 0,
    "Red Beans": 0,
    "Cups (Regular)": 0,
    "Straws (Regular)": 0,
    "Straws (Jumbo)": 0,
    "Napkins (Regular)": 0,
    "To-Go Bags (Small)": 0,
    "Lids (Dome)": 0,
    "Lids (Flat)": 0,
    "Condiment Station Supplies": 0,
    "Taro": 0,
    "Matcha": 0,
    "Brown Sugar": 0,
    "Black Sugar": 0,
    "Strawberry Milk Cream": 0,
    "Mango Milk Cream": 0,
    "Sago": 0,
    "Crystal Jelly": 0,
    "Jasmine Green Tea Leaves": 0,
    "Passion Fruit Tea Leaves": 0,
    "Oolong Tea Leaves": 0
}

# Drink to inventory item mapping
drink_to_inventory = {
    "Classic Brown Sugar Boba Milk Tea": [
        ("Tapioca Pearls (Boba)", 1),
        ("Cups (Regular)", 1),
        ("Straws (Jumbo)", 1),
        ("Lids (Flat)", 1),
        ("Condiment Station Supplies", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Brown Sugar", 1),
        ("Fresh Milk", 1)
    ],
    "Taro Bubble Tea": [
        ("Tapioca Pearls (Boba)", 1),
        ("Cups (Regular)", 1),
        ("Straws (Jumbo)", 1),
        ("Lids (Flat)", 1),
        ("Condiment Station Supplies", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Taro", 1)
    ],
    "Matcha Black Sugar Boba Milk": [
        ("Cups (Regular)", 1),
        ("Straws (Regular)", 1),
        ("Lids (Flat)", 1),
        ("Condiment Station Supplies", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Matcha", 1),
        ("Black Sugar", 1),
        ("Fresh Milk", 1)
    ],
    "Black Sugar Coffee Jelly": [
        ("Grass Jelly", 1),
        ("Cups (Regular)", 1),
        ("Straws (Regular)", 1),
        ("Lids (Flat)", 1),
        ("Condiment Station Supplies", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Black Sugar", 1)
    ],
    "Strawberry Milk": [
        ("Cups (Regular)", 1),
        ("Straws (Regular)", 1),
        ("Lids (Dome)", 1),
        ("Condiment Station Supplies", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Fresh Milk", 1),
        ("Strawberry Milk Cream", 1)
    ],
    "Tiger Mango Sago": [
        ("Cups (Regular)", 1),
        ("Straws (Regular)", 1),
        ("Lids (Flat)", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Sago", 1),
        ("Crystal Jelly", 1),
        ("Mango Milk Cream", 1)
    ],
    "Passion Fruit Tea": [
        ("Cups (Regular)", 1),
        ("Straws (Regular)", 1),
        ("Lids (Flat)", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Lychee Jelly", 2),
        ("Passion Fruit Tea Leaves", 1)
    ],
    "Golden Oolong Tea": [
        ("Cups (Regular)", 1),
        ("Straws (Regular)", 1),
        ("Lids (Flat)", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Aloe Vera Bits", 1),
        ("Oolong Tea Leaves", 1)
    ],
    "Red Bean Matcha Milk": [
        ("Cups (Regular)", 1),
        ("Straws (Regular)", 1),
        ("Lids (Flat)", 1),
        ("Condiment Station Supplies", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Fresh Milk", 1),
        ("Red Beans", 1),
        ("Matcha", 1)
    ],
    "Jasmine Green Tea": [
        ("Cups (Regular)", 1),
        ("Straws (Regular)", 1),
        ("Lids (Flat)", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Jasmine Green Tea Leaves", 1)
    ]
}

aggregated_inventory_file = "aggregated_inventory_data.csv"

aggregated_inventory = {}

with open("sales_data.csv", mode="r") as file:
    reader = csv.reader(file)
    next(reader)

    for row in reader:
        year, month, day, hour = map(int, (row[0], row[1], row[2], row[3]))
        date_str = f"{year}-{month:02d}-{day:02d}-{hour:02d}"  # Include the hour component

        drink = row[4]
        num_sold = int(row[5])

        for item, quantity in drink_to_inventory[drink]:
            inventory[item] -= quantity * num_sold

        if date_str not in aggregated_inventory:
            aggregated_inventory[date_str] = {item: 0 for item in inventory.keys()}

        for item, quantity in drink_to_inventory[drink]:
            aggregated_inventory[date_str][item] += quantity * num_sold

with open(aggregated_inventory_file, mode="w", newline="") as inventory_file:
    writer = csv.writer(inventory_file)
    header = ["Year", "Month", "Day", "Hour"] + list(inventory.keys())  # Update header
    writer.writerow(header)

    for date, data in aggregated_inventory.items():
        year, month, day, hour = map(int, date.split('-'))
        row = [year, month, day, hour] + [data[item] for item in inventory.keys()]
        writer.writerow(row)

# Print remaining inventory
for item, quantity in inventory.items():
    print(f"{item}: {quantity}")
