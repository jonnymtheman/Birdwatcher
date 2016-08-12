package com.example.jonas.birdwatcher;

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
    private TextView birdnameView;

    private ImageButton imageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird);

        birdnameView = (TextView) findViewById(R.id.birdName);
        String name = getIntent().getStringExtra("Hej");
        Log.d(TAG, "Name: "+name);
        birdnameView.setText(name);

        //TODO Check if camera exists
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BirdActivity.this, CameraActivity.class);
                startActivity(intent);

            }
        });
    }
}
