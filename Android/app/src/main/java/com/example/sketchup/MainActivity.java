package com.example.sketchup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button btnmainbasla,btnmainresimler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnmainbasla=findViewById(R.id.btnmainBasla);
        btnmainresimler=findViewById(R.id.btnmainresimler);
        islem();
    }

    private void islem() {

        btnmainbasla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cizaktivity= new Intent(MainActivity.this,CizActivity.class);
                startActivity(cizaktivity);
            }
        });
        btnmainresimler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resimactivity=new Intent(MainActivity.this,ResimActivity.class);
                startActivity(resimactivity);
            }
        });
    }


}