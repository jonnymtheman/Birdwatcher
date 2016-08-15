package com.example.jonas.birdwatcher;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * File:       Bird.java
 * Author:     Jonas Nyman
 * Assignment: Inlämningsuppgift 3 - Valfri Applikation
 * Course:     Utveckling av mobila applikationer
 * Version:    1.0
 */
public class Bird implements Parcelable {

    private int mId;

    private String name;

    private ArrayList<BirdPhoto> photos;

    public Bird(String name, int id) {
        this.name = name;
        mId = id;
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
        str += name+","+mId+",";

        for (BirdPhoto photo : photos) {
            str+= photo.getFileName()+";";
        }
        return str;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //TODO Fixa listan med photos
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{this.name,
                                    Integer.toString(this.mId)});
    }

    protected Bird(Parcel in) {
        //name = in.readString();
        String[] data = new String[2];

        in.readStringArray(data);
        this.name = data[0];
        this.mId = Integer.parseInt(data[1]);
        //this.grade = data[2];
    }
    public static final Creator<Bird> CREATOR = new Creator<Bird>() {
        @Override
        public Bird createFromParcel(Parcel in) {
            return new Bird(in);
        }

        @Override
        public Bird[] newArray(int size) {
            return new Bird[size];
        }
    };
}
