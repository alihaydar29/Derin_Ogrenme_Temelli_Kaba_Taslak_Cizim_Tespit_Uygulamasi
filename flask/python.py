import requests

# Resim dosyasının yolunu belirleyin
image_path = "araba2.png"

# API endpoint'i
endpoint = "http://localhost:5000/"

# Resmi yükleyin ve sonuçları alın
with open(image_path, "rb") as f:
    files = {"image": f}
    response = requests.post(endpoint, files=files)

# Sonuçları kontrol edin
if response.status_code == 200:
    results = response.json()
    for result in results:
        print("Layer Name:", result["name"])
        print("Image Filename:", result["image_filename"])
        print()
else:
    print("API request failed with status code:", response.status_code)
