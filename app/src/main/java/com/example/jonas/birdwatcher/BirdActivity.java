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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/*

 */
public class BirdActivity extends AppCompatActivity {
    private static final String TAG = "BirdActivity";
    private static final int REQUEST_PHOTO = 1;
    private static final String BIRD_NAME = "BIRD_NAME";
    private static final String BIRD_ID = "BIRD_ID";
    private static final String PHOTO_ID = "PHOTO_ID";

    private TextView nameView;
    private TextView latinNameView;
    private EditText editBirdName;
    private ImageView birdImage;
    private ImageButton imageButton;
    //private ListView photosList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Bird bird;
    private ArrayList<BirdPhoto> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird);

        String birdId = getIntent().getStringExtra("BirdID");
        bird = BirdBank.get(this).getBird(Integer.parseInt(birdId));

        nameView = (TextView) findViewById(R.id.birdName);
        nameView.setText(bird.getName());
        latinNameView = (TextView) findViewById(R.id.latin_birdName);
        latinNameView.setText(bird.getLatinName());

        editBirdName = (EditText) findViewById(R.id.edit_bird_name_field);

        /*
        bird = getIntent().getParcelableExtra("Hej1");
        ArrayList<BirdPhoto> tmp = getIntent().getParcelableArrayListExtra("Hej2");
        bird.setPhotos(tmp);
        Log.d(TAG, "Name: "+birdId);
        Log.d(TAG, "Bird: " +bird.getName()+","+bird.getmId());
        birdnameView.setText(name); */

        photos = new ArrayList<BirdPhoto>();
        photos = bird.getPhotos();

        /*

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
        }); */


        /*
        ArrayList<BirdPhoto> photos1 = bird.getPhotos();
        BirdPhoto photo = bird.getPhotos().get(2);
        Log.d(TAG, "PhotoName:"+photo.getFileName());
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).
                getAbsolutePath(), photo.getFileName()); //"1464691368453.jpg");
        String fstr = f.getAbsolutePath();
        //Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
        birdImage = (ImageView) findViewById(R.id.birdImageView);
        birdImage.setImageURI(Uri.fromFile(f));
        */
      //  photosList = (ListView) findViewById(R.id.bird_photos_listView);


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        BirdCardAdapter adapter = new BirdCardAdapter(photos);
        mRecyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bird_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_take_pic :
                Log.d("LisftFragment", "Tryckte på take pic");
                Intent intent = new Intent(BirdActivity.this, CameraActivity.class);
                intent.putExtra(BIRD_NAME, bird.getName());
                intent.putExtra(BIRD_ID, bird.getmId());
                intent.putExtra(PHOTO_ID, bird.getPhotos().size());
                startActivityForResult(intent, REQUEST_PHOTO);

                return true;
            //TODO Lägg till så att man kan ändra
            case R.id.menu_item_edit_bird :
                nameView.setVisibility(View.INVISIBLE);
                editBirdName.setVisibility(View.VISIBLE);

                return true;
            case R.id.menu_item_delete_bird :
                //TODO implementera en delete, ge en popup som säger att
                //alla bilder kommer att tas bort
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
