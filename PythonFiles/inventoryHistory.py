import csv
from datetime import datetime
import json 
import re
# Assuming starting inventory amounts for each item
inventory = {
    "Sago": 0,
    "Cups (XL)": 0,
    "Cups (Regular Hot)": 0,
    "Grass Jelly": 0,
    "Crystal Jelly": 0,
    "Mango Milk Cream": 0,
    "Black Sugars": 0,
    "Aloe Vera Bits": 0,
    "Straws (Jumbo)": 0,
    "Brown Sugar": 0,
    "Black Sugar": 0,
    "Lids (Dome)": 0,
    "Strawberry Milk Cream": 0,
    "Condiment Station Supplies": 0,
    "Matcha": 0,
    "Fresh Milk": 0,
    "Tapioca Pearls (Boba)": 0,
    "Tiger Pearls": 0,
    "Cream Mousse": 0,
    "Taro": 0,
    "Red Beans": 0,
    "Pudding": 0,
    "Mochi": 0,
    "Jasmine Green Tea Leaves": 0,
    "Passion Fruit Tea Leaves": 0,
    "Lychee Jelly": 0,
    "Oat Milk": 0,
    "Strawberry Mango": 0,
    "Oolong Tea Leaves": 0,
    "Straws (Regular)": 0,
    "Lids (Flat)": 0,
    "Napkins (Regular)": 0,
    "To-Go Bags (Small)": 0,
    "Cups (Regular)": 0,
    "Soy Milk": 0,
    "Lactose Free Milk": 0
}

