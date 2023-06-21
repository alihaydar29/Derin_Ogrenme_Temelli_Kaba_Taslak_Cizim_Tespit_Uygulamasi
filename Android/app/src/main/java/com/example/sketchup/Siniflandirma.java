package com.example.sketchup;

import android.app.Activity;

import android.graphics.Bitmap;


import com.example.sketchup.ml.MB4;

import org.tensorflow.lite.DataType;

import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class Siniflandirma{
    private static final String labelsFile = "etiketler.csv";
    public static final int height=79;
    public static final int widht=79;

    private float[] sonuclar;
    private String[] labels;
    private int sonuc_index;
    private Bitmap bitmap;
    private ByteBuffer imgData = null;
    public MB4 model;
    private int[] imagePixels = new int[height * widht];
    public Siniflandirma(Activity activity){

        this.labels=label(activity,labelsFile);

        this.imgData = ByteBuffer.allocateDirect(4* 1 *height*widht* 1);
        //4 ile çarpma, 32-bit renk derinliği varsayılarak her pikselin 4 byte gerektirdiğini belirtir.
        this.imgData.order(ByteOrder.nativeOrder());
        try {
            this.model = MB4.newInstance(activity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void model(Bitmap bmp){

        // Creates inputs for reference.
        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, height, widht, 1}, DataType.FLOAT32);
        this.bitmap=bmp;
        this.imgData=convertBitmapToByteBuffer(bitmap);

        inputFeature0.loadBuffer(imgData);
        // Runs model inference and gets result.
        MB4.Outputs outputs = model.process(inputFeature0);
        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

        this.sonuclar=outputFeature0.getFloatArray();
        this.sonuc_index=getMax(this.sonuclar);

    }
    public ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        imgData.rewind();
        bitmap.getPixels(imagePixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < widht; ++i) {
            for (int j = 0; j < height; ++j) {
                final int color = imagePixels[pixel++];
                imgData.putFloat((((color >> 16) & 0xFF) + ((color >> 8) & 0xFF) + (color & 0xFF)) / 3.0f  );
            }
        }
        return imgData;
    }
    private String[] label(Activity activity, String labelsFile){
        // label.txt dosyasını oku ve List<String> oluştur
        List<String> labelList = new ArrayList<>();
        try {
            InputStream is = activity.getAssets().open(labelsFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                labelList.add(line);
            }
            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // List<String>'i String[]'a dönüştür
        String[] labels = new String[labelList.size()];
        labelList.toArray(labels);
        return labels;
    }

    private int getMax(float[] arr){
        int max=0;
        for(int i=0; i<arr.length;i++){
            if(arr[i]>arr[max]) max=i;
        }
        return max;
    }

    public void modelClose(){
        this.model.close();
    }

    public int getSonuc_index() {
        return sonuc_index;
    }

    public String[] getLabels() {
        return labels;
    }

    public float[] getSonuclar() {
        return sonuclar;
    }

}
