package com.example.jonas.birdwatcher;

import android.support.v4.app.Fragment;

/**
 * Activity that holds a BirdListFragment that
 * displays a list of all the Birds.
 *
 * File:       BirdListActivity.java
 * Author:     Jonas Nyman
 * Assignment: Inlämningsuppgift 3 - Valfri Applikation
 * Course:     Utveckling av mobila applikationer
 * Version:    1.0
 */
public class BirdListActivity extends SingleFragmentActivity {

        @Override
        protected Fragment createFragment() {
            return new BirdListFragment();
        }
}
