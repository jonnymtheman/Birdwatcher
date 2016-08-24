package com.example.jonas.birdwatcher;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/*
Detta går nog inte att fixa bra för att cardviewn suger om det är >= 4 bilder.
Öppna en lista med bara filnamnen där man kan ta bort bilder,
en fullösning men det borde funka. Kolla buggar i BirdBank bara.

En till grej, om man har stängt av camera permission så kommer den inte
att upptäcka det.

TODO kolla nullpointer om man redigerar en fågel
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
    private ImageSwitcher mSwitcher;
    private Button mNextImageButton;
    private Button mPrevImageButton;
    private TextView mImagePhotoView;
    private int currIndex;
    private ArrayList<File> photoFiles;

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

        photoFiles = new ArrayList<File>();
        for (BirdPhoto photo : photos) {
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).
                    getAbsolutePath(), photo.getFileName());
            photoFiles.add(f);
        }

        currIndex = 0;

        mSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        mSwitcher.setFactory(new ImageSwitcher.ViewFactory() {
            @Override
            public View makeView() {

                ImageView myView = new ImageView(getApplicationContext());
                myView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT
                ));

                myView.setScaleType(ImageView.ScaleType.CENTER);
                new AsyncTask<ImageView, Void, Bitmap>() {
                    ImageView v;

                    @Override
                    protected Bitmap doInBackground(ImageView... imageViews) {
                        v = imageViews[0];

                        if (photoFiles.size() != 0) {
                            Bitmap bitmap = BitmapFactory.decodeFile(photoFiles.get(currIndex).getAbsolutePath());
                            if (bitmap != null) {
                                int nh = (int) (bitmap.getHeight() * (1100.0 / bitmap.getWidth())); //512.0
                                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1100, nh, true); //512
                                return scaled;
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bm) {
                        if (bm != null) {
                            v.setImageBitmap(bm);
                        }
                    }

                }.execute(myView);
                myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (photoFiles.size() != 0) {
                            showLargerImage(photoFiles.get(currIndex).getName());
                        }
                    }
                });

                return myView;
            }

        });

        mImagePhotoView = (TextView) findViewById(R.id.image_photo_name);
        if (photoFiles.size() != 0) {
            mImagePhotoView.setText(photoFiles.get(currIndex).getName());
        }

        mNextImageButton = (Button) findViewById(R.id.next_image_button);
        mNextImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNextImageButton.setEnabled(false);
                currIndex++;
                displayNextImage(mSwitcher);

            }
        });

        mPrevImageButton = (Button) findViewById(R.id.prev_image_button);
        mPrevImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPrevImageButton.setEnabled(false);
                currIndex--;
                displayNextImage(mSwitcher);
            }
        });
    }


    private void displayNextImage(View v) {
        if (currIndex > photoFiles.size()-1) {
            currIndex = 0;
        } else if (currIndex < 0) {
            currIndex = photoFiles.size()-1;
        }
        Log.d(TAG, "Currindex: " +currIndex);
        if (photoFiles.size() != 0) {
            mSwitcher.setImageURI(Uri.fromFile(photoFiles.get(currIndex).getAbsoluteFile()));
            mImagePhotoView.setText(photoFiles.get(currIndex).getName());
        }
        mNextImageButton.setEnabled(true);
        mPrevImageButton.setEnabled(true);
    }


    private void changeImage(View v) {
        currIndex++;
        Bitmap bmImg = BitmapFactory.decodeFile(photoFiles.get(currIndex).getAbsolutePath());
        mSwitcher.setImageURI(Uri.fromFile(photoFiles.get(currIndex).getAbsoluteFile()));
    }

    public void showLargerImage(String photoname) {
        Intent intent = new Intent(BirdActivity.this, BirdImageActivity.class);
        intent.putExtra("Name", photoname);
        startActivity(intent);
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
                Log.d(TAG, "Tryckte på take pic");
                PackageManager pm = getPackageManager();
                if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    new AsyncTask<Integer, Void, String>() {

                        @Override
                        protected String doInBackground(Integer... integers) {
                            Intent intent = new Intent(BirdActivity.this, CameraActivity.class);
                            intent.putExtra(BIRD_NAME, bird.getName());
                            intent.putExtra(BIRD_ID, bird.getmId());
                            intent.putExtra(PHOTO_ID, bird.getPhotos().size());
                            startActivityForResult(intent, integers[0]);
                            return null;
                        }
                    }.execute(REQUEST_PHOTO);

                }
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
        newBird.setPhotos(bird.getPhotos());
        BirdBank.get(this).deleteBirdInfo(bird);
        ArrayList<Bird> bird1 = BirdBank.get(this).getBirds();
        BirdBank.get(this).storeBirdInfo(newBird);
        ArrayList<Bird> bird2 = BirdBank.get(this).getBirds();
        bird = newBird;

        nameView.setText(bird.getName());
        latinNameView.setText(bird.getLatinName());
    }

    private void showPhoto() {


    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //Förutom det finns en bugg när man redigerar fågeln
    //Annars allt klart och gör layout-land

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("POPO", "Requestcode:"+requestCode);
        if (resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "Här inne");
            String filename = data
                    .getStringExtra("Filename");
            if (filename != null) {
                bird = BirdBank.get(this).getBird(bird.getmId());
                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).
                        getAbsolutePath(), filename);
                photoFiles.add(f);
                currIndex = photoFiles.size()-1;
                displayNextImage(mSwitcher);
            }
            return;
        } else if (requestCode == REQUEST_PHOTO) {
                Log.d(TAG, "Efter kollen");
            // create a new Photo object and attach it to the crime
            String filename = data
                    .getStringExtra(BirdCameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null) {
                Log.d(TAG, "Fick tillbaka: "+filename);
                BirdPhoto photo = new BirdPhoto(filename);
                ArrayList<BirdPhoto> photos = new ArrayList<BirdPhoto>();
                photos.add(photo);
                bird.setPhotos(photos);
                BirdBank.get(this).updateBird(bird);
                //Photo p = new Photo(filename);
                //mCrime.setPhoto(p);
                showPhoto();
                Log.d(TAG, filename);
                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).
                        getAbsolutePath(), photo.getFileName());
                photoFiles.add(f);
                displayNextImage(mSwitcher);
                //BirdBank.get(this).storeBirds();
            }
        }
    }
}
