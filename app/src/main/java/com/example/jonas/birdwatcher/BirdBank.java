package com.example.jonas.birdwatcher;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * BirdBank enables the application to access the birds.
 * It is responsible for loading and storing Bird objects
 * and Photos taken by the application.
 *
 * This class has been inspired by the class CrimeLab from the book
 * Android Programming, The Big Nerd Ranch Guide - Brian Hardy, Bill Philips.
 *
 * File:       BirdBank.java
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
    }

    /**
     * Creates, if neccessary, an instance of BirdBank.
     *
     * @param context Activity Context
     * @return instance of BirdBank
     */
    public static BirdBank get(Context context) {
        if (sBirdBank == null) {
            sBirdBank = new BirdBank(context.getApplicationContext());
        }
        return sBirdBank;
    }

    public ArrayList<Bird> getBirds() {
        return birds;
    }

    public Bird getBird(int id) {
        for (Bird bird : birds) {
            if (bird.getmId() == id) {
                return bird;
            }
        }
        return null;
    }

    /**
     * Stores a photo taken by the application on the external Memory Card.
     * Location of storage is the Pictures folder.
     *
     * After the picture is stored, it is added to the current Bird.
     *
     * @param data Byte array of picture.
     * @param birdId Id of bird associated with the picture.
     */
    public String storeBirdPhoto(byte[] data, int birdId) {
        String retStr = "";
        //Check permission to write data to memory card.
        if (Environment.MEDIA_MOUNTED.
                equals(Environment.getExternalStorageState())) {

            Bird bird = getBird(birdId);
            Date date = new Date();
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat ("Eyyyy.MM.ddhh:mm:ss");

            String dateStr = dateFormat.format(date);
            System.out.println("Current Date: " + dateFormat.format(date));
            String newFileName = "Photo"+bird.getmId()
                                        +bird.getPhotos().size()+dateStr+".jpg";

            //Get file from the Pictures directory
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File file = new File(path, newFileName);

                try {
                    path.mkdirs();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(data);
                    out.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    displayToast();
                }
            bird.addPhoto(new BirdPhoto(newFileName));
            storeBirdInfo(bird);
            return retStr +=newFileName;
        }
        return  retStr;
    }

    /**
     * Deletes a picture of a bird stored in the Pictures directory.
     *
     * @param photoName Name of picture to be deleted
     */
    public void deleteBirdPhoto(String photoName, Bird bird) {
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        int count = 0;
        boolean deletePhoto = false;
        boolean deleted = false;
        for (BirdPhoto photo : bird.getPhotos()) {
            if (photo.getFileName().equals(photoName)) {
                deletePhoto = true;
                break;
            }
            count++;
        }
        if (deletePhoto) {
            ArrayList<BirdPhoto> tmp = bird.getPhotos();
            tmp.remove(count);
            Bird tmpBird = bird;
            deleteBirdInfo(bird);
            tmpBird.setPhotos(tmp);
            storeBirdInfo(tmpBird);
            File file = new File(path, photoName);
            deleted = file.delete();
        }

        if (!deleted) {
            displayToast();
        }
    }

    /**
     * Removes a photo from a Bird's photo list.
     *
     * @param photoName Name of BirdPhoto.
     * @param bird Bird to remove the BirdPhoto from.
     */
    private void removePhotoFromBird(String photoName, Bird bird) {
        ArrayList<BirdPhoto> tmp = bird.getPhotos();
        if (tmp != null) {
            int count = 0;
            for (BirdPhoto photo : tmp) {
                if (photo.getFileName().equals(photoName)) {
                    tmp.remove(count);
                    bird.setPhotos(tmp);
                    break;
                }
                count++;
            }
        }
    }

    /**
     * Loads bird info from the internal memory.
     * Reads the file using a BufferedReader and creates
     * Bird objects which are added to the arraylist of Bird's.
     */
    public void loadBirds() {
        String fileList[] = this.appContext.fileList();
        for (String s : fileList) {
            if (s.startsWith("Bird:")) {
                FileInputStream inputStream = null;
                try{
                    inputStream = appContext.openFileInput(s);
                    String inputString ="";
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream));

                    StringBuilder stringBuilder =  new StringBuilder();
                    while ((inputString = reader.readLine()) != null) {
                        stringBuilder.append(inputString);
                    }
                    inputString = stringBuilder.toString();
                    reader.close();

                    //Create a Bird object
                    ArrayList<String> birdContent = extractBirdName(inputString);
                    Bird bird = new Bird(birdContent.get(0),birdContent.get(1),
                            Integer.parseInt(birdContent.get(2)));
                    ArrayList<String> photos = getPhotoNames(inputString);
                    if (photos != null) {
                        ArrayList<BirdPhoto> birdPhotos = new ArrayList<BirdPhoto>();
                        for (String photoStr : photos) {
                            birdPhotos.add(new BirdPhoto(photoStr));
                        }
                        bird.setPhotos(birdPhotos);
                    }
                    birds.add(bird);

                } catch (IOException e) {
                    e.printStackTrace();
                    displayToast();
                }
            }
        }
    }

    /**
     * Extracts the bird content of a string from Bird.toString().
     * The format of the string is "Name,Latin name,id".
     *
     * @param str String from Bird.toString()
     * @return Arraylist of Name, Latin name and id.
     */
    private ArrayList<String> extractBirdName(String str) {
        ArrayList<String> birdContent = new ArrayList<String>();
        String[] tmp = str.split(",");
        birdContent.add(tmp[0]);
        birdContent.add(tmp[1]);
        birdContent.add(tmp[2]);

        return birdContent;
    }

    private ArrayList<String> getPhotoNames(String string) {
        ArrayList<String> photoNames = new ArrayList<String>();
        String[] namePhoto = string.split(",");
        if (namePhoto.length == 4) {
            String[] photos = namePhoto[3].split(";");
            int i = 0;
            for (String photoName : photos) {
                if (photoName.startsWith("Photo") &&
                        photoName.endsWith(".jpg")) {
                    photoNames.add(photoName);
                }
            }
        }
        return photoNames;
    }

    /**
     * Stores a Bird object on the applications internal memory as a file.
     * The filename is "Bird:" followed by the Bird's name and the content
     * of the file is the result of the Bird's toString(), which returns
     * a string containing information about the bird and its photo names.
     *
     * @param bird Bird object to store on the internal memory,
     */
    public void storeBirdInfo(Bird bird) {
        String filename = "Bird:"+bird.getName();
        FileOutputStream os = null;
        try {
            os = this.appContext.openFileOutput(filename, Context.MODE_PRIVATE);
            os.write(bird.toString().getBytes());
        } catch (Exception e) {
            Log.e(TAG, "Error writing to file " + filename, e);
            displayToast();
        } finally {
            try {
                if (os != null)
                    os.close();
            } catch (Exception e) {
                displayToast();
            }
        }
        if (!isBirdAdded(bird)) {
            birds.add(bird);
        }
    }

    private void displayToast() {
        Toast.makeText(appContext, "Unable access storage",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Delete a bird from the internal application's memory.
     *
     * The bird is removed from the bank's internal list of birds
     * and then removed from the application's internal memory.
     * Any pictures associated with the bird is left to the user
     * to handle.
     *
     * @param bird Bird to be removed.
     */
    public void deleteBirdInfo(Bird bird) {
        String filename = "Bird:"+bird.getName();
        int count = 0;
        for (Bird b : birds) {
            if (b.getmId() == bird.getmId()) {
                if (b.getName().equals(bird.getName())) {
                    birds.remove(count);
                    break;
                }
            }
            count++;
        }
        this.appContext.deleteFile(filename);
    }

    /**
     * Check if a Bird already exists in the bank's internal
     * list of Birds.
     *
     * @param bird Bird to check if it exists in the bank.
     * @return Boolean if the Bird exists.
     */
    private boolean isBirdAdded(Bird bird) {
        for (Bird b : birds) {
            if (b.getmId() == bird.getmId()) {
                return true;
            }
        }
        return false;
    }


}
