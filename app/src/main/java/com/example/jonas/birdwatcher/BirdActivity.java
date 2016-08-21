package com.example.jonas.birdwatcher;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
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
    private TextView editNameView;

    private EditText editBirdName;
    private EditText editLatinName;

    private LinearLayout editNameLayout;
    private LinearLayout editLatinNameLayout;
    private LinearLayout buttonLayout;

    private Button applyButt;
    private Button cancelButt;
    //private ImageView birdImage;
    //private ImageButton imageButton;
    private RecyclerView mRecyclerView;
    //private RecyclerView.Adapter mAdapter;
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
        editLatinName = (EditText) findViewById(R.id.edit_bird_latin_name_field);
        editNameView = (TextView) findViewById(R.id.birdName_edit);

        editNameLayout = (LinearLayout) findViewById(R.id.edit_name_layout);
        editLatinNameLayout = (LinearLayout) findViewById(R.id.edit_latin_name_layout);
        buttonLayout = (LinearLayout) findViewById(R.id.edit_name_button_layout);

        applyButt = (Button) findViewById(R.id.create_button_bird_activity);
        cancelButt = (Button) findViewById(R.id.cancel_button_bird_activity);

        photos = new ArrayList<BirdPhoto>();
        photos = bird.getPhotos();

        ArrayList<File> photoFiles = new ArrayList<File>();
        for (BirdPhoto photo : photos) {
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).
                    getAbsolutePath(), photo.getFileName());
            photoFiles.add(f);
        }



        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        BirdCardAdapter adapter = new BirdCardAdapter(photos, photoFiles);
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

            case R.id.menu_item_edit_bird :
                toggleEditFieldsVisibility();

                applyButt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, editBirdName.getText().toString()+","+
                        editLatinName.getText().toString());
                        applyChanges(editBirdName.getText().toString(),
                                editLatinName.getText().toString());
                        toggleEditFieldsVisibility();
                    }
                });
                cancelButt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Klickade cancel");
                        toggleEditFieldsVisibility();
                    }
                });
                return true;

            case R.id.menu_item_delete_bird :
                displayDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toggleEditFieldsVisibility() {
        if (nameView.getVisibility() == View.VISIBLE) {
            nameView.setVisibility(View.GONE);
            latinNameView.setVisibility(View.GONE);
            editNameLayout.setVisibility(View.VISIBLE);
            editLatinNameLayout.setVisibility(View.VISIBLE);
            buttonLayout.setVisibility(View.VISIBLE);
        } else  {
            nameView.setVisibility(View.VISIBLE);
            latinNameView.setVisibility(View.VISIBLE);
            editNameLayout.setVisibility(View.GONE);
            editLatinNameLayout.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.GONE);
        }
    }

    private void displayDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.delete_dialog_title);
        if (photos.size() == 0) {
            dialogBuilder.setMessage(R.string.delete_dialog_content);
        } else {
            dialogBuilder.setMessage(R.string.delete_dialog_content_phots);
        }
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "Clicked ok in dialog");
                applyDeleteBird();
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "Clicked cancel in dialog");
            }
        });

        AlertDialog deleteDialog = dialogBuilder.create();
        deleteDialog.show();

    }

    private void applyDeleteBird() {
        BirdBank.get(this).deleteBirdInfo(bird);
        finish();
    }

    private void applyChanges(String name, String latinName) {
        Bird newBird = new Bird(name, latinName, bird.getmId());
        BirdBank.get(this).deleteBirdInfo(bird);
        ArrayList<Bird> bird1 = BirdBank.get(this).getBirds();
        BirdBank.get(this).storeBirdInfo(newBird);
        ArrayList<Bird> bird2 = BirdBank.get(this).getBirds();
        bird = newBird;
        bird.setName(name);
        bird.setLatinName(latinName);
        //BirdBank.get(this).updateBird(bird, name, latinName);

        nameView.setText(bird.getName());
        latinNameView.setText(bird.getLatinName());
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
