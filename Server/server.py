import os
import io
from imageai.Detection import ObjectDetection
import json
from flask import Flask, request, jsonify

app = Flask(__name__)
execution_path = os.getcwd()

detector = ObjectDetection()
detector.setModelTypeAsYOLOv3()
detector.setModelPath( os.path.join(execution_path , "yolov3.pt"))
detector.loadModel()
def recognize_objects(image):
    # Preprocess the image
    img = image.resize((224, 224))
    img = preprocess_input(img)

    # Perform object recognition
    img = img.reshape((1, 224, 224, 3))
    preds = model.predict(img)
    predictions = decode_predictions(preds, top=5)[0]

    # Extract the labels from predictions
    labels = [label for (_, label, _) in predictions]
    print(labels)
    return labels

@app.route('/recognize', methods=['get'])
def recognize():
    detections = detector.detectObjectsFromImage(input_image=os.path.join(execution_path , "fruits.jpg"), output_image_path=os.path.join(execution_path , "image2new.jpg"), minimum_percentage_probability=30)
    for eachObject in detections:
        print(eachObject["name"] , " : ", eachObject["percentage_probability"], " : ", eachObject["box_points"] )
        print("--------------------------------")


app.run(host="0.0.0.0")
