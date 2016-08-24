package com.example.jonas.birdwatcher;

/**
 * Represents a photo of a Bird by holding the file name
 * of a saved image.
 *
 * File:       BirdPhoto.java
 * Author:     Jonas Nyman
 * Assignment: Inlämningsuppgift 3 - Valfri Applikation
 * Course:     Utveckling av mobila applikationer
 * Version:    1.0
 */
public class BirdPhoto  {

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