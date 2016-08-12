package com.example.jonas.birdwatcher;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;

import java.util.UUID;

/**
 * File:       ${FILE_NAME}.java
 * Author:     Jonas Nyman
 * Assignment: Inlämningsuppgift 3 - Valfri Applikation
 * Course:     Utveckling av mobila applikationer
 * Version:    1.0
 */
@SuppressLint("NewApi")
public class BirdFragment extends Fragment {

    private Bird bird;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       // int birdId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);

      //  bird = BirdBank.get(getActivity()).getBird(birdId);

    }


}
