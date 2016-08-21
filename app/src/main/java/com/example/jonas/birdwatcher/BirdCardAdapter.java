package com.example.jonas.birdwatcher;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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
    private Button deleteButton;

    private String photoName;
    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public MyViewHolder(View view) {
            super(view);

            photoName = "";

            imageView = (ImageView) view.findViewById(R.id.bird_card_imageView);
            deleteButton = (Button) view.findViewById(R.id.delete_card_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getContext());
                    dialogBuilder.setTitle(R.string.delete_dialog_card_title).
                            setMessage(R.string.delete_dialog_card_msg).
                            setCancelable(true);
                    dialogBuilder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d("TAG", "Clicked ok in dialog");
                            BirdBank.get(view.getContext()).deleteBirdPhoto(photoName);
                            int count = 0;
                            for (BirdPhoto photo1 : photos) {
                                if (photo1.getFileName().equals(photoName)) {
                                    photos.remove(count);
                                }
                                count++;
                            }

                            updatePhotoList();
                        }
                    });
                    dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d("TAG", "Clicked cancel in dialog");
                        }
                    });

                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }
            });

        }

    }

    public BirdCardAdapter(ArrayList<BirdPhoto> photos) {
            //this.mContext = context;
            this.photos = photos;
    }

    public void updatePhotoList() {
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bird_card, parent, false);

        return new MyViewHolder(itemView);
    }

    //TODO kanske ska kolla om man kan ladda in dom innan
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        BirdPhoto photo = photos.get(position);
        photoName = photo.getFileName();
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
