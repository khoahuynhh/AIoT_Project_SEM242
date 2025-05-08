from fastapi import FastAPI, WebSocket, Request
import base64
import cv2
import numpy as np
from deepface.DeepFace import represent, extract_faces
from io import BytesIO
from PIL import Image
from fastapi.responses import JSONResponse
import jwt
from utils.api import ApiRespose
import requests
import os
from os.path import join, dirname
from dotenv import load_dotenv
import httpx
import time
from sentence_transformers import SentenceTransformer


app = FastAPI()

dotenv_path = join(dirname(__file__), '.env')
load_dotenv(dotenv_path)

JWT_SECRET = os.environ.get("SPRING_JWT_SECRET")
SPRING_URL = os.environ.get("SPRING_SERVER_URL")

# Simulated stored embeddings (Replace with database storage)

model = SentenceTransformer('sentence-transformers/all-MiniLM-L6-v2')
identify_face_list:dict = {}
register_face_list = []
register_face_status_code = {
    -1: "Something went wrong",
    0: "No face detected, face not matched or multiple faces detected",
    1: "Face detected",
    2: "Come closer to the camera",
    3: "Move slightly away from the camera",
    4:"Time out",
}
identify_face_status_code = {
    -1: "Something went wrong",
    0: "No face detected, multiple faces detected or face not matched",
    1: "Face matched",
    2: "Come closer to the camera",
    3: "Move slightly away from the camera",
    4:"Time out",
}
###### HELPER FUNCTIONS ######
def embedding_for_face_registration(image):
    '''
        Function return (embedding, status code) if valid else return None
        1. Just get one face from the image
        2. Face area is bigger than 80% of the area which is calculated by x * x (x is min(w,h))
    '''
    faces = represent(img_path=image, model_name="ArcFace", detector_backend='retinaface', enforce_detection=False, align=True)
    if len(faces) == 1:
        w,h = faces[0]['facial_area']['w'], faces[0]['facial_area']['h']
        w_image, h_image = image.shape[1], image.shape[0]
        ratio_w = w / w_image
        ratio_h = h / h_image
        print("ratio_w: ", ratio_w, "ratio_h: ", ratio_h)
        if ratio_h < 0.7 and ratio_w < 0.7:
            return None, 2
        elif (ratio_w >= 0.95) and (ratio_h >= 0.95):
            return None, 3
        else:
            return faces[0]['embedding'], 1

    return None, 0
def caculate_similarity_for_identification(image, embedding2):
    '''
        Return (True/False,image embedding) if valid else return (False,None)
    '''
    faces = represent(img_path=image, model_name="ArcFace", detector_backend='retinaface', enforce_detection=False, align=True)
    if len(faces) == 1:
        w,h = faces[0]['facial_area']['w'], faces[0]['facial_area']['h']
        w_image, h_image = image.shape[1], image.shape[0]
        ratio_w = w / w_image
        ratio_h = h / h_image
        print("ratio_w: ", ratio_w, "ratio_h: ", ratio_h, "confidence: ")
        if ratio_w >= 0.95 and ratio_h >= 0.95:
            return False, None, 3
        elif (ratio_w <= 0.5) and (ratio_h <= 0.5):
            return False, None, 2
        
        embedding = faces[0]['embedding']
        similarity = np.dot(embedding, embedding2) / (np.linalg.norm(embedding) * np.linalg.norm(embedding2))
        print("Similarity: ", similarity)
        if similarity > 0.8:
            return True, similarity, 1
        else:
            return False, None, 0

    return False, None, 0
###### HELPER FUNCTIONS ######



# Add user name with embedding to the database
@app.websocket("/client/face/register")
async def add_userface(websocket: WebSocket):

    token = websocket.query_params.get('token')
    if token is None: 
        websocket.send_json(ApiRespose(error="Invalid token",data={"status":-1}).to_dict())
        return
    username = get_username_from_token(token)
    if username is None or not(username in register_face_list):
        websocket.send_json(ApiRespose(error="Invalid token",data={"status":-1}).to_dict())
        return
            
    #Validate token and get username
    '''
        1. Receive image + ID + token
        2. Decode Base64 image
        3. Validate the face
        4. If valid, send embedding to Spring server through api /api/face/server/send_embedding
        5. If get response from Spring server, send success message to client
        6. If not, send error message to client
        7. Remove user name away from the list
    '''
    await websocket.accept()
    print("Client connected!")
    status_code = 0
    try:

        embedding = None
        while True:
            description = register_face_status_code[status_code]
            # handle status code
            if status_code == -1:
                await websocket.send_json(ApiRespose(message="face identification failed", data={"status": -1, "description": description}).to_dict())
                break
            elif status_code == 1:
                 # Take username away from the list
                identify_face_list.pop(username)
                await websocket.send_json(ApiRespose(message="face identification success", data={"status": 1, "description": description}).to_dict())
                break
            elif status_code == 0:
                await websocket.send_json(ApiRespose(message="Not match", data={"status": 0, "description": description}).to_dict())
            elif status_code == 2:
                await websocket.send_json(ApiRespose(message="Come closer to the camera", data={"status": 2, "description": description}).to_dict())
            elif status_code == 3:
                await websocket.send_json(ApiRespose(message="Come farther from the camera", data={"status": 3, "description": description}).to_dict())
            print(status_code)
            # Receive image + token
            data = await websocket.receive_json()

            image_data = data["image"]  # Base64 encoded image
            print(f"Received image for ID: {username}")
            # Decode Base64 image
            image_bytes = base64.b64decode(image_data.split(",")[1])
            image = Image.open(BytesIO(image_bytes))
            image = np.array(image)

            # Convert to RGB if needed
            if image.shape[-1] == 4:
                image = cv2.cvtColor(image, cv2.COLOR_RGBA2RGB)

            embedding,status_code = embedding_for_face_registration(image)
            if embedding:
                await websocket.send_json(ApiRespose(message="Wait for confirming", data={"status": 2, "description": register_face_status_code[1]}).to_dict())
                status_code = 1
                await websocket.send_json(ApiRespose(message="Success to recognize face", data={"status": 1, "description": register_face_status_code[1]}).to_dict())

                url = SPRING_URL + "/api/face/server/send_embedding"
                data = {
                    "username": username,
                    "embedding": embedding
                }
                # Take username away from the list
                register_face_list.remove(username)

                async with httpx.AsyncClient(timeout=2.0) as client:
                    res = await client.post(url, json=ApiRespose(data=data).to_dict())
                    print(res.status_code, res.headers)
                    if res.status_code == 200:
                        status_code = 1
                    else: status_code = -1
                break
                
    except Exception as e:
        print("Error:", e)
        

