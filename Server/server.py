import os
import io
import sqlite3
from imageai.Detection import ObjectDetection
import json
from flask import Flask, request, jsonify
import base64
from PIL import Image

app = Flask(__name__)
execution_path = os.getcwd()

detector = ObjectDetection()
detector.setModelTypeAsRetinaNet()
detector.setModelPath( os.path.join(execution_path , "model2.pth"))
detector.loadModel()

def compare_ingredients(ingredients):
    # Create a connection to the SQLite database
    conn = sqlite3.connect('food_database.db')

    # Create a cursor object to interact with the database
    cursor = conn.cursor()

    # Retrieve unique food names and ingredients from the database
    cursor.execute("SELECT DISTINCT food_name, ingredients FROM foods")
    foods_data = cursor.fetchall()

    # Create a list to store matching foods with their number of matching ingredients
    matching_foods = []

    # Convert all scanned ingredients to lowercase
    ingredients = [ingredient.lower() for ingredient in ingredients]

    # Iterate over the foods and compare their ingredients with scanned ingredients
    for food in foods_data:
        food_name = food[0]
        food_ingredients = [ingredient.lower() for ingredient in food[1].split(", ")]

        # Count the number of matching ingredients
        matching_count = 0
        for ingredient in ingredients:
            for food_ingredient in food_ingredients:
                if ingredient in food_ingredient:
                    matching_count += 1

        # Add the food and its matching count to the list
        matching_foods.append((food_name, matching_count))

    # Sort the matching foods based on the number of matching ingredients in descending order
    matching_foods.sort(key=lambda x: x[1], reverse=True)

    # Check if multiple foods have the same maximum matching count
    max_count = matching_foods[0][1]
    multiple_max_foods = [food for food in matching_foods if food[1] == max_count]

    # Adjust the points for multiple foods with the maximum matching count
    if len(multiple_max_foods) > 1:
        matching_foods[0] = (matching_foods[0][0], matching_foods[0][1] + 0.1)

    # Close the database connection
    conn.close()

    return matching_foods

def recognize_objects(image):
    img = image.resize((200, 200))
    img = preprocess_input(img)
    img = img.reshape((1, 200, 200, 3))
    preds = model.predict(img)
    predictions = decode_predictions(preds, top=10)[0]
    labels = [label for (_, label, _) in predictions]
    print(labels)
    return labels

@app.route('/recognize', methods=['POST'])
def recognize():
    if request.method == 'POST':
        img = request.form.get("image")
        if img is None:
            return "No image data received", 400
        img = base64.b64decode(img)
        img = Image.open(io.BytesIO(img))
        img.save("temp.png")
        detections = detector.detectObjectsFromImage(input_image=os.path.join(execution_path , "temp.png"), output_image_path=os.path.join(execution_path , "temp_new.png"), minimum_percentage_probability=10)
        for eachObject in detections:
            print(eachObject["name"] , " : ", eachObject["percentage_probability"], " : ", eachObject["box_points"] )
            print("--------------------------------")
        # Extract the ingredient names from `detections`
        ingredients = [eachObject["name"] for eachObject in detections]
        ingredients = list(set(ingredients))

        additional_ingredients = [
            "flour", "tomato", "cheese", "mushroom", "spinach", "pepper", "noodles", "vegetables", "meat", "sauce", "rice flour", "sugar", "toppings", "bun", "patty", "lettuce", "rice", "fish", "seaweed", "pasta", "garlic", "olive oil", "parmesan cheese", "chicken", "bell peppers", "broccoli", "soy sauce", "eggs", "veggies", "salt", "pepper", "milk", "sugar", "baking powder", "butter"
        ]
        ingredients.extend(additional_ingredients)


        # Compare the scanned ingredients with the foods in the database
        matching_foods = compare_ingredients(ingredients)
        print("Ingredients:", ingredients)
        print("Matching Foods:", matching_foods)

        return jsonify({'ingredients': ingredients, 'matching_foods': matching_foods})

