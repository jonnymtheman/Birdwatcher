package com.example.jonas.birdwatcher;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

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

    private final static String TAG = "BirdBank";
    private final Context appContext;

    private ArrayList<Bird> birds;

    private static BirdBank sBirdBank;

    public BirdBank(Context appContext) {
        this.appContext = appContext;

        birds = new ArrayList<Bird>();

        for (int i = 0; i < 10; i++) {
            Bird bird = new Bird("Name"+i, i);
            BirdPhoto photo = new BirdPhoto("Photo"+i+".jpg");
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



    /*
       Store a photo on external memory.

     */
    public void storeBirdPhoto(byte[] data, int birdId) {
        if (isExternalStorageWritable()) {
            Bird bird = getBird(birdId);
            Date date = new Date();
            String str = date.toString();
            String newFileName = "Photo" + bird.getName()+bird.getmId()
                                        +bird.getPhotos().size()+str+".jpg";

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
            storeBirdInfo(bird);
        }

    }

    //Ska spara bilderna på extrenal, kanske inte behövs
    private void storePhotos() {

    } //Name0.Photo0:Name001Mon Aug 15 14:59:04 GMT+02:00 2016.jpg:


    // Ska hämta sparade på internal memory och external memory
    //När appen startas. Tänk på filnamnen, kanske behöver lägga till
    //mer.
    public void loadBirds() {
        String str[] = this.appContext.fileList();
        for (String s : str) {
            if (s.startsWith("Bird:")) {
                FileInputStream in = null;
                try{
                    in = appContext.openFileInput(s);
                    //byte[] buffer = new byte[1024];
                    String str1 ="";
                    int re;
                    while ((re = in.read()) != -1) {
                        str1 += (char)re;
                    }
                    in.close();
                    //Näst nästa uppgift: Kolla om det finns lagrat birds, om inte returnera!
                    //Ta ut och skapa en bird
                   ArrayList<String> photos = getPhotoNames(str1);
                    //Check photos not null

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private ArrayList<String> getPhotoNames(String string) {
        ArrayList<String> photoNames = new ArrayList<String>();
        String[] namePhoto = string.split(",");
        String[] photos = namePhoto[1].split(";");
        int i = 0;
        for (String photoName : photos) {
            if (photoName.startsWith("Photo") && photoName.endsWith(".jpg")) {
                photoNames.add(photoName);
            }
        }
        return photoNames;
    }

    // Ska spara infon om fåglarna på internal storage
    public void storeBirdInfo(Bird bird) {
        Log.d(TAG, "inne i storebirdInfo");
        String filename = "Bird:"+bird.getName();
        FileOutputStream os = null;
        try {
            os = this.appContext.openFileOutput(filename, Context.MODE_PRIVATE);
            os.write(bird.toString().getBytes());
        } catch (Exception e) {
            Log.e(TAG, "Error writing to file " + filename, e);
        } finally {
            try {
                if (os != null)
                    os.close();
            } catch (Exception e) {
                Log.e(TAG, "Error closing file " + filename, e);
            }
        }

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

}
