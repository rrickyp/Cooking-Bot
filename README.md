# FoodHub <img src="app/src/main/res/drawable/logo_transparent.png" width="40"/>
A personalized cooking bot application, that generate recipes based on the ingredients that users have.

https://github.com/rrickyp/Cooking-Bot/assets/100031934/73bd9a2d-c009-4555-846b-03cba3955c40

## Features
1. Detecting ingredients inside the image
2. Generating some recipes based on the detected ingredients

## Setting Up the Server

1. Clone the repository
  ```bash
  git clone https://github.com/rrickyp/Cooking-Bot.git
  ```
2. Download the model from https://github.com/OlafenwaMoses/ImageAI/releases/download/3.0.0-pretrained/retinanet_resnet50_fpn_coco-eeacb38b.pth, put it inside the Server directory, and rename it to “model2.pth”.
   
3. Move to the Server directory and install all the dependencies (make sure that your python version is >=3.7 and <3.11)
  ```bash
  cd Server
  pip3 install -r requirements.txt
  ```
4. Initialize the database
  ```bash
  python3 db.py
  ```
5. Start the server and get the URL (not 127.0.0.1:8080)
  ```bash
  python3 server.py
  ```
6. Open   ```app/src/main/java/hku/hk/cs/cooking_bot/data/Constants.kt``` and change the URL
  ```java
  object Constants {
      const val BASE_URL = "YOUR_URL:8080"
  }
  ```
6. Open the app in Android Studio and run it. For the best development experience, use Android Studio 4.2 or higher.

