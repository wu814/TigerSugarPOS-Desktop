/*Display email and customer id for a specific person given first name and last name*/
SELECT first_name, last_name, customer_id, email
FROM customers
WHERE first_name = 'Alice' AND last_name = 'Johnson';