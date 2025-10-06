package com.example.realtimeedgedetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, Camera.PreviewCallback {

    private Camera mCamera;
    private TextureView mTextureView;
    private ImageView mImageView;

    private byte[] mFrameData;
    private int[] mOutputPixels;
    private Bitmap mOutputBitmap;
    private boolean isProcessing = false;
    private int frameWidth, frameHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextureView = findViewById(R.id.textureView);
        mImageView = findViewById(R.id.imageView);
        mTextureView.setSurfaceTextureListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        try {
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);

            Camera.Parameters params = mCamera.getParameters();
            // Choose a good preview size
            Camera.Size bestSize = params.getSupportedPreviewSizes().get(0);
            params.setPreviewSize(bestSize.width, bestSize.height);
            mCamera.setParameters(params);

            frameWidth = bestSize.width;
            frameHeight = bestSize.height;

            // Prepare buffers for processing
            mFrameData = new byte[frameWidth * frameHeight * 3 / 2]; // NV21 format
            mOutputPixels = new int[frameWidth * frameHeight];
            mOutputBitmap = Bitmap.createBitmap(frameWidth, frameHeight, Bitmap.Config.ARGB_8888);

            mCamera.addCallbackBuffer(mFrameData);
            mCamera.setPreviewCallbackWithBuffer(this);
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mCamera != null) {
            // Re-queue the buffer for the next frame
            mCamera.addCallbackBuffer(mFrameData);
        }

        if (!isProcessing) {
            isProcessing = true;
            // Call the C++ function
            NativeBridge.processFrame(frameWidth, frameHeight, data, mOutputPixels);

            // Create a bitmap from the processed pixels
            mOutputBitmap.setPixels(mOutputPixels, 0, frameWidth, 0, 0, frameWidth, frameHeight);

            // --- THIS IS THE FIX ---
            // Rotate the bitmap by 90 degrees before displaying
            Bitmap rotatedBitmap = rotateBitmap(mOutputBitmap, 90);

            // Update the UI thread to display the rotated bitmap
            runOnUiThread(() -> mImageView.setImageBitmap(rotatedBitmap));
            isProcessing = false;
        }
    }

    /**
     * Helper method to rotate a Bitmap.
     * @param source The source Bitmap.
     * @param angle The angle of rotation.
     * @return The rotated Bitmap.
     */
    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        // No action needed here
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        if (mCamera != null) {
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        // No action needed here
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            if (mTextureView.isAvailable()) {
                onSurfaceTextureAvailable(mTextureView.getSurfaceTexture(), mTextureView.getWidth(), mTextureView.getHeight());
            }
        } else {
            Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
}
