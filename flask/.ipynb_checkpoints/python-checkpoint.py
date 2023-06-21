import requests
import cv2
import base64
import numpy as np

url = 'http://127.0.0.1:5000/'

# Resmi yükle ve kodla
image_path = 'S.jpg'
image = cv2.imread(image_path)
img_encoded = cv2.imencode('.jpg', image)
img_base64 = base64.b64encode(img_encoded.tobytes()).decode('utf-8')

# HTTP isteği gönder
headers = {'content-type': 'image/jpeg'}
response = requests.post(url, data=img_base64, headers=headers)

# İsteği gönder ve dönüşü kullanma
response.raise_for_status()
