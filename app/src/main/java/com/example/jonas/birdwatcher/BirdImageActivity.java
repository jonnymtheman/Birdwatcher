package com.example.jonas.birdwatcher;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import java.io.File;

/**
 * TODO kolla det gråa utanför bilden
 * Displays a larger Image in an imageView.
 * In order to scale the picture, the class PictureUtils
 * have been used.
 *
 * File:       BirdImageActivity.java
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
        setTitle(fileName);

        imageView = (ImageView) findViewById(R.id.bird_large_imageView);
        File f = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getAbsolutePath(), fileName);
        BitmapDrawable drawable = PictureUtils.getScaledDrawable(this,
                f.getPath());
        if (drawable != null) {
            imageView.setImageDrawable(drawable);
        }
    }
}
