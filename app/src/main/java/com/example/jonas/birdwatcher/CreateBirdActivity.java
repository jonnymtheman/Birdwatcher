package com.example.jonas.birdwatcher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateBirdActivity extends AppCompatActivity {

    private final String TAG = "CreateBirdActivity";

    private Button mCreateBirdButton;
    private Button mCancelButton;
    private EditText birdNameInput;
    private EditText birdLatinNameInput;


    private String nameString;
    private String latinNameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bird);

        birdNameInput = (EditText) findViewById(R.id.new_bird_editText);
        birdLatinNameInput = (EditText) findViewById(R.id.new_bird_latin_editText);


        mCreateBirdButton = (Button) findViewById(R.id.new_bird_okButton);
        mCreateBirdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Name: "+birdNameInput.getText() + ", Latin: " +birdLatinNameInput.getText());
                nameString = birdNameInput.getText().toString();
                saveBird(birdLatinNameInput.getText().toString(), birdLatinNameInput.getText().toString());

            }
        });

        mCancelButton = (Button)findViewById(R.id.cancel_create_bird);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Clicked cancel");
            }
        });
    }

    private void saveBird(String name, String latinName) {
        Log.d(TAG, "Real name: "+name);
        Log.d(TAG, "Real Latin name: "+latinName);
        int id = BirdBank.get(this).getBirds().size();
        Bird bird = new Bird(nameString, id);
        BirdBank.get(this).storeBirdInfo(bird);
        finish();
    }
}
