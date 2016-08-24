package com.example.jonas.birdwatcher;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class BirdCameraActivity extends AppCompatActivity {

    private static final String TAG = "BirdCameraActivity";
    private String birdName;
    private int birdID;
    private int birdPhotoID;

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private View mProgressContainer;

    SurfaceHolder surfaceHolder;

    Camera.Size size;
    Camera.PictureCallback rawCallback;
    Camera.ShutterCallback shutterCallback;
    Camera.PictureCallback jpegCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird_camera);
        getSupportActionBar().hide();
        birdName = getIntent().getStringExtra("BIRD_NAME");
        birdID = getIntent().getIntExtra("BIRD_ID",0);
        birdPhotoID = getIntent().getIntExtra("PHOTO_ID", 0);

        mProgressContainer = findViewById(R.id.mbird_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        mSurfaceView = (SurfaceView) findViewById(R.id.mbird_camera_surfaceView);
        surfaceHolder = mSurfaceView.getHolder();
        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Taped to take picture", Toast.LENGTH_SHORT).show();
                if (mCamera != null) {
                    mCamera.takePicture(shutterCallback, null, jpegCallback);
                }
            }
        });
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            public void surfaceCreated(SurfaceHolder holder) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    mCamera = Camera.open(0);
                } else {
                    mCamera = Camera.open();
                }
                // tell the camera to use this surface as its preview area
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                        mCamera.startPreview();
                    }
                } catch (IOException exception) {
                    Log.e(TAG, "Error setting up preview display", exception);
                }
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                // we can no longer display on this surface, so stop the preview.
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.release();
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                if (mCamera == null) return;

                // the surface has changed size; update the camera preview size
                Camera.Parameters parameters = mCamera.getParameters();
                size = getBestSupportedSize(parameters.getSupportedPreviewSizes(), w, h);
                parameters.setPreviewSize(size.width, size.height);
                size = getBestSupportedSize(parameters.getSupportedPictureSizes(), w, h);
                parameters.setPictureSize(size.width, size.height);
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

        jpegCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                String filename = birdID + birdPhotoID +".jpg";
                Log.d(TAG, "FileName: "+filename);
                String tmp = "";
                //Store the image in the BirdBank.
                tmp = BirdBank.get(getApplicationContext()).storeBirdPhoto(data, birdID);

                Intent intent = new Intent();
                intent.putExtra("Filename", tmp);
                intent.putExtra("Height", size.height);
                intent.putExtra("Width", size.width);
                setResult(1, intent);
                finish();
            }
        };
    }

    /** a simple algorithm to get the largest size available. For a more
     * robust version, see CameraPreview.java in the ApiDemos
     * sample app from Android. */
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
