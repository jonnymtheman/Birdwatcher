package com.example.jonas.birdwatcher;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/*
TODO Gör så att man kan ta kort i action baren : Starta kamera activiteten
 */
public class BirdActivity extends AppCompatActivity {
    private static final String TAG = "BirdActivity";
    private static final int REQUEST_PHOTO = 1;
    private TextView birdnameView;

    private Bird bird;

    private ImageButton imageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird);

        birdnameView = (TextView) findViewById(R.id.birdName);
        String name = getIntent().getStringExtra("Hej");
        bird = getIntent().getParcelableExtra("Hej1");
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

                //Photo p = new Photo(filename);
                //mCrime.setPhoto(p);
                //showPhoto();
                Log.d(TAG, filename);
            }
        }
    }
}
