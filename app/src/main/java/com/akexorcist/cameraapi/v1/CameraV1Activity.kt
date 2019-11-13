package com.akexorcist.cameraapi.v1

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.media.MediaActionSound
import android.os.Bundle
import android.util.Log
import android.view.TextureView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.akexorcist.cameraapi.FileUtil
import com.akexorcist.cameraapi.R
import kotlinx.android.synthetic.main.activity_camera_v1.*
import java.io.File

import java.io.IOException

/**
 * Created by Akexorcist on 7/28/2017 AD.
 */

class CameraV1Activity : AppCompatActivity() {

    private val cameraId = Camera.CameraInfo.CAMERA_FACING_BACK

    private var camera: Camera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_v1)

        buttonCapture.setOnClickListener { takePicture() }
        buttonFocus.setOnClickListener { refocus() }
        toggleButtonNegativeColor.setOnCheckedChangeListener { _, isChecked -> toggleNegativeColor(isChecked) }
        textureViewCamera.surfaceTextureListener = surfaceTextureListener
    }

    override fun onStart() {
        super.onStart()
        if (textureViewCamera.isAvailable) {
            setupCamera(textureViewCamera.width, textureViewCamera.height)
            startCameraPreview(textureViewCamera.surfaceTexture)
        }
    }

    override fun onStop() {
        super.onStop()
        stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        textureViewCamera.surfaceTextureListener = null
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
            setupCamera(width, height)
            startCameraPreview(surfaceTexture)
        }

        override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {}

        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
            stopCamera()
            return true
        }

        override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
    }

    private val shutterCallback = Camera.ShutterCallback { playShutterSound() }

    private val pictureCallback = object : Camera.PictureCallback {
        override fun onPictureTaken(data: ByteArray, camera: Camera) {
            FileUtil.savePicture(this@CameraV1Activity, data)?.let { file: File ->
                val orientation = CameraV1Util.getCameraDisplayOrientation(this@CameraV1Activity, cameraId)
                CameraV1Util.setImageOrientation(file, orientation)
                FileUtil.updateMediaScanner(this@CameraV1Activity, file)
                Toast.makeText(this@CameraV1Activity, getString(R.string.photo_saved, file.absolutePath), Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(this@CameraV1Activity, R.string.unable_save_photo, Toast.LENGTH_SHORT).show()
            }
            startCameraPreview(textureViewCamera.surfaceTexture)
        }
    }

    private fun setupCamera(width: Int, height: Int) {
        camera = CameraV1Util.openCamera(cameraId)
        camera?.let { camera: Camera ->
            val cameraOrientation = CameraV1Util.getCameraDisplayOrientation(this, cameraId)
            camera.setDisplayOrientation(cameraOrientation)
            val parameters = camera.parameters
            val bestPreviewSize: Camera.Size? = CameraV1Util.getBestPreviewSize(parameters.supportedPreviewSizes, width, height)
            bestPreviewSize?.let { previewSize: Camera.Size ->
                parameters.setPreviewSize(previewSize.width, previewSize.height)
                textureViewCamera.setTransform(
                    CameraV1Util.getCropCenterScaleMatrix(
                        cameraOrientation,
                        width.toFloat(),
                        height.toFloat(),
                        previewSize.width.toFloat(),
                        previewSize.height.toFloat()
                    )
                )
            }
            CameraV1Util.getBestPictureSize(parameters.supportedPictureSizes)?.let { pictureSize: Camera.Size ->
                parameters.setPictureSize(pictureSize.width, pictureSize.height)
            }
            if (CameraV1Util.isContinuousFocusModeSupported(parameters.supportedFocusModes)) {
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
            }
            camera.parameters = parameters
        } ?: run {
            Toast.makeText(this, R.string.camera_unavailable, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCameraPreview(surfaceTexture: SurfaceTexture) {
        try {
            camera?.setPreviewTexture(surfaceTexture)
            camera?.startPreview()
        } catch (e: IOException) {
            Log.e(TAG, "Error start camera preview: " + e.message)
        }

    }

    private fun stopCamera() {
        try {
            camera?.stopPreview()
            camera?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error stop camera preview: " + e.message)
        }
    }

    private fun takePicture() {
        camera?.takePicture(shutterCallback, null, null, pictureCallback)
    }

    private fun refocus() {
        camera?.autoFocus(autoFocusCallback)
    }

    private val autoFocusCallback = Camera.AutoFocusCallback { success: Boolean, _: Camera ->
        if (success) {
            playFocusSound()
        }
    }

    private fun toggleNegativeColor(isTurnOn: Boolean) {
        camera?.let { camera: Camera ->
            val parameters = camera.parameters
            parameters.supportedColorEffects?.let { colorEffectList: List<String> ->
                if (colorEffectList.contains(Camera.Parameters.EFFECT_NEGATIVE)) {
                    if (isTurnOn) {
                        parameters.colorEffect = Camera.Parameters.EFFECT_NEGATIVE
                    } else {
                        parameters.colorEffect = Camera.Parameters.EFFECT_NONE
                    }
                } else {
                    Toast.makeText(this, R.string.negative_color_effect_unavailable, Toast.LENGTH_SHORT).show()
                }
            }
            camera.parameters = parameters
        }
    }

    private fun playShutterSound() {
        val sound = MediaActionSound()
        sound.play(MediaActionSound.SHUTTER_CLICK)
    }

    private fun playFocusSound() {
        val sound = MediaActionSound()
        sound.play(MediaActionSound.FOCUS_COMPLETE)
    }

    companion object {
        private val TAG = CameraV1Activity::class.java.simpleName
    }
}
