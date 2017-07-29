package com.akexorcist.cameraapi.v2;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import com.akexorcist.cameraapi.R;

/**
 * Created by Akexorcist on 7/28/2017 AD.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraV2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_v2);

        CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing.equals(CameraCharacteristics.LENS_FACING_FRONT)) {

                }
                // Do something with the characteristics
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
