package com.example.jonas.birdwatcher;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class BirdCameraFragment extends Fragment {
    private static final String TAG = "BirdCameraFragment";
    public static final String EXTRA_PHOTO_FILENAME = "BirdCameraFragment.filename";

    private String birdName;
    private int birdID;
    private int birdPhotoID;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;



    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            // display the progress indicator
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };
    private Camera.PictureCallback mJpegCallBack = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // create a filename
            String filename = birdID + birdPhotoID +".jpg"; //UUID.randomUUID().toString() + ".jpg";
            Log.d(TAG, "FileName: "+filename);
            String tmp = "";
            tmp = BirdBank.get(getActivity()).storeBirdPhoto(data, birdID);
            Intent intent = new Intent();
            intent.putExtra("Filename", tmp);
            getActivity().setResult(1, intent);
            getActivity().finish();
        }
    };
    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bird_camera, parent, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        birdName = getActivity().getIntent().getStringExtra("BIRD_NAME");
        birdID = getActivity().getIntent().getIntExtra("BIRD_ID",0);
        birdPhotoID = getActivity().getIntent().getIntExtra("PHOTO_ID", 0);
        Log.d(TAG, "Birdname: "+birdName);
        Log.d(TAG, "BirdID: "+birdID);


        mProgressContainer = v.findViewById(R.id.bird_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);
        Button takePictureButton = (Button)v.findViewById(R.id.bird_camera_takePicture);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mCamera != null) {
                    mCamera.takePicture(mShutterCallback, null, mJpegCallBack);
                }
            }
        });

        mSurfaceView = (SurfaceView)v.findViewById(R.id.bird_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        // deprecated, but required for pre-3.0 devices
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback() {

            public void surfaceCreated(SurfaceHolder holder) {
                // tell the camera to use this surface as its preview area
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (IOException exception) {
                    Log.e(TAG, "Error setting up preview display", exception);
                }
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                // we can no longer display on this surface, so stop the preview.
                if (mCamera != null) {
                    mCamera.stopPreview();
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                if (mCamera == null) return;

                // the surface has changed size; update the camera preview size
                Camera.Parameters parameters = mCamera.getParameters();
                Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), w, h);
                parameters.setPreviewSize(s.width, s.height);
                s = getBestSupportedSize(parameters.getSupportedPictureSizes(), w, h);
                parameters.setPictureSize(s.width, s.height);
                mCamera.setParameters(parameters);
                try {
                    mCamera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "Could not start preview", e);
                    mCamera.release();
                    mCamera = null;
                }
            }
        });

        return v;
    }

    @TargetApi(9)
    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mCamera = Camera.open(0);
        } else {
            mCamera = Camera.open();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    /** a simple algorithm to get the largest size available. For a more
     * robust version, see CameraPreview.java in the ApiDemos
     * sample app from Android. */
    private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
        Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }

}
