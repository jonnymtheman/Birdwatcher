package com.example.jonas.birdwatcher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/*
TODO Gör så att man kan ta kort i action baren : Starta kamera activiteten
 */
public class BirdActivity extends AppCompatActivity {
    private static final String TAG = "BirdActivity";
    private static final int REQUEST_PHOTO = 1;
    private TextView birdnameView;
    private ImageView birdImage;

    private Bird bird;

    private ImageButton imageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird);

        birdnameView = (TextView) findViewById(R.id.birdName);
        String name = getIntent().getStringExtra("Hej");
        bird = getIntent().getParcelableExtra("Hej1");
        ArrayList<BirdPhoto> tmp = getIntent().getParcelableArrayListExtra("Hej2");
        bird.setPhotos(tmp);
        Log.d(TAG, "Name: "+name);
        Log.d(TAG, "Bird: " +bird.getName()+","+bird.getmId());
        birdnameView.setText(name);

        //TODO Check if camera exists
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BirdActivity.this, CameraActivity.class);
                startActivityForResult(intent, REQUEST_PHOTO);

            }
        });

        birdImage = (ImageView) findViewById(R.id.birdImageView);
    }

    private void showPhoto() {
        BirdPhoto photo = bird.getPhotos().get(0);
        BitmapDrawable b = null;
        if (photo != null) {
            String photoPath = getFileStreamPath(photo.getFileName()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(this, photoPath);
        }
        birdImage.setImageDrawable(b);
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (birdImage != null) {
            BitmapDrawable b = (BitmapDrawable)birdImage.getDrawable();
            if (b.getBitmap() != null) {
                b.getBitmap().recycle();
            }
            birdImage.setImageDrawable(null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        } else if (requestCode == REQUEST_PHOTO) {
            // create a new Photo object and attach it to the crime
            String filename = data
                    .getStringExtra(BirdCameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null) {
                BirdPhoto photo = new BirdPhoto(filename);
                ArrayList<BirdPhoto> photos = new ArrayList<BirdPhoto>();
                photos.add(photo);
                bird.setPhotos(photos);
                BirdBank.get(this).updateBird(bird);
                //Photo p = new Photo(filename);
                //mCrime.setPhoto(p);
                showPhoto();
                Log.d(TAG, filename);
            }
        }
    }
}
