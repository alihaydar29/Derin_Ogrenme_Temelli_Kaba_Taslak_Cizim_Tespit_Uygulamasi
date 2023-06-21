import tensorflow as tf
from keras.models import load_model
from PIL import Image
import numpy as np
import base64
import io
import os
from flask import Flask, request, render_template, jsonify, send_from_directory, redirect, url_for

app = Flask(__name__, template_folder='templates', static_folder='static')
# Modeli yükle
model = load_model('MB4.h5')
# Geçici dosya dizini
TEMP_DIR = 'static'
results=[]

@app.route('/', methods=['POST'])
def predict():
    if 'image' not in request.files:
        return jsonify({'error': 'No image uploaded.'}), 400
    results.clear()
    image = process_image(request.files['image'])
    get_conv_outputs(model, image)
    # Sonuçları döndür
    return jsonify(results)

def process_image(image_file):
    img = Image.open(io.BytesIO(image_file.read()))
    img = img.convert('L')
    img_array_resized = np.array(img.resize((model.input_shape[1], model.input_shape[2])))
    print(img_array_resized.shape)
    img_tensor = np.expand_dims(img_array_resized, axis=0)
    img_tensor = img_tensor.astype(np.float32)
    temp_filename = "yeniden_buyutulan_resim.jpg"
    temp_filepath = os.path.join(TEMP_DIR, temp_filename)
    resim = Image.fromarray(img_array_resized.astype(np.uint8))
    resim.save(temp_filepath, 'JPEG')
    results.append({'name': "Kabataslak çizimin modele girmeden önceki hali:", 'image_filename': temp_filename})
    return img_tensor


def get_conv_outputs(model, img_tensor):
    global results
    # Katman isimlerini al
    layer_names = [layer.name for layer in model.layers if isinstance(layer, tf.keras.layers.Conv2D)]
    # Görselleştirme modelini oluştur
    visualization_model = tf.keras.models.Model(inputs=model.input,
                                                outputs=[model.get_layer(name).output for name in layer_names])
    activations = visualization_model.predict(img_tensor)
    classes = ["airplane", "automobile", "bird", "cat", "dog", "frog", "horse", "ship", "truck"]
    predictions = model.predict(img_tensor)
    predicted_class = classes[np.argmax(predictions)]
    print("Predicted class:", predicted_class)
    for layer_name, feature_map in zip(layer_names, activations):
        if len(feature_map.shape) == 4:
            n_features = feature_map.shape[-1]
            size = feature_map.shape[1]
            display_grid = np.zeros((size, size * n_features), dtype=np.uint8)
            for i in range(n_features):
                x = feature_map[0, :, :, i]
                x -= x.mean()
                x /= x.std()
                x *= 64
                x += 128
                x = np.clip(x, 0, 255).astype('uint8')
                display_grid[:, i * size: (i + 1) * size] = x
                temp_filename = f'{layer_name}.jpg'
                temp_filepath = os.path.join(TEMP_DIR, temp_filename)
                resim = Image.fromarray(display_grid)
                resim.save(temp_filepath, 'JPEG')
            results.append({'name': layer_name, 'image_filename': temp_filename})
            
@app.route('/')
def index():
    global results
    return render_template('index.html', results=results)

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)

