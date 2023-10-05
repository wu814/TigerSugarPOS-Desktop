import csv

drink_to_ingredients = {
    "Classic Brown Sugar Boba Milk Tea": [
        ("Tapioca Pearls (Boba)"),
        ("Cups (Regular)"),
        ("Straws (Jumbo)"),
        ("Lids (Flat)"),
        ("Condiment Station Supplies"),
        ("Napkins (Regular)"),
        ("To-Go Bags (Small)"),
        ("Brown Sugar"),
        ("Fresh Milk")
    ],
    "Taro Bubble Tea": [
        ("Tapioca Pearls (Boba)"),
        ("Cups (Regular)"),
        ("Straws (Jumbo)"),
        ("Lids (Flat)"),
        ("Condiment Station Supplies"),
        ("Napkins (Regular)"),
        ("To-Go Bags (Small)"),
        ("Taro")
    ],
    "Matcha Black Sugar Boba Milk": [
        ("Cups (Regular)"),
        ("Straws (Regular)"),
        ("Lids (Flat)"),
        ("Condiment Station Supplies"),
        ("Napkins (Regular)"),
        ("To-Go Bags (Small)"),
        ("Matcha"),
        ("Black Sugar"),
        ("Fresh Milk")
    ],
    "Black Sugar Coffee Jelly": [
        ("Grass Jelly"),
        ("Cups (Regular)"),
        ("Straws (Regular)"),
        ("Lids (Flat)"),
        ("Condiment Station Supplies"),
        ("Napkins (Regular)"),
        ("To-Go Bags (Small)"),
        ("Black Sugar")
    ],
    "Strawberry Milk": [
        ("Cups (Regular)"),
        ("Straws (Regular)"),
        ("Lids (Dome)"),
        ("Condiment Station Supplies"),
        ("Napkins (Regular)"),
        ("To-Go Bags (Small)"),
        ("Fresh Milk"),
        ("Strawberry Milk Cream")
    ],
    "Tiger Mango Sago": [
        ("Cups (Regular)"),
        ("Straws (Regular)"),
        ("Lids (Flat)"),
        ("Napkins (Regular)"),
        ("To-Go Bags (Small)"),
        ("Sago"),
        ("Crystal Jelly"),
        ("Mango Milk Cream")
    ],
    "Passion Fruit Tea": [
        ("Cups (Regular)"),
        ("Straws (Regular)"),
        ("Lids (Flat)"),
        ("Napkins (Regular)"),
        ("To-Go Bags (Small)"),
        ("Lychee Jelly"),
        ("Passion Fruit Tea Leaves")
    ],
    "Golden Oolong Tea": [
        ("Cups (Regular)"),
        ("Straws (Regular)"),
        ("Lids (Flat)"),
        ("Napkins (Regular)"),
        ("To-Go Bags (Small)"),
        ("Aloe Vera Bits"),
        ("Oolong Tea Leaves")
    ],
    "Red Bean Matcha Milk": [
        ("Cups (Regular)"),
        ("Straws (Regular)"),
        ("Lids (Flat)"),
        ("Condiment Station Supplies"),
        ("Napkins (Regular)"),
        ("To-Go Bags (Small)"),
        ("Fresh Milk"),
        ("Red Beans"),
        ("Matcha")
    ],
    "Jasmine Green Tea": [
        ("Cups (Regular)"),
        ("Straws (Regular)"),
        ("Lids (Flat)"),
        ("Napkins (Regular)"),
        ("To-Go Bags (Small)"),
        ("Jasmine Green Tea Leaves")
    ]
}

# Initialize inventory
inventory = {
    "Tapioca Pearls (Boba)": 40000,
    "Lychee Jelly": 20000,
    "Aloe Vera Bits": 20000,
    "Grass Jelly": 20000,
    "Fresh Milk": 100000,
    "Red Beans": 20000,
    "Cups (Regular)": 170000,
    "Straws (Regular)": 130000,
    "Straws (Jumbo)": 40000,
    "Napkins (Regular)": 170000,
    "To-Go Bags (Small)": 170000,
    "Lids (Dome)": 20000,
    "Lids (Flat)": 200000,
    "Condiment Station Supplies": 100000,
    "Taro": 20000,
    "Matcha": 40000,
    "Brown Sugar": 20000,
    "Black Sugar": 40000,
    "Strawberry Milk Cream": 20000,
    "Mango Milk Cream": 20000,
    "Sago": 20000,
    "Crystal Jelly": 20000,
    "Jasmine Green Tea Leaves": 20000,
    "Passion Fruit Tea Leaves": 20000,
    "Oolong Tea Leaves": 20000
}

for key in inventory:
    inventory[key] = 160000

# Read orders from the CSV file
with open('orders.csv', 'r') as file:
    orders = csv.DictReader(file)

    # Process each order
    for order in orders:
        order_items = [item.strip('{} ') for item in order['order_items'].split(', ')]

        # Subtract ingredients for each drink in the order
        for drink in order_items:
            if drink in drink_to_ingredients:
                for ingredient in drink_to_ingredients[drink]:
                    if inventory[ingredient] > 0:
                        inventory[ingredient] -= 1
                    else:
                        print(f"Warning: Not enough {ingredient} for {drink}")

# Write the updated inventory to a new CSV file
with open('inventory.csv', 'w', newline='') as file:
    writer = csv.writer(file)

    # Write header
    writer.writerow(['supply', 'stock_remaining'])

    # Write inventory data
    for supply, stock_remaining in inventory.items():
        writer.writerow([supply, stock_remaining])