drink_to_inventory = {
    "Classic Brown Sugar Boba Milk Tea": [
        ("Tapioca Pearls (Boba)", 1),
        ("Straws (Jumbo)", 1),
        ("Lids (Flat)", 1),
        ("Condiment Station Supplies", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Black Sugar", 1)
    ],
    "Taro Bubble Tea": [
        ("Tapioca Pearls (Boba)", 1),
        ("Straws (Jumbo)", 1),
        ("Lids (Flat)", 1),
        ("Condiment Station Supplies", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Taro", 1)
    ],
    "Matcha Black Sugar Boba Milk": [
        ("Straws (Regular)", 1),
        ("Lids (Flat)", 1),
        ("Condiment Station Supplies", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Matcha", 1),
        ("Black Sugar", 1)
    ],
    "Black Sugar Coffee Jelly": [
        ("Grass Jelly", 1),
        ("Straws (Regular)", 1),
        ("Lids (Flat)", 1),
        ("Condiment Station Supplies", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Black Sugar", 1)
    ],
    "Strawberry Milk": [
        ("Straws (Regular)", 1),
        ("Lids (Dome)", 1),
        ("Condiment Station Supplies", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Strawberry Milk Cream", 1)
    ],
    "Tiger Mango Sago": [
        ("Straws (Regular)", 1),
        ("Lids (Flat)", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Sago", 1),
        ("Crystal Jelly", 1),
        ("Mango Milk Cream", 1)
    ],
    "Passion Fruit Tea": [
        ("Straws (Regular)", 1),
        ("Lids (Flat)", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Lychee Jelly", 1),
        ("Passion Fruit Tea Leaves", 1)
    ],
    "Golden Oolong Tea": [
        ("Straws (Regular)", 1),
        ("Lids (Flat)", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Aloe Vera Bits", 1),
        ("Oolong Tea Leaves", 1)
    ],
    "Red Bean Matcha Milk": [
        ("Straws (Regular)", 1),
        ("Lids (Flat)", 1),
        ("Condiment Station Supplies", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Red Beans", 1),
        ("Matcha", 1)
    ],
    "Jasmine Green Tea": [
        ("Straws (Regular)", 1),
        ("Lids (Flat)", 1),
        ("Napkins (Regular)", 1),
        ("To-Go Bags (Small)", 1),
        ("Jasmine Green Tea Leaves", 1)
    ]
}


# Create a dictionary to store the updated inventory history
inventory_history = {}

# def parse_attributes(input_string):
#     # Split the string into key-value pairs
#     pairs = [pair.strip() for pair in input_string.split(',')]

#     # Create a dictionary from the key-value pairs
#     data = dict(pair.split(':') for pair in pairs)

#     # Retrieve values for the specified keys
#     dairy_free_alternative = data.get('Dairy Free Alternative', 'Not specified')
#     cup_size = data.get('Cup Size', 'Not specified')

#     return dairy_free_alternative, cup_size

def parse_attributes(input_string):
    # Split the string into key-value pairs
    pairs = [pair.strip() for pair in input_string.split(',')]

    # Create a dictionary from the key-value pairs
    data = dict(pair.split(':') for pair in pairs)

    # Retrieve values for the specified keys
    dairy_free_alternative = data.get('Dairy Free Alternative', 'Not specified')
    cup_size = data.get('Cup Size', 'Not specified')
    drink_addons = data.get('Drink Addons', 'None')  # New line to get drink addons

    return dairy_free_alternative, cup_size, drink_addons  # Updated return statement


with open('../csvFiles/orders_test.csv', mode='r') as orders_file:
    reader = csv.reader(orders_file)
    next(reader)  # Skip header
    counter =0
    for row in reader:
        counter += 1
        timestamp_str, _, _, order_items_str, _, drink_attributes_str, drink_addons_str = row
        timestamp = datetime.strptime(timestamp_str, "%Y-%m-%d %H:%M:%S")

        # Split the order items into a list
        order_items = [item.strip() for item in order_items_str.strip('{}').split(',')]

        # Initialize a dictionary to store the used inventory items and quantities for this order
        used_inventory = {item: 0 for item in inventory}

        # Parse the drink attributes
        
        segments_attributes = drink_attributes_str.split('", ')
        segments_addons = drink_addons_str.split('", ')
        for index, attribute_str in enumerate(segments_attributes):
            attribute_str += '"'
            
            if index == 0:
                attribute_str = attribute_str[2:-1]
            elif index == len(segments_attributes) - 1:
                attribute_str = attribute_str[1:-2]
            else:
                attribute_str = attribute_str[1:-1]
            

            parsed_attributes = parse_attributes(attribute_str)
            parsed_addons = parse_attributes(segments_addons[index])
            
            # Determine which ingredients to use based on attributes
            dairy_alternative, cup_size, drink_addons  = parsed_attributes[0], parsed_attributes[1], parsed_addons

            if dairy_alternative.lower().strip() == 'none':
                used_inventory['Fresh Milk'] += 1
            elif dairy_alternative.lower().strip() == 'lactose free milk':
                used_inventory['Lactose Free Milk'] += 1
            elif dairy_alternative.lower().strip() == 'oat milk':
                used_inventory['Oat Milk'] += 1
            elif dairy_alternative.lower().strip() == 'soy milk':
                used_inventory['Soy Milk'] += 1

            #print(cup_size.lower().strip())
            if cup_size.lower().strip() == 'regular':
                used_inventory['Cups (Regular)'] += 1 
            elif cup_size.lower().strip() == 'xl':
                used_inventory['Cups (XL)'] += 1
            elif cup_size.lower().strip() == 'regular hot':
                used_inventory['Cups (Regular Hot)'] += 1

            var = attribute_str
        # print(used_inventory)
        for item in order_items:
            # Check if the item is in the drink_to_inventory mapping
            if item in drink_to_inventory:
                # Get the list of ingredients for this item
                ingredients = drink_to_inventory[item]

                # Update the used_inventory dictionary with the quantities
                for ingredient, quantity in ingredients:
                    used_inventory[ingredient] += quantity

        # Store the updated inventory for this timestamp
        timestamp_key = timestamp.strftime("%Y-%m-%d %H:%M:%S")
        if timestamp_key not in inventory_history:
            inventory_history[timestamp_key] = used_inventory


        # Print the used inventory for this timestamp
        # if (counter == 4):
        #     break
# Write the updated inventory history to 'inventory_history.csv' file
with open('../csvFiles/inventory_history.csv', mode='w', newline='') as history_file:
    fieldnames = ["order_timestamp"] + list(inventory.keys())
    writer = csv.DictWriter(history_file, fieldnames=fieldnames)
    writer.writeheader()

    for timestamp, inventory_data in inventory_history.items():
        row_data = {"order_timestamp": timestamp}
        row_data.update(inventory_data)
        writer.writerow(row_data)

# print(f"Timestamp: {timestamp_str}")
# for item, quantity in used_inventory.items():
#     if quantity > 0:
#         print(f"{item}: {quantity}")

print("Inventory history updated and saved to 'inventory_history.csv'.")