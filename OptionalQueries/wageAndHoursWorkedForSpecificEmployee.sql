/*Display the wage and hours worked for a specific employee*/
SELECT first_name, last_name, wage, hours_worked
FROM employees
WHERE first_name = 'Tyson' AND last_name = 'Long';