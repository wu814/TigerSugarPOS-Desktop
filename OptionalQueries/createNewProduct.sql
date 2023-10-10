/*Create a new row from products*/
INSERT INTO products (product_id, drink_name, price, ingredients)
VALUES (DEFAULT, 'new drink name', 6.25, ARRAY['ingredient1', 'ingredient2', 'ingredient3', 'ingredient4', 'ingredient5']);