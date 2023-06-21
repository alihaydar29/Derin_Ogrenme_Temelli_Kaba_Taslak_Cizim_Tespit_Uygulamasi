package com.example.sketchup;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResimActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;
    List<String> imagePaths;
    ImageAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resim);

        recyclerView = findViewById(R.id.recyclerView);

        // Resimlerin bulunduğu dizini belirtin
        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Sketchup/";

        // Resimleri almak için bir yöntem çağırın
        ArrayList<String> imagePaths = getImagePathsFromDirectory(directoryPath);
        Collections.reverse(imagePaths);

        imageAdapter = new ImageAdapter(imagePaths,this );

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(imageAdapter);

        int newTotalItemCount = imageAdapter.getItemCount();

        // Activity'nin başlığını güncelleyin
        setTitle("Toplam Öğe Sayısı: " + newTotalItemCount);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                imagePaths.clear();
                File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Sketchup");
                if (directory.exists()) {
                    File[] files = directory.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            if (file.getName().endsWith(".png")) {
                                imagePaths.add(file.getAbsolutePath());
                            }
                        }
                    }
                } else {
                    Log.d("İzin","Resim bulunamadı");
                }

                adapter = new ImageAdapter(imagePaths, ResimActivity.this);
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);

                // RecyclerView oluşturulurken veya güncellenirken
                int newTotalItemCount = imageAdapter.getItemCount();
                // Activity'nin başlığını güncelleyin
                setTitle("Toplam Öğe Sayısı: " + newTotalItemCount);
            }

        });


    }
    private ArrayList<String> getImagePathsFromDirectory(String directoryPath) {
        ArrayList<String> imagePaths = new ArrayList<>();

        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".png")) {
                    imagePaths.add(file.getAbsolutePath());
                }
            }
        }

        return imagePaths;
    }
}
