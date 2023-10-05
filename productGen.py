import csv

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

# Create a list to store the data
drink_data = []

for drink, price in drink_prices.items():
    ingredients_list = drink_to_ingredients[drink]
    # Join the list elements into a string with commas and curly braces
    ingredients_str = "{" + ", ".join(ingredients_list) + "}"
    drink_data.append([drink, price, ingredients_str])

output_csv_file = "products.csv"

with open(output_csv_file, mode="w", newline="") as file:
    writer = csv.writer(file)
    writer.writerow(["drink_name", "price", "ingredients"])
    writer.writerows(drink_data)

print(f"Data has been written to {output_csv_file}")
