package com.example.jonas.birdwatcher;

import java.util.ArrayList;

/**
 * Represents a Bird.
 * A Bird object contains a unique id, a bird's latin and full name
 * and a list of BirdPhotos associated with the Bird.
 *
 * File:       Bird.java
 * Author:     Jonas Nyman
 * Assignment: Inlämningsuppgift 3 - Valfri Applikation
 * Course:     Utveckling av mobila applikationer
 * Version:    1.0
 */
public class Bird  {

    private int mId;
    private String name;

    public String getLatinName() {
        return latinName;
    }

    public void setLatinName(String latinName) {
        this.latinName = latinName;
    }

    private String latinName;
    private ArrayList<BirdPhoto> photos;

    public Bird(String name, String latinName, int id) {
        this.name = name;
        this.latinName = latinName;
        this.mId = id;
        photos = new ArrayList<BirdPhoto>();

    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotos(ArrayList<BirdPhoto> photos) {
        this.photos = photos;
    }

    public void addPhoto(BirdPhoto photo) {
        photos.add(photo);
    }

    public ArrayList<BirdPhoto> getPhotos() {

        return photos;
    }

    @Override
    public String toString() {
        String str = "";
        str += name+","+latinName+","+mId+",";

        for (BirdPhoto photo : photos) {
            str+= photo.getFileName()+";";
        }
        return str;
    }

}
