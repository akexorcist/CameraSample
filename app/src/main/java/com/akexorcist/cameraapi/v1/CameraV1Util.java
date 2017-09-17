package com.akexorcist.cameraapi.v1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Akexorcist on 7/28/2017 AD.
 */

public class CameraV1Util {
    private static final String TAG = CameraV1Activity.class.getSimpleName();

    public static boolean isCameraSupport(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static Camera openDefaultCamera() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            Log.e(TAG, "Error open camera: " + e.getMessage());
        }
        return camera;
    }

    public static Camera openCamera(int cameraId) {
        Camera camera = null;
        try {
            camera = Camera.open(cameraId);
        } catch (RuntimeException e) {
            Log.e(TAG, "Error open camera: " + e.getMessage());
        }
        return camera;
    }

    public static int getNumberOfCamera() {
        return Camera.getNumberOfCameras();
    }

    public static Camera.Size getBestPictureSize(@NonNull List<Camera.Size> pictureSizeList) {
        Camera.Size bestPictureSize = null;
        for (Camera.Size pictureSize : pictureSizeList) {
            if (bestPictureSize == null ||
                    (pictureSize.height >= bestPictureSize.height &&
                            pictureSize.width >= bestPictureSize.width)) {
                bestPictureSize = pictureSize;
            }
        }
        return bestPictureSize;
    }

    public static Camera.Size getBestPreviewSize(@NonNull List<Camera.Size> previewSizeList, int previewWidth, int previewHeight) {
        Camera.Size bestPreviewSize = null;
        for (Camera.Size previewSize : previewSizeList) {
            if (bestPreviewSize != null) {
                int diffBestPreviewWidth = Math.abs(bestPreviewSize.width - previewWidth);
                int diffPreviewWidth = Math.abs(previewSize.width - previewWidth);
                int diffBestPreviewHeight = Math.abs(bestPreviewSize.height - previewHeight);
                int diffPreviewHeight = Math.abs(previewSize.height - previewHeight);
                if (diffPreviewWidth + diffPreviewHeight < diffBestPreviewWidth + diffBestPreviewHeight) {
                    bestPreviewSize = previewSize;
                }
            } else {
                bestPreviewSize = previewSize;
            }
        }
        return bestPreviewSize;
    }

    public static boolean isContinuousFocusModeSupported(List<String> supportedFocusModes) {
        if (supportedFocusModes != null && !supportedFocusModes.isEmpty()) {
            for (String focusMode : supportedFocusModes) {
                if (focusMode != null && focusMode.equalsIgnoreCase(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getCameraDisplayOrientation(Activity activity, int cameraId) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;
        }
        int orientation = 0;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            orientation = (cameraInfo.orientation + degree) % 360;
            orientation = (360 - orientation) % 360;
        } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            orientation = (cameraInfo.orientation - degree + 360) % 360;
        }
        return orientation;
    }

    public static Matrix getCropCenterScaleMatrix(float viewWidth, float viewHeight, float previewWidth, float previewHeight) {
        float scaleX = 1.0f;
        float scaleY = 1.0f;
        if (previewWidth > viewWidth && previewHeight > viewHeight) {
            scaleX = previewWidth / viewWidth;
            scaleY = previewHeight / viewHeight;
        } else if (previewWidth < viewWidth && previewHeight < viewHeight) {
            scaleY = viewWidth / previewWidth;
            scaleX = viewHeight / previewHeight;
        } else if (viewWidth > previewWidth) {
            scaleY = (viewWidth / previewWidth) / (viewHeight / previewHeight);
        } else if (viewHeight > previewHeight) {
            scaleX = (viewHeight / previewHeight) / (viewWidth / previewWidth);
        }
        return createScaleMatrix(scaleX, scaleY, viewWidth, viewHeight);
    }

    private static float getDiffX(float viewWidth, float videoWidth) {
        return (viewWidth > videoWidth) ? viewWidth / videoWidth : videoWidth / viewWidth;
    }

    private static float getDiffY(float viewHeight, float videoHeight) {
        return (viewHeight > videoHeight) ? viewHeight / videoHeight : videoHeight / viewHeight;
    }

    private static float getAspectRatio(float width, float height) {
        return width / height;
    }

    private static float getCropCenterX(float viewWidth, float viewHeight, float videoWidth, float videoHeight, float diffX, float diffY, float videoAspectRatio) {
        float scaleX;
        if (viewWidth < videoWidth) {
            if (viewHeight < videoHeight) {
                if (diffX > diffY) {
                    scaleX = (viewHeight * videoAspectRatio) / viewWidth;
                } else {
                    scaleX = 1;
                }
            } else {
                scaleX = (viewHeight * videoAspectRatio) / viewWidth;
            }
        } else {
            if (viewHeight < videoHeight) {
                scaleX = 1;
            } else {
                if (diffX >= diffY) {
                    scaleX = 1;
                } else {
                    scaleX = (viewHeight * videoAspectRatio) / viewWidth;
                }
            }
        }
        return scaleX;
    }

    private static float getCropCenterY(float viewWidth, float viewHeight, float videoWidth, float videoHeight, float diffX, float diffY, float videoAspectRatio) {
        float scaleY;
        if (viewHeight < videoHeight) {
            if (viewWidth < videoWidth) {
                if (diffY > diffX) {
                    scaleY = (viewWidth / videoAspectRatio) / viewHeight;
                } else {
                    scaleY = 1;
                }
            } else {
                scaleY = (viewWidth / videoAspectRatio) / viewHeight;
            }
        } else {
            if (viewWidth < videoWidth) {
                scaleY = 1;
            } else {
                if (diffY > diffX) {
                    scaleY = 1;
                } else {
                    scaleY = (viewWidth / videoAspectRatio) / viewHeight;
                }
            }
        }
        return scaleY;
    }

    private static Matrix createScaleMatrix(float scaleX, float scaleY, float width, float height) {
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY, width / 2, height / 2);
        return matrix;
    }

    public static void setImageOrientation(File file, int orientation) {
        if (file != null) {
            try {
                ExifInterface exifInterface = new ExifInterface(file.getPath());
                String orientationValue = String.valueOf(getOrientationExifValue(orientation));
                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, orientationValue);
                exifInterface.saveAttributes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static int getOrientationExifValue(int orientation) {
        switch (orientation) {
            case 90:
                return ExifInterface.ORIENTATION_ROTATE_90;
            case 180:
                return ExifInterface.ORIENTATION_ROTATE_180;
            case 270:
                return ExifInterface.ORIENTATION_ROTATE_270;
            default:
                return ExifInterface.ORIENTATION_NORMAL;
        }
    }

    public static File savePicture(byte[] data) {
        String fileName = getCurrentDate() + ".jpg";
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(filePath + "/" + fileName);
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    public static void updateMediaScanner(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
    }
}
