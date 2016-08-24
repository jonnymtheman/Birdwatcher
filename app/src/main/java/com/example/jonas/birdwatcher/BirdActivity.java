package com.example.jonas.birdwatcher;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;

/**
 * BirdActivity is the activity that displays information
 * about a bird. The bird's photos are displayed in an
 * image switcher and the app bar gives options to take
 * a picture, edit the bird and delete the bird from the
 * application.
 *
 * File:       BirdActivity.java
 * Author:     Jonas Nyman
 * Assignment: Inlämningsuppgift 3 - Valfri Applikation
 * Course:     Utveckling av mobila applikationer
 * Version:    1.0
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
    private Button deleteButton;

    private int currIndex;
    private Bird bird;
    private ArrayList<BirdPhoto> photos;
    private ArrayList<File> photoFiles;

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

        //ImageSwitcher to display images of birds
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
                    //Scale images for improved performance
                    @Override
                    protected Bitmap doInBackground(ImageView... imageViews) {
                        v = imageViews[0];
                        if (photoFiles.size() != 0) {
                            Bitmap bitmap = BitmapFactory.decodeFile(
                                    photoFiles.get(currIndex).getAbsolutePath());
                            if (bitmap != null) {
                                int imageSize = (int) (bitmap.getHeight()
                                        * (1100.0 / bitmap.getWidth()));
                                Bitmap scaledImage = Bitmap.createScaledBitmap(bitmap,
                                        1100, imageSize, true);
                                return scaledImage;
                            }
                        }
                        return null;
                    }
                    //Set scaled image to the ImageSwitcher
                    @Override
                    protected void onPostExecute(Bitmap bm) {
                        if (bm != null) {
                            v.setImageBitmap(bm);
                        }
                    }

                }.execute(myView);
                //Start new activity if user presses on the image
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

        deleteButton = (Button) findViewById(R.id.delete_picture_button);
        if (photoFiles.isEmpty()) {
            deleteButton.setEnabled(false);
            deleteButton.setVisibility(View.INVISIBLE);
        }
        deleteButton.setOnClickListener(new View.OnClickListener() {
            //Display dialog to confirm deletion of picture
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getContext());
                dialogBuilder.setTitle(R.string.delete_dialog_card_title).
                        setMessage(R.string.delete_dialog_card_msg).
                        setCancelable(true);
                dialogBuilder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletePicture();
                    }
                });
                dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Do nothing, user clicked cancel
                    }
                });

                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });
    }

    /**
     * Delete a picture by calling BirdBank's deleteBirdPhoto.
     * UI components are updated and another picture is displayed
     * in the image switcher.
     */
    private void deletePicture() {
        String tmp = photoFiles.get(currIndex).getName();
        photoFiles.remove(currIndex);
        BirdBank.get(this).deleteBirdPhoto(tmp, bird);

        if (photoFiles.isEmpty()) {
            mSwitcher.setImageDrawable(null);
            deleteButton.setVisibility(View.INVISIBLE);
            deleteButton.setEnabled(false);
            mImagePhotoView.setText(null);
        } else {
            displayNextImage(mSwitcher);
        }
    }

    /**
     * Display the next image on the imageswitcher.
     * The value of currIndex decides if the next or the
     * previous bird image in the photo list are to be displayed.
     *
     * @param v Imageswitcher to display the next image.
     */
    private void displayNextImage(View v) {
        if (currIndex > photoFiles.size()-1) {
            currIndex = 0;
        } else if (currIndex < 0) {
            currIndex = photoFiles.size()-1;
        }
        if (photoFiles.size() != 0) {
            mSwitcher.setImageURI(Uri.fromFile(photoFiles.get(currIndex).getAbsoluteFile()));
            mImagePhotoView.setText(photoFiles.get(currIndex).getName());
        }
        mNextImageButton.setEnabled(true);
        mPrevImageButton.setEnabled(true);
    }

    /**
     * Start the BirdImageActivity that displays a larger image.
     *
     * @param photoName Name of photo to be displayed.
     */
    public void showLargerImage(String photoName) {
        Intent intent = new Intent(BirdActivity.this, BirdImageActivity.class);
        intent.putExtra("Name", photoName);
        startActivity(intent);
    }

    /**
     * Inflate the app bar menu bird_edit_menu.
     *
     * @param menu Menu to be inflated.
     * @return True.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bird_edit_menu, menu);
        return true;
    }

    /**
     * Decide action depending on which app bar icon the user taps.
     * The camera is started within a AsyncTask to improve performance.
     *
     * @param item Menu item pressed.
     * @return True or Menu item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_take_pic :
                PackageManager pm = getPackageManager();
                if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    //Start camera activity in AsyncTask
                    new AsyncTask<Integer, Void, String>() {
                        @Override
                        protected String doInBackground(Integer... integers) {
                            //Intent intent = new Intent(BirdActivity.this, CameraActivity.class);
                            Intent intent = new Intent(BirdActivity.this, BirdCameraActivity.class);
                            intent.putExtra(BIRD_NAME, bird.getName());
                            intent.putExtra(BIRD_ID, bird.getmId());
                            intent.putExtra(PHOTO_ID, bird.getPhotos().size());
                            startActivityForResult(intent, integers[0]);
                            return null;
                        }
                    }.execute(1);

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
                        toggleEditFieldsVisibility();
                    }
                });
                return true;

            case R.id.menu_item_delete_bird :
                displayDeleteBirdDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Toggles the Edit text fields visibility to allow
     * the user to edit the Bird.
     */
    private void toggleEditFieldsVisibility() {
        if (nameView.getVisibility() == View.VISIBLE) {
            nameView.setVisibility(View.GONE);
            latinNameView.setVisibility(View.GONE);
            editNameLayout.setVisibility(View.VISIBLE);
            editLatinNameLayout.setVisibility(View.VISIBLE);
            buttonLayout.setVisibility(View.VISIBLE);
        } else {
            nameView.setVisibility(View.VISIBLE);
            latinNameView.setVisibility(View.VISIBLE);
            editNameLayout.setVisibility(View.GONE);
            editLatinNameLayout.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Display a dialog confirming that the user indeed wishes to
     * delete the bird permanently.
     */
    private void displayDeleteBirdDialog() {

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
                applyDeleteBird();
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Do nothing, user pressed cancel
            }
        });
        AlertDialog deleteDialog = dialogBuilder.create();
        deleteDialog.show();
    }

    /**
     * Delete current bird from the Bird bank and finish the activity.
     */
    private void applyDeleteBird() {
        BirdBank.get(this).deleteBirdInfo(bird);
        finish();
    }

    /**
     * Apply the changes after the Bird has been modified.
     *
     * @param name New bird's name.
     * @param latinName New latin bird's name.
     */
    private void applyChanges(String name, String latinName) {
        Bird newBird = new Bird(name, latinName, bird.getmId());
        newBird.setPhotos(bird.getPhotos());
        BirdBank.get(this).deleteBirdInfo(bird);
        BirdBank.get(this).storeBirdInfo(newBird);
        bird = newBird;

        nameView.setText(bird.getName());
        latinNameView.setText(bird.getLatinName());
    }

    /**
     * Retrieve the photo taken by the camera activity.
     *
     * @param requestCode Request code.
     * @param resultCode Result code.
     * @param data Intent containing the filename with key "Filename".
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            if (data != null) {
                String filename = data
                        .getStringExtra("Filename");
                if (filename != null) {
                    bird = BirdBank.get(this).getBird(bird.getmId());
                    File f = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).
                            getAbsolutePath(), filename);
                    photoFiles.add(f);
                    currIndex = photoFiles.size() - 1;
                    displayNextImage(mSwitcher);
                    deleteButton.setVisibility(View.VISIBLE);
                    deleteButton.setEnabled(true);
                }
            }
        }
    }
}
