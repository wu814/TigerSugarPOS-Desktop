import csv
import random

num_employees = 5

employee_data = []

employee_names = [
    "Doby Lanete",
    "Josh Hare",
    "Tyson Long",
    "William Wu",
    "Christopher Vu"
]


employee_data = []
for employee_id, full_name in enumerate(employee_names, start=1):
    wage = round(random.uniform(10.0, 30.0), 2)  # Random wage between 10.00 and 30.00
    hours_worked = random.randint(20, 40)  # Random hours worked between 20 and 40
    positions = ["Manager", "Sales Associate", "Customer Service", "Assistant"]
    position = random.choice(positions)  # Randomly select a position

    # Separate the full name into first name and last name
    first_name, last_name = full_name.split()

    employee_data.append([first_name, last_name, wage, hours_worked, position])

# Write the data to a CSV file
with open("employees.csv", mode="w", newline="") as file:
    writer = csv.writer(file)
    writer.writerow(["first_name", "last_name", "wage", "hours_worked", "position"])
    writer.writerows(employee_data)

print("Employee data has been written to employees.csv")
