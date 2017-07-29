package com.akexorcist.cameraapi;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.akexorcist.cameraapi.v1.CameraV1Activity;
import com.akexorcist.cameraapi.v2.CameraV2Activity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button btnCameraApiV1;
    private Button btnCameraApiV2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();
        setupView();
        checkCameraPermission();
    }

    private void checkCameraPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            // Do something
                        } else {
                            Toast.makeText(MainActivity.this, R.string.camera_and_write_external_storage_denied, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void bindView() {
        btnCameraApiV1 = findViewById(R.id.btnCameraApiV1);
        btnCameraApiV2 = findViewById(R.id.btnCameraApiV2);
    }

    private void setupView() {
        btnCameraApiV1.setOnClickListener(view -> openActivity(CameraV1Activity.class));
        btnCameraApiV2.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                openActivity(CameraV2Activity.class);
            } else {
                Toast.makeText(this, R.string.camera_api_v2_unavailable, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }
}
