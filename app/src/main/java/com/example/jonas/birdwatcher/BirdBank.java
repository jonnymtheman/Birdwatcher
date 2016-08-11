package com.example.jonas.birdwatcher;

import android.content.Context;

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
