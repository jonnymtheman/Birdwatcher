package com.example.jonas.birdwatcher;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Ska hålla alla birds, kan vid uppstard ladda in alla fåglar
 *
 *
 * File:       ${FILE_NAME}.java
 * Author:     Jonas Nyman
 * Assignment: Inlämningsuppgift 3 - Valfri Applikation
 * Course:     Utveckling av mobila applikationer
 * Version:    1.0
 */
public class BirdBank {

    private final Context appContext;

    private ArrayList<Bird> birds;

    private static BirdBank sBirdBank;

    public BirdBank(Context appContext) {
        this.appContext = appContext;

        birds = new ArrayList<Bird>();

        for (int i = 0; i < 10; i++) {
            Bird bird = new Bird("Name"+i, i);
            BirdPhoto photo = new BirdPhoto("Photo"+i);
            ArrayList<BirdPhoto> photos = new ArrayList<BirdPhoto>();
            photos.add(photo);
            bird.setPhotos(photos);
            birds.add(bird);

        }
    }



    public static BirdBank get(Context context) {
        if (sBirdBank == null) {
            sBirdBank = new BirdBank(context.getApplicationContext());
        }
        return sBirdBank;
    }

    public ArrayList<Bird> getBirds() {
        return birds;
    }

    public void updateBird(Bird bird) {
        for (Bird b : birds) {
            if (b.getmId() == bird.getmId()) {
                b.setPhotos(bird.getPhotos());
            }
        }
    }

    public Bird getBird(int id) {
        for (Bird bird : birds) {
            if (bird.getmId() == id) {
                return bird;
            }
        }
        return null;
    }

    // Ska hämta sparade på internal memory och external memory
    //När appen startas. Tänk på filnamnen, kanske behöver lägga till
    //mer.
    public void loadBirds() {

    }

    /*
        Denna sparar just nu en bild, byt namn på metoden.

     */
    public void storeBirds(byte[] data, int birdId) {
        if (isExternalStorageWritable()) {
            Bird bird = getBird(birdId);
            String newFileName = bird.getName()+bird.getmId()
                                        +bird.getPhotos().size()+".jpg";

            File f = getAlbumStorageDir(this.appContext, "Birds");
            File file = new File(f, newFileName);
            if (f.exists()) {
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(data);
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            bird.addPhoto(new BirdPhoto(newFileName)); //Kanske måste spara mer än namnet för när man ska hämta
        }

    }

    //Ska spara bilderna på extrenal, kanske inte behövs
    private void storePhotos() {

    }

    // Ska spara infon om fåglarna på internal storage
    private void storeBirdInfo() {

    }

    //TODO Directoryn blir inte skapad
    public File getAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        Log.d("TAG", "Sparat: "+ file.getAbsolutePath());
        if (!file.mkdirs()) {
            Log.e("LOG_TAG", "Directory not created");
        }
        return file;
    }

    private boolean isExternalStorageWritable() {

        String avaiable = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(avaiable)) {
            return true;
        } else {
            return false;
        }
    }
    /*
        private boolean saveCrimes() {
        try {
            mSerializer.saveCrimes(mCrimes);
            Log.d(TAG, "crimes saved to file");
            return true;
        } catch (Exception e ) {
            Log.e(TAG, "Error saving crimes: " + e);
            return false;
        }
    }
     */
}
