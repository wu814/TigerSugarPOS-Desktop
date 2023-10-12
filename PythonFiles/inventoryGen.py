import csv

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
# Create a list of supplies based on your inventory dictionary
supplies = list(inventory.keys())

# Set a default stock value
default_stock = 10000

# Create a list of supply data with default stock values
supply_data = [{"supply": supply, "stock_remaining": default_stock} for supply in supplies]

# Define the output CSV file name
output_csv_file = "inventory.csv"

# Write the supply data to the CSV file
with open(output_csv_file, mode="w", newline="") as file:
    writer = csv.DictWriter(file, fieldnames=["supply", "stock_remaining"])
    # Write header row
    writer.writeheader()
    # Write supply data rows
    writer.writerows(supply_data)

print(f"Inventory data has been written to {output_csv_file}")
