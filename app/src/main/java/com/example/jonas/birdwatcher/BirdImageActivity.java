package com.example.jonas.birdwatcher;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;

/**
 * File:       ${FILE_NAME}.java
 * Author:     Jonas Nyman
 * Assignment: Inlämningsuppgift 3 - Valfri Applikation
 * Course:     Utveckling av mobila applikationer
 * Version:    1.0
 */
public class BirdImageActivity extends AppCompatActivity{

    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird_image);

        String fileName = getIntent().getStringExtra("Name");

        imageView = (ImageView) findViewById(R.id.bird_large_imageView);
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).
                getAbsolutePath(), fileName);
        BitmapDrawable md = PictureUtils.getScaledDrawable(this, f.getPath());
        //Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
        imageView.setImageDrawable(md);


    }
}
