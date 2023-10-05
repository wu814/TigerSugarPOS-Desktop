import csv
import random

# Preset list of customer names and email domains
customer_names = [
    "Alice Johnson",
    "Bob Smith",
    "Charlie Brown",
    "David White",
    "Emily Davis",
    "Frank Miller",
    "Grace Wilson",
    "Henry Taylor",
    "Ivy Lee",
    "Jack Harris",
    "Kelly Moore",
    "Liam Davis",
    "Mia Johnson",
    "Noah Wilson",
    "Olivia Smith",
    "Paul Miller",
    "Quinn Taylor",
    "Ryan White",
    "Sophia Brown",
    "Tyler Lee"
]

email_domains = ["gmail.com", "yahoo.com", "outlook.com", "example.com"]

num_customers = 20

customer_data = []

for customer_id, full_name in enumerate(customer_names, start=1):
    # Separate the full name into first name and last name
    first_name, last_name = full_name.split()

    email = f"{first_name.lower()}.{last_name.lower()}@{random.choice(email_domains)}"

    customer_data.append([first_name, last_name, email])

# Write the data to a CSV file
with open("customers.csv", mode="w", newline="") as file:
    writer = csv.writer(file)
    writer.writerow(["first_name", "last_name", "email"])
    writer.writerows(customer_data)

print("Customer data has been written to customers.csv")
