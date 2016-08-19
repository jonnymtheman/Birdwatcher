package com.example.jonas.birdwatcher;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Ska hålla alla birds, kan vid uppstard ladda in alla fåglar
 * TODO lägg till deletemetoder
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

     /*   for (int i = 0; i < 10; i++) {
            Bird bird = new Bird("Name"+i, i);
            BirdPhoto photo = new BirdPhoto("Photo"+i+".jpg");
            ArrayList<BirdPhoto> photos = new ArrayList<BirdPhoto>();
            photos.add(photo);
            bird.setPhotos(photos);
            birds.add(bird);

        } */
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
                b.setName(bird.getName());
                b.setLatinName(bird.getLatinName());
                storeBirdInfo(b);
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
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat ("Eyyyy.MM.ddhh:mm:ss");

            String dateStr = dateFormat.format(date);
            System.out.println("Current Date: " + dateFormat.format(date));
            String newFileName = "Photo"+bird.getmId()
                                        +bird.getPhotos().size()+dateStr+".jpg";

            //File f = getAlbumStorageDir(this.appContext, "Birds");
            //File file = new File(f, newFileName);
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File file = new File(path, newFileName);

           // if (file.exists()) {
                try {
                    path.mkdirs();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(data);
                    out.close();

                   /* MediaScannerConnection.scanFile(this,
                            new String[] { file.toString() }, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> uri=" + uri);
                                }
                            }); */
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
           // }
            bird.addPhoto(new BirdPhoto(newFileName)); //Kanske måste spara mer än namnet för när man ska hämta
            storeBirdInfo(bird);
        }

    }

    //TODO implementera
    //TODO kolla om det är värt att ladda in/deleta i egna trådar(förmodligen inte)
    public void deleteBirdPhoto(String photoName) {

    }
    // Ska hämta sparade på internal memory och external memory
    //När appen startas. Tänk på filnamnen, kanske behöver lägga till
    //mer. TODO hämta latin name
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
                    BufferedReader red = new BufferedReader(new InputStreamReader(in));




                    StringBuilder sb =  new StringBuilder();
                    while ((str1 = red.readLine()) != null) {
                        sb.append(str1);
                        // do something with your read line
                    }
                    str1 = sb.toString();
                    red.close();
                   // while ((re = in.read()) != -1) {
                   //     str1 += (char)re;
                   // }
                   // in.close();
                    //Näst nästa uppgift: Kolla om det finns lagrat birds, om inte returnera!
                    //Ta ut och skapa en bird
                    ArrayList<String> nameId = extractBirdName(str1);
                    Bird bird = new Bird(nameId.get(0),nameId.get(1) ,Integer.parseInt(nameId.get(2)));
                   // bird.setLatinName(nameId.get(2));
                   ArrayList<String> photos = getPhotoNames(str1);
                    if (photos != null) {
                        ArrayList<BirdPhoto> birdPhotos = new ArrayList<BirdPhoto>();
                        for (String photoStr : photos) {
                            birdPhotos.add(new BirdPhoto(photoStr));
                        }
                        bird.setPhotos(birdPhotos);
                    }
                    birds.add(bird);
                  //  File f = getAlbumStorageDir(this.appContext, "Birds");
                  //  File[] files = f.listFiles();
                  //  int i = 1;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ArrayList<String> extractBirdName(String str) {
        ArrayList<String> nameId = new ArrayList<String>();
        String[] tmp = str.split(",");
        nameId.add(tmp[0]);
        nameId.add(tmp[1]);
        nameId.add(tmp[2]);

        return nameId;
    }

    private ArrayList<String> getPhotoNames(String string) {
        ArrayList<String> photoNames = new ArrayList<String>();
        String[] namePhoto = string.split(",");
        if (namePhoto.length == 4) {
            String[] photos = namePhoto[3].split(";");
            int i = 0;
            for (String photoName : photos) {
                if (photoName.startsWith("Photo") && photoName.endsWith(".jpg")) {
                    photoNames.add(photoName);
                }
            }
        }
        return photoNames;
    }

    // Ska spara infon om fåglarna på internal storage
    //TODO fixa latin name
    public void storeBirdInfo(Bird bird) {
        Log.d(TAG, "inne i storebirdInfo");
        String filename = "Bird:"+bird.getName();
        FileOutputStream os = null;
        String te = bird.toString();
        try {
            os = this.appContext.openFileOutput(filename, Context.MODE_PRIVATE);
            //Writer writer = new OutputStreamWriter(os, Charset.forName("UTF-8"));
            //writer.write(bird.toString());
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
        if (!isBirdAdded(bird)) {
            birds.add(bird);
        }

        int k = 2;
    }

    public void deleteBirdInfo(Bird bird) {
        Log.d(TAG, "inne i deletebirdInfo");
        String filename = "Bird:"+bird.getName();
        int count = 0;
        for (Bird b : birds) {
            if (b.getmId() == bird.getmId()) {
                if (b.getName() == bird.getName()) {
                    birds.remove(count);
                    break;
                }
            }
            count++;
        }
        this.appContext.deleteFile(filename);
    }

    private boolean isBirdAdded(Bird bird) {
        for (Bird b : birds) {
            if (b.getmId() == bird.getmId()) {
                return true;
            }
        }
        return false;
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
