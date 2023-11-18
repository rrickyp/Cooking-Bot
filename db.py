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
        image_path TEXT
    )
''')

# Insert food entries
food_data = [
    ('Pizza', 'Dough, Tomato Sauce, Cheese, Toppings', 'app/src/main/res/drawable/pizza.jpg'),
    ('Noodle', 'Noodles, Vegetables, Meat, Sauce', 'app/src/main/res/drawable/noodle.jpg'),
    ('Rice Cake', 'Rice Flour, Sugar, Toppings', 'app/src/main/res/drawable/ricecake.jpg'),
    ('Burger', 'Bun, Patty, Cheese, Lettuce, Tomato', 'app/src/main/res/drawable/burger.jpg'),
    ('Sushi', 'Rice, Fish, Seaweed, Vegetables', 'app/src/main/res/drawable/sushi.jpg')
]

for food_entry in food_data:
    food_name = food_entry[0]
    cursor.execute("SELECT COUNT(*) FROM foods WHERE food_name=?", (food_name,))
    count = cursor.fetchone()[0]
    if count == 0:
        cursor.execute("INSERT INTO foods (food_name, ingredients, image_path) VALUES (?, ?, ?)", food_entry)

# Commit the changes and close the connection
conn.commit()
conn.close()