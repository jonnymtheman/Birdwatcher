package com.example.jonas.birdwatcher;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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

    private final String TAG = "BIRDCARDADAPTER";
    private Context mContext;
    private ArrayList<BirdPhoto> photos;
    private ArrayList<File> photoFiles;
    private Button deleteButton;

    private View mView;
   // private ImageView imageView;
    private String photoName;
    boolean isFail = false;
    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        private holdValues values;
        public MyViewHolder(View view) {
            super(view);
            mView = view;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent intent = new Intent(view.getContext(), BirdActivity.class);
                    //intent.putExtra("Disp", "Dips");
                    ((BirdActivity)view.getContext()).showLargerImage(photos.get(getAdapterPosition()).getFileName());
                }
            });
            photoName = "";
            values = new holdValues();

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
                            int count = 0;
                            for (BirdPhoto photo1 : photos) {
                                if (photo1.getFileName().equals(photos.get(getAdapterPosition()).getFileName())) {
                                    photos.remove(count);
                                    break;
                                }
                                count++;
                            }
                            BirdBank.get(view.getContext()).deleteBirdPhoto(photoName);


                            //updatePhotoList();
                            notifyDataSetChanged();
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

    public BirdCardAdapter(ArrayList<BirdPhoto> photos, ArrayList<File> photoFiles) {
            this.photos = photos;
            this.photoFiles = photoFiles;
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

    private void displayToast() {
        Toast.makeText(mView.getContext(), "Unable to read storage", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        BirdPhoto photo = photos.get(position);
        photoName = photo.getFileName();
        holder.values.pos = position;

        new AsyncTask<Integer, Void, Bitmap>() {
            private int imgPos;

            @Override
            protected Bitmap doInBackground(Integer... pos) {
                Bitmap bitmap = null;
                Bitmap scaled = null;
                imgPos = pos[0];
                Log.d(TAG, "Imgpos: " +imgPos +" ,AdapterPos: "+holder.getAdapterPosition());

                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).
                        getAbsolutePath(), photos.get(holder.getAdapterPosition()).getFileName());
                if (f.isFile()) {
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                    if (bitmap != null) {
                        int nh = (int) (bitmap.getHeight() * (1100.0 / bitmap.getWidth())); //512.0
                        scaled = Bitmap.createScaledBitmap(bitmap, 1100, nh, true); //512
                        return scaled;
                    }
                   isFail = true;
                }
                    return null;

            }
            @Override
            protected void onPostExecute(Bitmap bm) {
                if (bm != null) {
                    if (imgPos == holder.getAdapterPosition()) {
                        holder.imageView.setImageBitmap(bm);
                    }

                }
            }
        }.execute(position);

        if (isFail) {
            displayToast();
        }

    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    private class loadBirdPicture extends AsyncTask<String, String, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Log.d(TAG, "inne i doBack");
            Log.d(TAG, "Filename: "+strings[0]);
            Bitmap bitmap = null;
            Bitmap scaled = null;

                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).
                        getAbsolutePath(), strings[0]);
                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                int nh = (int) (bitmap.getHeight() * (1100.0 / bitmap.getWidth())); //512.0
                scaled = Bitmap.createScaledBitmap(bitmap, 1100, nh, true); //512

            return scaled;
        }

        @Override
        protected void onPostExecute(Bitmap f) {
                Log.d(TAG, "Inne i onPost");
               // imageView.setImageBitmap(f);
        }
    }

    private static class holdValues {
        public int pos;
    }

}
