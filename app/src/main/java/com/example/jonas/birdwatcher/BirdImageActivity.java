package com.example.jonas.birdwatcher;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import java.io.File;

/**
 * Displays a larger Image in an imageView.
 * The image is scaled up to a larger size before
 * being displayed.
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
        Bitmap bitmap = BitmapFactory.decodeFile(
                f.getAbsolutePath());
        int imageSize = (int) (bitmap.getHeight()
                * (2200.0 / bitmap.getWidth()));
        Bitmap scaledImage = Bitmap.createScaledBitmap(bitmap,
                2200, imageSize, true);
        if (scaledImage != null) {
            imageView.setImageBitmap(scaledImage);
        }
    }
}
