package com.akexorcist.cameraapi.v1

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.hardware.Camera
import android.util.Log
import android.view.Surface
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.IOException

/**
 * Created by Akexorcist on 7/28/2017 AD.
 */

object CameraV1Util {
    private val TAG = CameraV1Util::class.java.simpleName

    fun getCameraCount() = Camera.getNumberOfCameras()

    fun openDefaultCamera(): Camera? {
        try {
            return Camera.open()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
        return null
    }

    fun openCamera(cameraId: Int): Camera? {
        var camera: Camera? = null
        try {
            camera = Camera.open(cameraId)
        } catch (e: RuntimeException) {
            Log.e(TAG, "Error open camera: " + e.message)
        }
        return camera
    }

    fun isCameraSupport(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    fun isContinuousFocusModeSupported(supportedFocusModes: List<String>?): Boolean {
        return supportedFocusModes?.find { mode -> mode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO, ignoreCase = true) } != null
    }

    fun getCameraDisplayOrientation(activity: Activity, cameraId: Int): Int {
        val cameraInfo = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, cameraInfo)
        val rotation = activity.windowManager.defaultDisplay.rotation
        val degree = getSurfaceRotation(rotation)
        var orientation = 0
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            orientation = (cameraInfo.orientation + degree) % 360
            orientation = (360 - orientation) % 360
        } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            orientation = (cameraInfo.orientation - degree + 360) % 360
        }
        return orientation
    }

    fun setImageOrientation(file: File?, orientation: Int) {
        file?.let {
            try {
                val exifInterface = ExifInterface(file.path)
                val orientationValue = getOrientationExifValue(orientation).toString()
                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, orientationValue)
                exifInterface.saveAttributes()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getBestPreviewSize(supportedSizeList: List<Camera.Size>?, width: Int, height: Int): Camera.Size? {
        val bestSize: Pair<Int, Int>? = getBestPreviewSize(supportedSizeList?.map { size -> Pair(size.width, size.height) }, width, height)
        return bestSize?.let {
            supportedSizeList?.find { size -> size.width == bestSize.first && size.height == bestSize.second }
        } ?: run {
            null
        }
    }

    internal fun getBestPreviewSize(supportedSizeList: List<Pair<Int, Int>>?, width: Int, height: Int): Pair<Int, Int>? = getBestSize(
        supportedSizeList = supportedSizeList,
        width = width,
        height = height,
        default = Int.MAX_VALUE,
        condition = { compareLong, compareShort, requiredLong, requiredShort, bestLong, bestShort ->
            compareLong >= requiredLong &&
                    compareShort >= requiredShort &&
                    compareLong <= bestLong &&
                    compareShort <= bestShort
        }
    )

    fun getBestPictureSize(supportedSizeList: List<Camera.Size>?, width: Int, height: Int): Camera.Size? {
        val bestSize: Pair<Int, Int>? = getBestPictureSize(supportedSizeList?.map { size -> Pair(size.width, size.height) }, width, height)
        return bestSize?.let {
            supportedSizeList?.find { size -> size.width == bestSize.first && size.height == bestSize.second }
        } ?: run {
            null
        }
    }

    internal fun getBestPictureSize(supportedSizeList: List<Pair<Int, Int>>?, width: Int, height: Int): Pair<Int, Int>? = getBestSize(
        supportedSizeList = supportedSizeList,
        width = width,
        height = height,
        default = 0,
        condition = { compareLong, compareShort, requiredLong, requiredShort, bestLong, bestShort ->
            compareLong <= requiredLong &&
                    compareShort <= requiredShort &&
                    compareLong >= bestLong &&
                    compareShort >= bestShort
        }
    )

    private fun getBestSize(
        supportedSizeList: List<Pair<Int, Int>>?,
        width: Int,
        height: Int,
        default: Int,
        condition: (compareLong: Int, compareShort: Int, requiredLong: Int, requiredShort: Int, bestLong: Int, bestShort: Int) -> Boolean
    ): Pair<Int, Int>? {
        if (supportedSizeList == null || supportedSizeList.isEmpty()) {
            return null
        }
        var bestSize: Pair<Int, Int>? = null
        val requiredLong = if (width > height) width else height
        val requiredShort = if (width > height) height else width
        supportedSizeList.forEach { supportedSize ->
            val compareLong = if (supportedSize.first > supportedSize.second) supportedSize.first else supportedSize.second
            val compareShort = if (supportedSize.first > supportedSize.second) supportedSize.second else supportedSize.first
            val bestLong = if (bestSize?.first ?: default > bestSize?.second ?: default) bestSize?.first ?: default else bestSize?.second ?: default
            val bestShort = if (bestSize?.first ?: default > bestSize?.second ?: default) bestSize?.second ?: default else bestSize?.first ?: default
            if (condition(compareLong, compareShort, requiredLong, requiredShort, bestLong, bestShort)) {
                bestSize = supportedSize
            }
        }
        return bestSize
    }

    fun getCropCenterScaleMatrix(
        cameraOrientation: Int,
        viewWidth: Float,
        viewHeight: Float,
        previewWidth: Float,
        previewHeight: Float
    ): Matrix {
        val scale = getViewScale(
            viewWidth = viewWidth,
            viewHeight = viewHeight,
            previewWidth = if (cameraOrientation == 90 || cameraOrientation == 270) previewHeight else previewWidth,
            previewHeight = if (cameraOrientation == 90 || cameraOrientation == 270) previewWidth else previewHeight
        )
        return createScaleMatrix(scale.first, scale.second, viewWidth, viewHeight)
    }

    internal fun getViewScale(
        viewWidth: Float,
        viewHeight: Float,
        previewWidth: Float,
        previewHeight: Float
    ): Pair<Float, Float> {
        val scaleX: Float
        val scaleY: Float
        val viewRatio = viewWidth / viewHeight
        val previewRatio = previewWidth / previewHeight
        if (previewRatio < viewRatio) {
            scaleX = 1.toFloat()
            scaleY = ((viewWidth * previewHeight) / previewWidth) / viewHeight
        } else {
            scaleX = ((previewWidth / previewHeight) * viewHeight) / viewWidth
            scaleY = 1.toFloat()
        }
        return Pair(scaleX, scaleY)
    }

    private fun createScaleMatrix(scaleX: Float, scaleY: Float, width: Float, height: Float): Matrix {
        return Matrix().apply {
            setScale(scaleX, scaleY, width / 2, height / 2)
        }
    }

    internal fun getOrientationExifValue(orientation: Int): Int {
        return when (orientation) {
            90 -> ExifInterface.ORIENTATION_ROTATE_90
            180 -> ExifInterface.ORIENTATION_ROTATE_180
            270 -> ExifInterface.ORIENTATION_ROTATE_270
            else -> ExifInterface.ORIENTATION_NORMAL
        }
    }

    internal fun getSurfaceRotation(rotation: Int): Int {
        return when (rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }
    }

    fun getAutoFocusMode(supportedFocusModes: List<String>?): String? {
        return when {
            supportedFocusModes?.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO) ?: false -> Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
            supportedFocusModes?.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) ?: false -> Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            supportedFocusModes?.contains(Camera.Parameters.FOCUS_MODE_AUTO) ?: false -> Camera.Parameters.FOCUS_MODE_AUTO
            else -> null
        }
    }
}
