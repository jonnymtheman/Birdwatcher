package com.example.jonas.birdwatcher;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BirdListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new BirdListFragment();
    }


}
