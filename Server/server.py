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
        matching_count = sum(ingredient in food_ingredients for ingredient in ingredients)

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

@app.route('/recognize', methods=['POST', 'GET'])
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
        ingredients.append("cheese")

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
    cursor.execute("SELECT food_name, ingredients FROM foods WHERE food_name=?", (food_name,))
    food_data = cursor.fetchone()

    # Close the database connection
    conn.close()

    if food_data is None:
        return jsonify({'error': 'Food not found'})

    food_name = food_data[0]
    ingredients = food_data[1].split(", ")

    return jsonify({'food_name': food_name, 'ingredients': ingredients})


app.run(host="0.0.0.0", port=8080)