@app.route('/recognize/<food_name>', methods=['GET'])
def get_food_data(food_name):
    # Create a connection to the SQLite database
    conn = sqlite3.connect('food_database.db')

    # Create a cursor object to interact with the database
    cursor = conn.cursor()

    # Retrieve the food data for the specified food name
    cursor.execute("SELECT food_name, ingredients, total_ingredients, serving_size, preparation_time, cooking_time, how_to_cook, image_path, video_path, id FROM foods WHERE food_name=?", (food_name,))
    food_data = cursor.fetchone()

    # Close the database connection
    conn.close()

    if food_data is None:
        return jsonify({'error': 'Food not found'})

    food_name = food_data[0]
    ingredients = food_data[1].split(", ")
    total_ingredients = food_data[2]
    serving_size = food_data[3]
    preparation_time = food_data[4]
    cooking_time = food_data[5]
    how_to_cook = food_data[6]
    image_path = food_data[7]
    video_path = food_data[8]
    id = food_data[9]

    return jsonify({
        'food_name': food_name,
        'ingredients': ingredients,
        'total_ingredients': total_ingredients,
        'serving_size': serving_size,
        'preparation_time': preparation_time,
        'cooking_time': cooking_time,
        'how_to_cook': how_to_cook,
        'image_path': image_path,
        'video_path': video_path,
        'id': id
    })

@app.route('/recognizemany', methods=['POST'])
def get_multiple_food_data():
    # Get the array of food names from the request body
    food_names = request.get_json()

    # Create a connection to the SQLite database
    conn = sqlite3.connect('food_database.db')

    # Create a cursor object to interact with the database
    cursor = conn.cursor()

    # Initialize an empty list to store the food data
    foods_data = []

    for food_name in food_names:
        # Retrieve the food data for the specified food name
        cursor.execute("SELECT food_name, ingredients, total_ingredients, serving_size, preparation_time, cooking_time, how_to_cook, image_path, video_path, id FROM foods WHERE food_name=?", (food_name,))
        food_data = cursor.fetchone()

        if food_data is None:
            foods_data.append({'error': f'Food {food_name} not found'})
        else:
            food_name = food_data[0]
            ingredients = food_data[1].split(", ")
            total_ingredients = food_data[2]
            serving_size = food_data[3]
            preparation_time = food_data[4]
            cooking_time = food_data[5]
            how_to_cook = food_data[6]
            image_path = food_data[7]
            video_path = food_data[8]
            id = food_data[9]
            foods_data.append({
                'food_name': food_name,
                'ingredients': ingredients,
                'total_ingredients': total_ingredients,
                'serving_size': serving_size,
                'preparation_time': preparation_time,
                'cooking_time': cooking_time,
                'how_to_cook': how_to_cook,
                'image_path': image_path,
                'video_path': video_path,
                'id': id
            })

    # Close the database connection
    conn.close()
    print(foods_data)
    return jsonify(foods_data)

@app.route('/users', methods=['GET'])
def get_all_users():
    # Create a connection to the SQLite database
    conn = sqlite3.connect('food_database.db')
    conn.row_factory = sqlite3.Row  # Set the row factory to sqlite3.Row

    cursor = conn.cursor()
    cursor.execute("SELECT * FROM users")
    users = cursor.fetchall()

    users_list = [{'username': user['username'], 'password': user['password'], 'saved_recipe': user['save_recipe']} for user in users]

    return jsonify(users_list)

@app.route('/login', methods=['POST'])
def login():
    # Get the request data
    data = request.get_json()

    # Get the username and password from the request data
    username = data.get('username')
    password = data.get('password')

    # Create a connection to the SQLite database
    conn = sqlite3.connect('food_database.db')

    # Create a cursor object to interact with the database
    cursor = conn.cursor()

    # Execute the query to select the user with the given username
    cursor.execute("SELECT password FROM users WHERE username=?", (username,))

    # Fetch the first row from the query
    user = cursor.fetchone()

    # Close the connection
    conn.close()

    if user is None:
        # If the user is not found, return an error message
        return jsonify({'success': False, 'error': 'Invalid username or password'}), 401
    
    else:
        # If the user is found, check the password
        if user[0] == password:
            # If the password is correct, return a success message
            return jsonify({'success': True})
        else:
            # If the password is incorrect, return an error message
            return jsonify({'success': False, 'error': 'Invalid username or password'}), 401


@app.route('/register', methods=['POST'])
def register():
    # Get the request data
    data = request.get_json()

    # Get the username and password from the request data
    username = data.get('username')
    password = data.get('password')

    # Create a connection to the SQLite database
    conn = sqlite3.connect('food_database.db')

    # Create a cursor object to interact with the database
    cursor = conn.cursor()

    try:
        # Execute the query to insert the new user
        # Check if the username is already in the database
        cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
        if cursor.fetchone() is not None:
            # If the username is already taken, return an error message
            return jsonify({'success': False, 'error': 'Username is already taken'}), 400

        # If the username is not taken, insert the new user
        cursor.execute("INSERT INTO users (username, password) VALUES (?, ?)", (username, password))
        conn.commit()
        cursor.execute("INSERT INTO users (username, password) VALUES (?, ?)", (username, password))

        # Commit the changes
        conn.commit()

        # Close the connection
        conn.close()

        # Return a success message
        return jsonify({'success': True})

    except sqlite3.IntegrityError:
        # If the username is already taken, return an error message
        return jsonify({'success': False, 'error': 'Username is already taken'}), 400


