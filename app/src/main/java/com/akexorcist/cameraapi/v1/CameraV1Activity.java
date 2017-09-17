package com.akexorcist.cameraapi.v1;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.akexorcist.cameraapi.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Akexorcist on 7/28/2017 AD.
 */

public class CameraV1Activity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private static final String TAG = CameraV1Activity.class.getSimpleName();

    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    private Button buttonCapture;
    private Button buttonFocus;
    private ToggleButton toggleButtonFlash;
    private TextureView textureViewCamera;

    private Camera camera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_v1);

        buttonCapture = findViewById(R.id.buttonCapture);
        buttonFocus = findViewById(R.id.buttonFocus);
        toggleButtonFlash = findViewById(R.id.toggleButtonFlash);
        textureViewCamera = findViewById(R.id.textureViewCamera);

        buttonCapture.setOnClickListener(view -> takePicture());
        buttonFocus.setOnClickListener(view -> refocus());
        toggleButtonFlash.setOnCheckedChangeListener((buttonView, isChecked) -> toggleNegativeColor(isChecked));
        textureViewCamera.setSurfaceTextureListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (textureViewCamera.isAvailable()) {
            setupCamera(textureViewCamera.getWidth(), textureViewCamera.getHeight());
            startCameraPreview(textureViewCamera.getSurfaceTexture());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textureViewCamera.setSurfaceTextureListener(null);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        setupCamera(width, height);
        startCameraPreview(surfaceTexture);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        stopCamera();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    private void setupCamera(int width, int height) {
        camera = CameraV1Util.openCamera(cameraId);
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size bestPreviewSize = CameraV1Util.getBestPreviewSize(parameters.getSupportedPreviewSizes(), width, height);
        parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
        Camera.Size bestPictureSize = CameraV1Util.getBestPictureSize(parameters.getSupportedPictureSizes());
        parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);
        if (CameraV1Util.isContinuousFocusModeSupported(parameters.getSupportedFocusModes())) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        camera.setParameters(parameters);
        camera.setDisplayOrientation(CameraV1Util.getCameraDisplayOrientation(this, cameraId));
        textureViewCamera.setTransform(CameraV1Util.getCropCenterScaleMatrix(width, height, bestPreviewSize.width, bestPreviewSize.height));
    }

    private void startCameraPreview(SurfaceTexture surfaceTexture) {
        try {
            camera.setPreviewTexture(surfaceTexture);
            camera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "Error start camera preview: " + e.getMessage());
        }
    }

    private void stopCamera() {
        try {
            camera.stopPreview();
            camera.release();
        } catch (Exception e) {
            Log.e(TAG, "Error stop camera preview: " + e.getMessage());
        }
    }

    private void takePicture() {
        camera.takePicture(this::playShutterSound,
                null,
                null,
                (data, camera) -> {
                    File file = CameraV1Util.savePicture(data);
                    int orientation = CameraV1Util.getCameraDisplayOrientation(this, cameraId);
                    CameraV1Util.setImageOrientation(file, orientation);
                    CameraV1Util.updateMediaScanner(this, file);
                });
    }

    private void refocus() {
        camera.autoFocus((success, camera) -> playFocusSound());
    }

    private void toggleNegativeColor(boolean isTurnOn) {
        Camera.Parameters parameters = camera.getParameters();
        List<String> supportedColorEffectList = parameters.getSupportedColorEffects();
        if (supportedColorEffectList != null && supportedColorEffectList.contains(Camera.Parameters.EFFECT_NEGATIVE)) {
            if (isTurnOn) {
                parameters.setColorEffect(Camera.Parameters.EFFECT_NEGATIVE);
            } else {
                parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
            }
        } else {
            Toast.makeText(this, R.string.negative_color_effect_unavailable, Toast.LENGTH_SHORT).show();
        }
        camera.setParameters(parameters);
    }

    private void playShutterSound() {
        MediaActionSound sound = new MediaActionSound();
        sound.play(MediaActionSound.SHUTTER_CLICK);
    }

    private void playFocusSound() {
        MediaActionSound sound = new MediaActionSound();
        sound.play(MediaActionSound.FOCUS_COMPLETE);
    }
}
