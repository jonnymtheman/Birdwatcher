package com.example.jonas.birdwatcher;

import java.util.ArrayList;

/**
 * Hold a photo of a bird
 * File:       BirdPhoto.java
 * Author:     Jonas Nyman
 * Assignment: Inlämningsuppgift 3 - Valfri Applikation
 * Course:     Utveckling av mobila applikationer
 * Version:    1.0
 */
public class BirdPhoto {

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

}
