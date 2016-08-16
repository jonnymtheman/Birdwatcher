package com.example.jonas.birdwatcher;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * File:       ${FILE_NAME}.java
 * Author:     Jonas Nyman
 * Assignment: Inlämningsuppgift 3 - Valfri Applikation
 * Course:     Utveckling av mobila applikationer
 * Version:    1.0
 */
public class BirdCardAdapter extends RecyclerView.Adapter<BirdCardAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<BirdPhoto> photos;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView birdNameText;
        private ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            birdNameText = (TextView) view.findViewById(R.id.card_bird_name);
            imageView = (ImageView) view.findViewById(R.id.bird_card_imageView);

        }

    }

    public BirdCardAdapter(ArrayList<BirdPhoto> photos) {
            //this.mContext = context;
            this.photos = photos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bird_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        BirdPhoto photo = photos.get(position);
        holder.birdNameText.setText(photo.getFileName());
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).
                getAbsolutePath(), photo.getFileName()); //"1464691368453.jpg");
        String fstr = f.getAbsolutePath();
        //Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
        holder.imageView.setImageURI(Uri.fromFile(f));
                      /*
        ArrayList<BirdPhoto> photos1 = bird.getPhotos();
        BirdPhoto photo = bird.getPhotos().get(2);
        Log.d(TAG, "PhotoName:"+photo.getFileName());
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).
                getAbsolutePath(), photo.getFileName()); //"1464691368453.jpg");
        String fstr = f.getAbsolutePath();
        //Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
        birdImage = (ImageView) findViewById(R.id.birdImageView);
        birdImage.setImageURI(Uri.fromFile(f));
        */
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }
}
