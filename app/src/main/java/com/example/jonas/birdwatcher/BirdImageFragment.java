package com.example.jonas.birdwatcher;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

/**
 * File:       ${FILE_NAME}.java
 * Author:     Jonas Nyman
 * Assignment: Inlämningsuppgift 3 - Valfri Applikation
 * Course:     Utveckling av mobila applikationer
 * Version:    1.0
 */
public class BirdImageFragment extends DialogFragment {

    private ImageView mImageView;

    public static BirdImageFragment newInstance(String photoName) {
        Bundle args = new Bundle();
        args.putSerializable("Name", photoName);

        BirdImageFragment fragment = new BirdImageFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        mImageView = new ImageView(getActivity());
        String path = (String) getArguments().getSerializable("Name");
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).
                getAbsolutePath(), path);
        Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
        mImageView.setImageBitmap(bitmap);
        return mImageView;
    }
}