@app.websocket("/client/face/identify")
async def face_recognition(websocket: WebSocket):
    token = websocket.query_params.get('token')
    if token is None: 
        websocket.send_json(ApiRespose(error="Invalid token",data={"status":-1}).to_dict())
        return
    username = get_username_from_token(token)
    if username is None or not(username in identify_face_list):
        websocket.send_json(ApiRespose(error="Invalid token",data={"status":-1}).to_dict())
        return
            
    #Validate token and get username
    '''
        1. Receive image + ID + token
        2. Decode Base64 image
        3. Caculate similarity with stored embeddings
        4. If valid, send success signal to client and close the connection
        5. If not, send error message to client
    '''
    await websocket.accept()
    print("Client connected!")
    status_code = 0
    time_start = time.time()
    try:

        embedding = None
        while True:
            # handle status code
            description = identify_face_status_code[status_code]
            if time.time() - time_start > 20:
                await websocket.send_json(ApiRespose(message="Timeout", data={"status": 4, "description": description}).to_dict())
                break
            if status_code == -1:
                await websocket.send_json(ApiRespose(message="face identification failed", data={"status": -1, "description": description}).to_dict())
                break
            elif status_code == 1:
                 # Take username away from the list
                identify_face_list.pop(username)
                await websocket.send_json(ApiRespose(message="face identification success", data={"status": 1, "similarity": similarity, "description": description}).to_dict())
                break
            elif status_code == 0:
                await websocket.send_json(ApiRespose(message="Not match", data={"status": 0, "description": description}).to_dict())
            elif status_code == 2:
                await websocket.send_json(ApiRespose(message="Come closer to the camera", data={"status": 2, "description": description}).to_dict())
            elif status_code == 3:
                await websocket.send_json(ApiRespose(message="Come farther from the camera", data={"status": 3, "description": description}).to_dict())
            print("Status:" ,status_code)

            # Receive image + token
            data = await websocket.receive_json()

            image_data = data["image"]  # Base64 encoded image
            print(f"Received image for ID: {username}")
            # Decode Base64 image
            image_bytes = base64.b64decode(image_data.split(",")[1])
            image = Image.open(BytesIO(image_bytes))
            image = np.array(image)

            # Convert to RGB if needed
            if image.shape[-1] == 4:
                image = cv2.cvtColor(image, cv2.COLOR_RGBA2RGB)

            match, similarity, status_code = caculate_similarity_for_identification(image, identify_face_list[username])
            
                
    except Exception as e:
        print("Error:", e)

@app.post("/face/identify")
async def get_identify_face_list(request: Request):
    body = await request.json()
    username = body.get("username")
    embedding = body.get("embedding", [])
    print(embedding)
    if not(username in identify_face_list): identify_face_list.update({username: embedding})
    return JSONResponse(content=ApiRespose(message="face recognition success").to_dict())

@app.post("/face/register")
async def get_identify_face_list(request: Request):
    body = await request.json()
    username = body.get("username")
    if not(username in register_face_list): register_face_list.append(username)
    return JSONResponse(content=ApiRespose(message="Add user to register face recognition successfully").to_dict())

def get_username_from_token(token):
    try:
        payload = jwt.decode(token, key=base64.b64decode(JWT_SECRET), algorithms=["HS256"])
        return payload.get("sub")
    except:
        return None

def extract_face_embeddings(image, percent = 90):
    faces = represent(img_path=image, model_name="Facenet", enforce_detection=False)
    if len(faces) == 1:
        height_distance = image.shape[1] - faces[0]['facial_area']['h']
        if height_distance > 0 and height_distance < 20:
            return faces[0]['embedding']
        else:
            return 2
    elif len(faces) == 0:
        return 0
    else:
        return 0

@app.get("/test/get_embedding")
async def test():
    url = SPRING_URL + "/api/face/server/send_embedding"
    data = {
        "username": "user1",
        "embedding": stored_embeddings["user1"]
    }
    return JSONResponse(content=ApiRespose(message="face recognition success",data=data).to_dict())

# Run with: uvicorn filename:app --reload
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)