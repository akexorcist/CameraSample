package com.akexorcist.cameraapi

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.akexorcist.cameraapi.v1.CameraV1Activity
import com.akexorcist.cameraapi.v2.CameraV2Activity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (!report.areAllPermissionsGranted()) {
                        Toast.makeText(this@MainActivity, R.string.camera_and_write_external_storage_denied, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            })
            .check()
    }

    private fun setupView() {
        btnCameraApiV1.setOnClickListener { openActivity(CameraV1Activity::class.java) }
        btnCameraApiV2.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                openActivity(CameraV2Activity::class.java)
            } else {
                Toast.makeText(this, R.string.camera_api_v2_unavailable, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openActivity(cls: Class<*>) {
        startActivity(Intent(this, cls))
    }
}
