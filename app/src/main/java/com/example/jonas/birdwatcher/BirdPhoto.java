package com.example.jonas.birdwatcher;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * TODO ta bort parceble
 * Hold a photo of a bird
 * File:       BirdPhoto.java
 * Author:     Jonas Nyman
 * Assignment: Inlämningsuppgift 3 - Valfri Applikation
 * Course:     Utveckling av mobila applikationer
 * Version:    1.0
 */
public class BirdPhoto implements Parcelable {

    private String fileName;


    public BirdPhoto(String fileName) {

        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(fileName);
    }

    protected BirdPhoto(Parcel in) {
        this.fileName = in.readString();

        //this.grade = data[2];
    }
    public static final Creator<BirdPhoto> CREATOR = new Creator<BirdPhoto>() {
        @Override
        public BirdPhoto createFromParcel(Parcel in) {
            return new BirdPhoto(in);
        }

        @Override
        public BirdPhoto[] newArray(int size) {
            return new BirdPhoto[size];
        }
    };
}
