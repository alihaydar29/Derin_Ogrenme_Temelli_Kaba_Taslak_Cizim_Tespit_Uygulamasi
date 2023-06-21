import tensorflow as tf
from keras.models import load_model
from PIL import Image
import numpy as np
import base64
import io
from flask import Flask, request, jsonify, render_template

app = Flask(__name__)

# Modeli yükle
model = load_model('MB4.h5')

# Katman isimlerini al
layer_names = [layer.name for layer in model.layers if isinstance(layer, tf.keras.layers.Conv2D)]

# Görselleştirme modelini oluştur
visualization_model = tf.keras.models.Model(inputs=model.input, outputs=[model.get_layer(name).output for name in layer_names])

@app.route('/', methods=['GET'])
def index():
    return render_template('index.html')

# API endpoint'i
@app.route('/', methods=['POST'])
def katman_ciktilari():
    # Gelen görüntüyü al
    file = request.files['image']
    img = Image.open(file).convert('L')
    img_array_resized = np.array(img.resize((model.input_shape[1], model.input_shape[2])))
    img_tensor = np.expand_dims(img_array_resized, axis=0)
    img_tensor = img_tensor.astype(np.float32) / 255.0

    # Katman çıktılarını al
    activations = visualization_model.predict(img_tensor)

    classes = ["airplane", "automobile", "bird", "cat", "dog", "frog", "horse", "ship", "truck"]
    predictions = model.predict(img_tensor)
    predicted_class = classes[np.argmax(predictions)]

    print("Predicted class:", predicted_class)

    # Katman çıktılarını resim olarak dönüştür
    results = []
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
            # Grid'i base64 formatında dönüştür
            img_pil = Image.fromarray(display_grid)
            buffered = io.BytesIO()
            img_pil.save(buffered, format="JPEG")
            img_str = base64.b64encode(buffered.getvalue()).decode('ascii')
            results.append({'name': layer_name, 'image': img_str})

    return jsonify(results)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000,debug=True)