@app.route('/user_recipes', methods=['GET'])
def get_user_recipes():
    # Get the username from the query parameters
    username = request.args.get('username')

    # Create a connection to the SQLite database
    conn = sqlite3.connect('food_database.db')

    # Create a cursor object to interact with the database
    cursor = conn.cursor()

    # Execute the query to select the user
    cursor.execute("SELECT save_recipe FROM users WHERE username = ?", (username,))

    # Fetch the first row from the query
    user = cursor.fetchone()

    if user is None:
        # If there is no user with the given username, return an error message
        conn.close()
        return jsonify({'error': 'User not found'}), 404

    # Convert the saved recipes from a JSON-formatted string to a list
    saved_recipes_ids = json.loads(user[0]) if user[0] else []

    # Fetch the saved recipes from the foods table
    cursor.execute("SELECT * FROM foods WHERE id IN ({})".format(','.join('?' for _ in saved_recipes_ids)), saved_recipes_ids)
    saved_recipes = cursor.fetchall()

    # Close the connection
    conn.close()

    # Convert the list of tuples into a list of dictionaries for JSON serialization
    saved_recipes_list = [{'id': recipe[0], 'food_name': recipe[1], 'ingredients': recipe[2].split(", "), 'total_ingredients': recipe[3], 'serving_size': recipe[4], 'preparation_time': recipe[5], 'cooking_time': recipe[6], 'how_to_cook': recipe[7], 'image_path': recipe[8], 'video_path': recipe[9]} for recipe in saved_recipes]

    return jsonify(saved_recipes_list)

import json

@app.route('/check_save', methods=['GET'])
def check_save():
    # Get the username and recipe_id from the query parameters
    username = request.args.get('username')
    recipe_id = request.args.get('recipe_id')

    # Create a connection to the SQLite database
    conn = sqlite3.connect('food_database.db')

    # Create a cursor object to interact with the database
    cursor = conn.cursor()

    # Execute a SELECT statement to get the save_recipe for the given username
    cursor.execute("SELECT save_recipe FROM users WHERE username = ?", (username,))

    # Fetch the first row from the results (there should only be one row since usernames are unique)
    row = cursor.fetchone()

    # Convert the save_recipe string back into a list
    saved_recipes = json.loads(row[0]) if row[0] else []

    # If a row was found and the recipe_id is in the save_recipe, return a positive response
    if row is not None and int(recipe_id) in saved_recipes:
        return jsonify({"status": True, "message": "Recipe is saved"}), 200

    # If no row was found or the recipe_id is not in the save_recipe, return a negative response
    return jsonify({"status": False, "message": "Recipe is not saved"}), 404

@app.route('/save_recipe', methods=['GET'])
def save_recipe():
    # Get the username and recipe_id from the query parameters
    username = request.args.get('username')
    recipe_id = int(request.args.get('recipe_id'))

    # Create a connection to the SQLite database
    conn = sqlite3.connect('food_database.db')

    # Create a cursor object to interact with the database
    cursor = conn.cursor()

    # Execute a SELECT statement to get the save_recipe for the given username
    cursor.execute("SELECT save_recipe FROM users WHERE username = ?", (username,))

    # Fetch the first row from the results (there should only be one row since usernames are unique)
    row = cursor.fetchone()

    # Convert the save_recipe string back into a list
    saved_recipes = json.loads(row[0]) if row[0] else []

    # If the recipe_id is in save_recipe, remove it, else append it
    if recipe_id in saved_recipes:
        saved_recipes.remove(recipe_id)
    else:
        saved_recipes.append(recipe_id)

    # Update the save_recipe in the database
    cursor.execute("UPDATE users SET save_recipe = ? WHERE username = ?", (json.dumps(saved_recipes), username))

    # Commit the changes and close the connection
    conn.commit()
    conn.close()

    # Return a positive response
    return jsonify({"status": True, "message": "Recipe saved status updated"}), 200
app.run(host="0.0.0.0", port=8080)