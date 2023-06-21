package com.example.sketchup;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.InputQueue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Web implements Callable<Void> {
    private Bitmap bitmap;
    String url;

    public Web(Bitmap bitmap,String url) {
        this.bitmap = bitmap;
        this.url=url;
    }
    @Override
    public Void call() throws Exception {
        // Bitmap'i byte dizisine dönüştürün
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bitmapData = byteArrayOutputStream.toByteArray();

        OkHttpClient client = new OkHttpClient();

        // Bitmap verisini ekleyin
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "image.jpg",
                        RequestBody.create(MediaType.parse("image/jpeg"), bitmapData))
                .build();

        Request request = new Request.Builder()
                .url(url) // Flask API'nin doğru URL'sini kullanın
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            // İsteği gönderdikten sonra herhangi bir yanıt almanıza gerek yok
            Log.d("TAG", "Resim başarıyla gönderildi.");
        } catch (IOException e) {
            Log.e("TAG", "Resim gönderilirken hata oluştu: " + e.getMessage());
        }


        return null;
    }
}
