package com.example.jonas.birdwatcher;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * BirdCameraActivity takes a picture and stores the
 * picture taken on the external memory card using the
 * BirdBank. The filename of the picture taken is sent
 * to BirdActivity using an intent.
 *
 * This class uses an algorithm from the book
 * Android Programming, The Big Nerd Ranch Guide - Brian Hardy, Bill Philips
 * to get the largest available size of the picture. This algorithm
 * is implemented in the method getBestSupportedSize().
 *
 * File:       BirdCameraActivity.java
 * Author:     Jonas Nyman
 * Assignment: Inlämningsuppgift 3 - Valfri Applikation
 * Course:     Utveckling av mobila applikationer
 * Version:    1.0
 */
public class BirdCameraActivity extends AppCompatActivity {

    private static final String TAG = "BirdCameraActivity";
    private String birdName;
    private int birdID;
    private int birdPhotoID;

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    SurfaceHolder surfaceHolder;

    Camera.Size size;
    Camera.ShutterCallback shutterCallback;
    Camera.PictureCallback pictureCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird_camera);
        getSupportActionBar().hide();

        birdName = getIntent().getStringExtra("BIRD_NAME");
        birdID = getIntent().getIntExtra("BIRD_ID",0);
        birdPhotoID = getIntent().getIntExtra("PHOTO_ID", 0);

        shutterCallback = new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
                // Plays the picture taken sound
            }
        };

        mSurfaceView = (SurfaceView) findViewById(R.id.mbird_camera_surfaceView);
        assert mSurfaceView != null;
        surfaceHolder = mSurfaceView.getHolder();
        // Click on the preview image to take a picture
        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCamera != null) {
                    mCamera.takePicture(shutterCallback, null, pictureCallback);
                }
            }
        });
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            // Create a preview showing the camera
            public void surfaceCreated(SurfaceHolder holder) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    mCamera = Camera.open(0);
                } else {
                    mCamera = Camera.open();
                }
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                        mCamera.startPreview();
                    }
                } catch (IOException exception) {
                    // Stop activity if preview fails
                    Toast.makeText(getApplicationContext(), "Failed to open camera",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            /**
             * Stops the preview and releases the camera.
             *
             * @param holder SurfaceHolder
             */
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.release();
                }
            }

            /**
             * Calculate the camera's parameters using the algorithm in
             * getBestSupportedSize() and preview the camera.
             *
             * @param holder SurfaceHolder
             * @param format Format
             * @param width width
             * @param height height
             */
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                if (mCamera == null) return;

                Camera.Parameters parameters = mCamera.getParameters();
                size = getBestSupportedSize(parameters.getSupportedPreviewSizes(),
                        width, height);
                parameters.setPreviewSize(size.width, size.height);
                size = getBestSupportedSize(parameters.getSupportedPictureSizes(),
                        width, height);
                parameters.setPictureSize(size.width, size.height);
                mCamera.setParameters(parameters);
                try {
                    mCamera.startPreview();
                } catch (Exception e) {
                    mCamera.release();
                    mCamera = null;
                }
            }
        });

        pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                String tmp = "";

                //Store the image in the BirdBank.
                tmp = BirdBank.get(getApplicationContext()).storeBirdPhoto(data, birdID);

                Intent intent = new Intent();
                intent.putExtra("Filename", tmp);
                setResult(1, intent);
                finish();
            }
        };
    }

    /**
     * Algorithm from the book
     * Android Programming, The Big Nerd Ranch Guide - Brian Hardy, Bill Philips
     * that retrieves the largest picture size available.
     * robust version, see CameraPreview.java in the ApiDemos
     */
    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }

}
