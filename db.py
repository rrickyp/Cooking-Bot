import sqlite3

# Create a connection to the SQLite database
conn = sqlite3.connect('food_database.db')

# Create a cursor object to interact with the database
cursor = conn.cursor()

# Create the 'foods' table if it doesn't exist
cursor.execute('''
    CREATE TABLE IF NOT EXISTS foods (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        food_name TEXT NOT NULL,
        ingredients TEXT NOT NULL,
        total_ingredients INTEGER NOT NULL,
        serving_size INTEGER NOT NULL,
        preparation_time INTEGER NOT NULL,
        cooking_time INTEGER NOT NULL,
        how_to_cook TEXT NOT NULL,
        image_path TEXT
    )
''')

# Insert food entries
food_data = [
    ('Pizza', '300g Dough, 200g Tomato Sauce, 200g Cheese, Toppings', 4, 4, 30, 20, '1. Prepare the pizza dough, 2. Add tomato sauce and toppings, 3. Bake in the oven.', 'app/src/main/res/drawable/pizza.jpg'),
    ('Noodle', '200g Noodles, Vegetables, 300g Meat, Sauce', 5, 2, 15, 10, '1. Boil the noodles, 2. Cook the vegetables and meat, 3. Mix with sauce.', 'app/src/main/res/drawable/noodle.jpg'),
    ('Rice Cake', '250g Rice Flour, 100g Sugar, Toppings', 3, 6, 40, 30, '1. Mix rice flour and sugar, 2. Steam the mixture, 3. Add toppings.', 'app/src/main/res/drawable/ricecake.jpg'),
    ('Burger', 'Bun, Patty, Cheese, Lettuce, Tomato, Pickle, Pepper, Egg', 8, 1, 25, 15, '1. Cook the patty, 2. Assemble the burger with bun, patty, and toppings.', 'app/src/main/res/drawable/burger.jpg'),
    ('Sushi', 'Rice, Fish, Seaweed, Vegetables', 4, 8, 35, 25, '1. Cook rice and season with vinegar, 2. Prepare fish, seaweed, and vegetables, 3. Roll ingredients in seaweed.', 'app/src/main/res/drawable/sushi.jpg'),
    ('Pasta', '250g Spaghetti, Tomato Sauce, Garlic, Olive Oil, Parmesan Cheese', 5, 4, 20, 15, '1. Boil the pasta, 2. Prepare the tomato sauce with garlic and olive oil, 3. Mix the cooked pasta with the sauce, 4. Serve with grated Parmesan cheese.', 'app/src/main/res/drawable/pasta.jpg'),
    ('Salad', 'Lettuce, Tomatoes, Cucumbers, Onions, Dressing', 4, 2, 10, 7, '1. Wash and chop the lettuce, tomatoes, cucumbers, and onions, 2. Toss the vegetables with dressing, 3. Serve chilled.', 'app/src/main/res/drawable/salad.jpg'),
    ('Stir-Fry', '300g Chicken, Bell Peppers, Broccoli, Soy Sauce', 3, 4, 25, 15, '1. Cook chicken and set aside, 2. Stir-fry bell peppers and broccoli, 3. Add chicken and soy sauce, 4. Serve hot.', 'app/src/main/res/drawable/stirfry.jpg'),
    ('Omelette', 'Eggs, Veggies, Cheese, Salt, Pepper', 2, 3, 10, 5, '1. Beat eggs with salt and pepper, 2. Cook veggies, 3. Pour eggs over veggies, 4. Sprinkle cheese, 5. Cook until set.', 'app/src/main/res/drawable/omelette.jpg'),
    ('Pancakes', 'Flour, Milk, Eggs, Sugar, Baking Powder, Butter', 2, 2, 15, 10, '1. Mix flour, milk, eggs, sugar, baking powder, 2. Cook on a griddle with butter, 3. Serve with desired toppings.', 'app/src/main/res/drawable/pancakes.jpg'),
    ('Steak', '500g Steak, Salt, Pepper, Olive Oil, Butter', 4, 8, 25, 15, '1. Season steak with salt and pepper, 2. Heat olive oil in a pan, 3. Sear steak on both sides, 4. Add butter and baste, 5. Cook to desired doneness.', 'app/src/main/res/drawable/steak.jpg'),
    ('Soup', '500g Chicken Broth, Vegetables, 300g Meat, Seasonings', 3, 3, 20, 10, '1. Bring chicken broth to a boil, 2. Add vegetables, meat, and seasonings, 3. Simmer until cooked through.', 'app/src/main/res/drawable/soup.jpg')
]

for food_entry in food_data:
    food_name = food_entry[0]
    cursor.execute("SELECT COUNT(*) FROM foods WHERE food_name=?", (food_name,))
    count = cursor.fetchone()[0]
    if count == 0:
        cursor.execute("INSERT INTO foods (food_name, ingredients, total_ingredients, serving_size, preparation_time, cooking_time, how_to_cook, image_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", food_entry)

# Commit the changes and close the connection
conn.commit()
conn.close()