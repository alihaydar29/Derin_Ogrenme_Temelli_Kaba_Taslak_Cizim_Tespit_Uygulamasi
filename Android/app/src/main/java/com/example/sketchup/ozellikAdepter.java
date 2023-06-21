package com.example.sketchup;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ozellikAdepter {
    /*
    public static void showCustomDialog(Context context, CizActivity cizActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);

        // Özel bir layout dosyasını kullanarak dialog içeriğini ayarlayın
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_item, null);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btnHayir = dialogView.findViewById(R.id.btnhayir);
        Button btnEvet = dialogView.findViewById(R.id.btnevet);

        // Dialog penceresinin dışına tıklandığında dialogun kapatılmasını sağlayın
        dialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btnEvet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cizActivity.yenile();
                alertDialog.dismiss();
            }
        });
        btnHayir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hayır butonuna tıklandığında yapılacak işlemler
                cizActivity.finish();
                alertDialog.dismiss();
            }
        });

        alertDialog.getWindow().setGravity(Gravity.BOTTOM); // Dialog ekranın altında görünecek şekilde ayarlandı
        alertDialog.show();
    }
    */
}

