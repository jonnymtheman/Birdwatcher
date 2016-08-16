package com.example.jonas.birdwatcher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/*
TODO Gör så att man kan ta kort i action baren : Starta kamera activiteten
TODO gör denna till en scrollview
 */
public class BirdActivity extends AppCompatActivity {
    private static final String TAG = "BirdActivity";
    private static final int REQUEST_PHOTO = 1;
    private static final String BIRD_NAME = "BIRD_NAME";
    private static final String BIRD_ID = "BIRD_ID";
    private static final String PHOTO_ID = "PHOTO_ID";

    private TextView birdnameView;
    private ImageView birdImage;
    private ImageButton imageButton;
    private ListView photosList;

    private Bird bird;

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
                intent.putExtra(BIRD_NAME, bird.getName());
                intent.putExtra(BIRD_ID, bird.getmId());
                intent.putExtra(PHOTO_ID, bird.getPhotos().size());
                startActivityForResult(intent, REQUEST_PHOTO);

            }
        });


        ArrayList<BirdPhoto> photos1 = bird.getPhotos();
        BirdPhoto photo = bird.getPhotos().get(2);
        Log.d(TAG, "PhotoName:"+photo.getFileName());
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).
                getAbsolutePath(), photo.getFileName()); //"1464691368453.jpg");
        String fstr = f.getAbsolutePath();
        //Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
        birdImage = (ImageView) findViewById(R.id.birdImageView);
        birdImage.setImageURI(Uri.fromFile(f));
        photosList = (ListView) findViewById(R.id.bird_photos_listView);


    }

    private void showPhoto() {

        //File f = getAlbumStorageDir(this.appContext, "Birds");
     /*   File file = new File(getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), "Birds");
        Uri uri = Uri.fromFile(file);
        birdImage.setImageURI(uri); */

     /*   BitmapDrawable b = null;
        if (photo != null) {
            String photoPath = getFileStreamPath(photo.getFileName()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(this, photoPath);
         } */
        // birdImage.setImageDrawable(b);
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onStop() {
        super.onStop();
       /* if (birdImage != null) {
            BitmapDrawable b = (BitmapDrawable)birdImage.getDrawable();
            if (b.getBitmap() != null) {
                b.getBitmap().recycle();
            }
            birdImage.setImageDrawable(null); */
        // }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("POPO", "Requestcode:"+requestCode);
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
                //BirdBank.get(this).storeBirds();
            }
        }
    }
}
