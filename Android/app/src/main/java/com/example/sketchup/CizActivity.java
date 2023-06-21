package com.example.sketchup;

import android.annotation.SuppressLint;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;


import android.graphics.Bitmap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class CizActivity extends AppCompatActivity {
    Button btncizkaydet,btntemizle,btncizdegistir;
    ImageButton btncizwebapi;
    public TextView txtcizsonuc;
    TextView txticizsure,txtbeklenencizim;
    DrawingView drawingView;
    Siniflandirma siniflandirma;
    private CountDownTimer countDownTimer;
    public String dosyaAdi="Sketchup";
    private String beklenencikti;
    ConstraintLayout cizactivitylayout;
    int originalColor;


    @SuppressLint("MissingInflatedId" )
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ciz);
        cizactivitylayout=findViewById(R.id.cizlayout);
        btncizkaydet=findViewById(R.id.btncizkaydet);
        btntemizle=findViewById(R.id.btnciztemizle);
        btncizdegistir=findViewById(R.id.btncizdegistir);
        btncizwebapi = findViewById(R.id.imgbtnciz);
        drawingView = findViewById(R.id.drawingView);
        txtcizsonuc=findViewById(R.id.txtcizsonuc);
        txticizsure=findViewById(R.id.txtcizsure);
        txtbeklenencizim=findViewById(R.id.txtbeklenencizim);
        originalColor = getResources().getColor(R.color.cigdem);

        siniflandirma=new Siniflandirma(this);

        Cizislem();
        beklenencikti = randomsinifsec(siniflandirma.getLabels());
        txtbeklenencizim.setText("Çiz...\n"+beklenencikti);
        tekrar(txticizsure);
        txtcizsonuc.setText("..");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void Cizislem() {
        btncizkaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = drawingView.getBitmap();
                siniflandir(bitmap);

            }
        });

        btntemizle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.clearCanvas();
                txtcizsonuc.setText("..");
                cizactivitylayout.setBackgroundColor(originalColor);
            }
        });

        btncizwebapi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://192.168.1.100:5000/";
                Bitmap bitmap = drawingView.getBitmap();
                Bitmap scaledBitmap = cropAndResize(bitmap, Siniflandirma.widht-8, Siniflandirma.height-8);
                Bitmap paddingbitmap = addPadding(scaledBitmap,4);

                // Yeni bir Bitmap oluşturun ve boyutlarıyla aynı şekilde ayarlayın
                //Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

                // Canvas oluşturun ve yeni Bitmap üzerinde çalışın
                //Canvas canvas = new Canvas(newBitmap);
                //canvas.drawColor(Color.WHITE); // Arka planı beyaz olarak doldurun
                //canvas.drawBitmap(bitmap, 0, 0, null);

                Web webTask = new Web(paddingbitmap, url);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(webTask);
                executor.shutdown();

                try {
                    webServis();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        btncizdegistir.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                cizactivitylayout.setBackgroundColor(originalColor);
                yenile();
            }
        });
    }
    public void yenile(){
        txtcizsonuc.setText("..");
        drawingView.clearCanvas();
        beklenencikti = randomsinifsec(siniflandirma.getLabels());
        txtbeklenencizim.setText("Çiz...\n"+beklenencikti);
        tekrar(txticizsure);
    }
    public void siniflandir(Bitmap bitmap){
        Bitmap scaledBitmap = cropAndResize(bitmap, Siniflandirma.widht-8, Siniflandirma.height-8);
        Bitmap paddingbitmap = addPadding(scaledBitmap,4);
        siniflandirma.model(paddingbitmap);
        float fsonuc= siniflandirma.getSonuclar()[siniflandirma.getSonuc_index()];
        String sonuc = String.format("%.3f", fsonuc);
        txtcizsonuc.setText(siniflandirma.getLabels()[siniflandirma.getSonuc_index()]+" ("+sonuc+")");


        saveImageToGallery(paddingbitmap,dosyaAdi);

        if(beklenencikti.equals(siniflandirma.getLabels()[siniflandirma.getSonuc_index()])){
            cizactivitylayout.setBackgroundColor(Color.GREEN);
        } else {
            cizactivitylayout.setBackgroundColor(Color.RED);
        }
        countDownTimer.cancel();
        //ozellikAdepter.showCustomDialog(this,this);
    }

    public static String randomsinifsec(String[] strings) {
        Random random = new Random();
        int randomIndex = random.nextInt(strings.length);
        return strings[randomIndex];
    }
    public int[] findEdges(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int leftEdge = Integer.MAX_VALUE;
        int rightEdge = Integer.MIN_VALUE;
        int topEdge = Integer.MAX_VALUE;
        int bottomEdge = Integer.MIN_VALUE;

        // Pikselleri tek tek tarayarak en uçtaki siyah piksellerin konumlarını bulun
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (bitmap.getPixel(x, y) == Color.BLACK) {
                    if (x < leftEdge) {
                        leftEdge = x;
                    }
                    if (x > rightEdge) {
                        rightEdge = x;
                    }
                    if (y < topEdge) {
                        topEdge = y;
                    }
                    if (y > bottomEdge) {
                        bottomEdge = y;
                    }
                }
            }
        }
        // Bulunan kenarları bir dizi olarak döndürün
        return new int[]{leftEdge, topEdge, rightEdge, bottomEdge};
    }
    public Bitmap cropAndResize(Bitmap bitmap, int newWidth,int newHeight) {
        int[] edges = findEdges(bitmap);
        // Resmi kırpın
        Bitmap croppedBitmap = Bitmap.createBitmap(
                bitmap,
                edges[0], // sol kenarın x koordinatı
                edges[1], // üst kenarın y koordinatı
                edges[2] - edges[0], // kırpılan bölgenin genişliği
                edges[3] - edges[1] // kırpılan bölgenin yüksekliği
        );

        // Kırpılmış resmi yeni boyutuna ölçeklendirin
        float scale = Math.min(
                (float) newWidth / croppedBitmap.getWidth(),
                (float) newHeight / croppedBitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap scaledBitmap = Bitmap.createBitmap(
                croppedBitmap,
                0, 0,
                croppedBitmap.getWidth(),
                croppedBitmap.getHeight(),
                matrix,
                true
        );
        // Yeni boyutlandırılmış resmi bir arka plan bitmapine yerleştirin
        Bitmap output = Bitmap.createBitmap(
                newWidth,
                newHeight,
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(output);
        canvas.drawColor(Color.WHITE);
        int left = (newWidth - scaledBitmap.getWidth()) / 2;
        int top = (newHeight - scaledBitmap.getHeight()) / 2;
        canvas.drawBitmap(scaledBitmap, left, top, null);

        return output;
    }
    public Bitmap addPadding(Bitmap bitmap, int padding) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int newWidth = width + 2 * padding;
        int newHeight = height + 2 * padding;

        Bitmap output = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawColor(Color.WHITE); // Tüm yeni bitmap'i beyaz renkte doldurur

        canvas.drawBitmap(bitmap, padding, padding, null);

        return output;
    }


    private void saveImageToGallery(Bitmap bitmap, String fileName){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timeStamp = dateFormat.format(new Date());

        String fileNameWithTimeStamp = fileName + "_" + timeStamp;
        OutputStream fos;
        try{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

                ContentResolver resolver = getContentResolver();
                ContentValues contentValues =  new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileNameWithTimeStamp);
                //contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + dosyaAdi);
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                Objects.requireNonNull(fos);

                Toast.makeText(this, "Resim Kaydedildi", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){

            Toast.makeText(this, "Resim kaydedilemedi \n" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    private void tekrar(TextView wws) {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(10000, 1000) {
            //değerler salise cinsisndendir. 10 snden 1'er 1'er geri say
            @Override
            public void onTick(long millisUntilFinished) {
                //her saniye değişiminde yapılacak işlemler
                wws.setText(millisUntilFinished / 1000+"sn");//millisUntilFinished salise cinsindedir 1000'e bölerek saniye değerini elde ederiz
            }
            @Override
            public void onFinish() {
                Bitmap bitmap = drawingView.getBitmap();
                siniflandir(bitmap);
            }
        }.start();
    }
    public void webServis() throws MalformedURLException {
        String url = "http://192.168.1.100:5000/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tekrar(txticizsure);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        siniflandirma.modelClose();
        countDownTimer.cancel();
    }
}
