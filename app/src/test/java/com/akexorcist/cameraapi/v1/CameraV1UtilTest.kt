package com.akexorcist.cameraapi.v1

import android.hardware.Camera
import android.view.Surface
import androidx.exifinterface.media.ExifInterface
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class CameraV1UtilTest {

    @Before
    fun setup() {
    }

    @Test
    fun `Is continuous focus mode supported with continuous focus mode supported`() {
        val supportedFocusModes = listOf(
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO,
            Camera.Parameters.FOCUS_MODE_AUTO
        )
        Assert.assertTrue(CameraV1Util.isContinuousFocusModeSupported(supportedFocusModes))
    }

    @Test
    fun `Is continuous focus mode supported without continuous focus mode supported`() {
        val supportedFocusModes = listOf(
            Camera.Parameters.FOCUS_MODE_FIXED,
            Camera.Parameters.FOCUS_MODE_AUTO
        )
        Assert.assertFalse(CameraV1Util.isContinuousFocusModeSupported(supportedFocusModes))
    }

    @Test
    fun `Is continuous focus mode supported with null`() {
        Assert.assertFalse(CameraV1Util.isContinuousFocusModeSupported(null))
    }

    @Test
    fun `Get auto focus mode with video continuous focus supported`() {
        val supportedFocusModes = listOf(
            Camera.Parameters.FOCUS_MODE_FIXED,
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
            Camera.Parameters.FOCUS_MODE_EDOF,
            Camera.Parameters.FOCUS_MODE_AUTO,
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
        )
        Assert.assertEquals(CameraV1Util.getAutoFocusMode(supportedFocusModes), Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)
    }

    @Test
    fun `Get auto focus mode with picture continuous focus supported`() {
        val supportedFocusModes = listOf(
            Camera.Parameters.FOCUS_MODE_FIXED,
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
            Camera.Parameters.FOCUS_MODE_EDOF,
            Camera.Parameters.FOCUS_MODE_AUTO
        )
        Assert.assertEquals(CameraV1Util.getAutoFocusMode(supportedFocusModes), Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
    }

    @Test
    fun `Get auto focus mode with auto focus result supported`() {
        val supportedFocusModes = listOf(
            Camera.Parameters.FOCUS_MODE_FIXED,
            Camera.Parameters.FOCUS_MODE_EDOF,
            Camera.Parameters.FOCUS_MODE_AUTO
        )
        Assert.assertEquals(CameraV1Util.getAutoFocusMode(supportedFocusModes), Camera.Parameters.FOCUS_MODE_AUTO)
    }

    @Test
    fun `Get auto focus mode with no supported`() {
        val supportedFocusModes = listOf(
            Camera.Parameters.FOCUS_MODE_FIXED,
            Camera.Parameters.FOCUS_MODE_EDOF
        )
        Assert.assertEquals(CameraV1Util.getAutoFocusMode(supportedFocusModes), null)
    }

    @Test
    fun `Get auto focus mode with null`() {
        Assert.assertEquals(CameraV1Util.getAutoFocusMode(null), null)
    }

    @Test
    fun `Get auto focus mode without result`() {
        Assert.assertFalse(CameraV1Util.isContinuousFocusModeSupported(null))
    }

    @Test
    fun `Get best square preview size in landscape size supported with landscape size result`() {
        val cameraSize = listOf(
            Pair(320, 240),
            Pair(640, 480),
            Pair(1280, 720),
            Pair(1920, 1080),
            Pair(2048, 1536),
            Pair(3264, 1840),
            Pair(3264, 2448),
            Pair(3968, 2976),
            Pair(2976, 2976)
        )
        Assert.assertEquals(Pair(1280, 720), CameraV1Util.getBestPreviewSize(cameraSize, 600, 600))
    }

    @Test
    fun `Get best landscape preview size in landscape size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(320, 240),
            Pair(640, 480),
            Pair(1280, 720),
            Pair(1920, 1080),
            Pair(2048, 1536),
            Pair(3264, 1840),
            Pair(3264, 2448),
            Pair(3968, 2976),
            Pair(2976, 2976)
        )
        Assert.assertEquals(Pair(1920, 1080), CameraV1Util.getBestPreviewSize(cameraSize, 1596, 1080))
    }

    @Test
    fun `Get best portrait preview size in landscape size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(320, 240),
            Pair(640, 480),
            Pair(1280, 720),
            Pair(1920, 1080),
            Pair(2048, 1536),
            Pair(3264, 1840),
            Pair(3264, 2448),
            Pair(3968, 2976),
            Pair(2976, 2976)
        )
        Assert.assertEquals(Pair(1920, 1080), CameraV1Util.getBestPreviewSize(cameraSize, 1080, 1596))
    }

    @Test
    fun `Get best square preview size in reverse landscape size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(2976, 2976),
            Pair(3968, 2976),
            Pair(3264, 2448),
            Pair(3264, 1840),
            Pair(2048, 1536),
            Pair(1920, 1080),
            Pair(1280, 720),
            Pair(640, 480),
            Pair(320, 240)
        )
        Assert.assertEquals(Pair(1280, 720), CameraV1Util.getBestPreviewSize(cameraSize, 600, 600))
    }

    @Test
    fun `Get best landscape preview size in reverse landscape size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(2976, 2976),
            Pair(3968, 2976),
            Pair(3264, 2448),
            Pair(3264, 1840),
            Pair(2048, 1536),
            Pair(1920, 1080),
            Pair(1280, 720),
            Pair(640, 480),
            Pair(320, 240)
        )
        Assert.assertEquals(Pair(1920, 1080), CameraV1Util.getBestPreviewSize(cameraSize, 1596, 1080))
    }

    @Test
    fun `Get best portrait preview size in reverse landscape size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(2976, 2976),
            Pair(3968, 2976),
            Pair(3264, 2448),
            Pair(3264, 1840),
            Pair(2048, 1536),
            Pair(1920, 1080),
            Pair(1280, 720),
            Pair(640, 480),
            Pair(320, 240)
        )
        Assert.assertEquals(Pair(1920, 1080), CameraV1Util.getBestPreviewSize(cameraSize, 1080, 1596))
    }

    @Test
    fun `Get best square preview size in portrait size supported with landscape size result`() {
        val cameraSize = listOf(
            Pair(240, 320),
            Pair(480, 640),
            Pair(720, 1280),
            Pair(1080, 1920),
            Pair(1536, 2048),
            Pair(1840, 3264),
            Pair(2448, 3264),
            Pair(2976, 3968),
            Pair(2976, 2976)
        )
        Assert.assertEquals(Pair(720, 1280), CameraV1Util.getBestPreviewSize(cameraSize, 600, 600))
    }

    @Test
    fun `Get best landscape preview size in portrait size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(240, 320),
            Pair(480, 640),
            Pair(720, 1280),
            Pair(1080, 1920),
            Pair(1536, 2048),
            Pair(1840, 3264),
            Pair(2448, 3264),
            Pair(2976, 3968),
            Pair(2976, 2976)
        )
        Assert.assertEquals(Pair(1080, 1920), CameraV1Util.getBestPreviewSize(cameraSize, 1596, 1080))
    }

    @Test
    fun `Get best portrait preview size in portrait size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(240, 320),
            Pair(480, 640),
            Pair(720, 1280),
            Pair(1080, 1920),
            Pair(1536, 2048),
            Pair(1840, 3264),
            Pair(2448, 3264),
            Pair(2976, 3968),
            Pair(2976, 2976)
        )
        Assert.assertEquals(Pair(1080, 1920), CameraV1Util.getBestPreviewSize(cameraSize, 1080, 1596))
    }

    @Test
    fun `Get best square preview size in reverse portrait size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(2976, 2976),
            Pair(2976, 3968),
            Pair(2448, 3264),
            Pair(1840, 3264),
            Pair(1536, 2048),
            Pair(1080, 1920),
            Pair(720, 1280),
            Pair(480, 640),
            Pair(240, 320)
        )
        Assert.assertEquals(Pair(720, 1280), CameraV1Util.getBestPreviewSize(cameraSize, 600, 600))
    }

    @Test
    fun `Get best landscape preview size in reverse portrait size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(2976, 2976),
            Pair(2976, 3968),
            Pair(2448, 3264),
            Pair(1840, 3264),
            Pair(1536, 2048),
            Pair(1080, 1920),
            Pair(720, 1280),
            Pair(480, 640),
            Pair(240, 320)
        )
        Assert.assertEquals(Pair(1080, 1920), CameraV1Util.getBestPreviewSize(cameraSize, 1596, 1080))
    }

    @Test
    fun `Get best portrait preview size in reverse portrait size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(2976, 2976),
            Pair(2976, 3968),
            Pair(2448, 3264),
            Pair(1840, 3264),
            Pair(1536, 2048),
            Pair(1080, 1920),
            Pair(720, 1280),
            Pair(480, 640),
            Pair(240, 320)
        )
        Assert.assertEquals(Pair(1080, 1920), CameraV1Util.getBestPreviewSize(cameraSize, 1080, 1596))
    }

    @Test
    fun `Get best portrait preview size in portrait size supported with landscape result 2`() {
        val cameraSize = listOf(
            Pair(2048, 1536),
            Pair(1920, 1080),
            Pair(1600, 1200),
            Pair(1440, 1080),
            Pair(1280, 960),
            Pair(1280, 720),
            Pair(1200, 1200),
            Pair(1024, 768),
            Pair(800, 600),
            Pair(720, 480),
            Pair(640, 480),
            Pair(640, 360),
            Pair(480, 360),
            Pair(480, 320),
            Pair(352, 288),
            Pair(320, 240),
            Pair(176, 144),
            Pair(160, 120)
        )
        Assert.assertEquals(Pair(1920, 1080), CameraV1Util.getBestPreviewSize(cameraSize, 1080, 1689))
    }

    @Test
    fun `Get best preview size in with null camera size with null result`() {
        val cameraSize: List<Pair<Int, Int>>? = null
        Assert.assertEquals(null, CameraV1Util.getBestPreviewSize(cameraSize, 600, 600))
    }

    @Test
    fun `Get best preview size in with empty camera size with null result`() {
        val cameraSize = listOf<Pair<Int, Int>>()
        Assert.assertEquals(null, CameraV1Util.getBestPreviewSize(cameraSize, 600, 600))
    }

    @Test
    fun `Get best square picture size in landscape size supported with landscape size result`() {
        val cameraSize = listOf(
            Pair(320, 240),
            Pair(640, 480),
            Pair(1280, 720),
            Pair(1920, 1080),
            Pair(2048, 1536),
            Pair(3264, 1840),
            Pair(3264, 2448),
            Pair(3968, 2976),
            Pair(2976, 2976)
        )
        Assert.assertEquals(Pair(640, 480), CameraV1Util.getBestPictureSize(cameraSize, 800, 800))
    }

    @Test
    fun `Get best landscape picture size in landscape size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(320, 240),
            Pair(640, 480),
            Pair(1280, 720),
            Pair(1920, 1080),
            Pair(2048, 1536),
            Pair(3264, 1840),
            Pair(3264, 2448),
            Pair(3968, 2976),
            Pair(2976, 2976)
        )
        Assert.assertEquals(Pair(640, 480), CameraV1Util.getBestPictureSize(cameraSize, 1200, 1000))
    }

    @Test
    fun `Get best portrait picture size in landscape size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(320, 240),
            Pair(640, 480),
            Pair(1280, 720),
            Pair(1920, 1080),
            Pair(2048, 1536),
            Pair(3264, 1840),
            Pair(3264, 2448),
            Pair(3968, 2976),
            Pair(2976, 2976)
        )
        Assert.assertEquals(Pair(640, 480), CameraV1Util.getBestPictureSize(cameraSize, 1000, 1200))
    }

    @Test
    fun `Get best square picture size in reverse landscape size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(2976, 2976),
            Pair(3968, 2976),
            Pair(3264, 2448),
            Pair(3264, 1840),
            Pair(2048, 1536),
            Pair(1920, 1080),
            Pair(1280, 720),
            Pair(640, 480),
            Pair(320, 240)
        )
        Assert.assertEquals(Pair(640, 480), CameraV1Util.getBestPictureSize(cameraSize, 800, 800))
    }

    @Test
    fun `Get best landscape picture size in reverse landscape size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(2976, 2976),
            Pair(3968, 2976),
            Pair(3264, 2448),
            Pair(3264, 1840),
            Pair(2048, 1536),
            Pair(1920, 1080),
            Pair(1280, 720),
            Pair(640, 480),
            Pair(320, 240)
        )
        Assert.assertEquals(Pair(640, 480), CameraV1Util.getBestPictureSize(cameraSize, 1200, 1000))
    }

    @Test
    fun `Get best portrait picture size in reverse landscape size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(2976, 2976),
            Pair(3968, 2976),
            Pair(3264, 2448),
            Pair(3264, 1840),
            Pair(2048, 1536),
            Pair(1920, 1080),
            Pair(1280, 720),
            Pair(640, 480),
            Pair(320, 240)
        )
        Assert.assertEquals(Pair(640, 480), CameraV1Util.getBestPictureSize(cameraSize, 1000, 1200))
    }

    @Test
    fun `Get best square picture size in portrait size supported with landscape size result`() {
        val cameraSize = listOf(
            Pair(240, 320),
            Pair(480, 640),
            Pair(720, 1280),
            Pair(1080, 1920),
            Pair(1536, 2048),
            Pair(1840, 3264),
            Pair(2448, 3264),
            Pair(2976, 3968),
            Pair(2976, 2976)
        )
        Assert.assertEquals(Pair(480, 640), CameraV1Util.getBestPictureSize(cameraSize, 800, 800))
    }

    @Test
    fun `Get best landscape picture size in portrait size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(240, 320),
            Pair(480, 640),
            Pair(720, 1280),
            Pair(1080, 1920),
            Pair(1536, 2048),
            Pair(1840, 3264),
            Pair(2448, 3264),
            Pair(2976, 3968),
            Pair(2976, 2976)
        )
        Assert.assertEquals(Pair(480, 640), CameraV1Util.getBestPictureSize(cameraSize, 1200, 1000))
    }

    @Test
    fun `Get best portrait picture size in portrait size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(240, 320),
            Pair(480, 640),
            Pair(720, 1280),
            Pair(1080, 1920),
            Pair(1536, 2048),
            Pair(1840, 3264),
            Pair(2448, 3264),
            Pair(2976, 3968),
            Pair(2976, 2976)
        )
        Assert.assertEquals(Pair(480, 640), CameraV1Util.getBestPictureSize(cameraSize, 1000, 1200))
    }

    @Test
    fun `Get best square picture size in reverse portrait size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(2976, 2976),
            Pair(2976, 3968),
            Pair(2448, 3264),
            Pair(1840, 3264),
            Pair(1536, 2048),
            Pair(1080, 1920),
            Pair(720, 1280),
            Pair(480, 640),
            Pair(240, 320)
        )
        Assert.assertEquals(Pair(480, 640), CameraV1Util.getBestPictureSize(cameraSize, 800, 800))
    }

    @Test
    fun `Get best landscape picture size in reverse portrait size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(2976, 2976),
            Pair(2976, 3968),
            Pair(2448, 3264),
            Pair(1840, 3264),
            Pair(1536, 2048),
            Pair(1080, 1920),
            Pair(720, 1280),
            Pair(480, 640),
            Pair(240, 320)
        )
        Assert.assertEquals(Pair(480, 640), CameraV1Util.getBestPictureSize(cameraSize, 1200, 1000))
    }

    @Test
    fun `Get best portrait picture size in reverse portrait size supported with landscape result`() {
        val cameraSize = listOf(
            Pair(2976, 2976),
            Pair(2976, 3968),
            Pair(2448, 3264),
            Pair(1840, 3264),
            Pair(1536, 2048),
            Pair(1080, 1920),
            Pair(720, 1280),
            Pair(480, 640),
            Pair(240, 320)
        )
        Assert.assertEquals(Pair(480, 640), CameraV1Util.getBestPictureSize(cameraSize, 1000, 1200))
    }

    @Test
    fun `Get best picture size in with null camera size with null result`() {
        val cameraSize: List<Pair<Int, Int>>? = null
        Assert.assertEquals(null, CameraV1Util.getBestPictureSize(cameraSize, 800, 800))
    }

    @Test
    fun `Get best picture size in with empty camera size with null result`() {
        val cameraSize = listOf<Pair<Int, Int>>()
        Assert.assertEquals(null, CameraV1Util.getBestPictureSize(cameraSize, 800, 800))
    }

    @Test
    fun `Get crop center scale matrix with landscape view, landscape preview and view size is bigger`() {
        val viewWidth = 1400.toFloat()
        val viewHeight = 1000.toFloat()
        val previewWidth = 800.toFloat()
        val previewHeight = 600.toFloat()
        val result: Pair<Float, Float> = CameraV1Util.getViewScale(viewWidth, viewHeight, previewWidth, previewHeight)
        val expect = Pair(1.toFloat(), 1.05.toFloat())
        Assert.assertEquals(expect.first, result.first)
        Assert.assertEquals(expect.second, result.second)
    }

    @Test
    fun `Get crop center scale matrix with landscape view, landscape preview and view size is smaller`() {
        val viewWidth = 1400.toFloat()
        val viewHeight = 1000.toFloat()
        val previewWidth = 2000.toFloat()
        val previewHeight = 600.toFloat()
        val result: Pair<Float, Float> = CameraV1Util.getViewScale(viewWidth, viewHeight, previewWidth, previewHeight)
        val expect = Pair(2.3809524.toFloat(), 1.toFloat())
        Assert.assertEquals(expect.first, result.first)
        Assert.assertEquals(expect.second, result.second)
    }

    @Test
    fun `Get crop center scale matrix with landscape view, landscape preview and same size`() {
        val viewWidth = 800.toFloat()
        val viewHeight = 600.toFloat()
        val previewWidth = 800.toFloat()
        val previewHeight = 600.toFloat()
        val result: Pair<Float, Float> = CameraV1Util.getViewScale(viewWidth, viewHeight, previewWidth, previewHeight)
        val expect = Pair(1.toFloat(), 1.toFloat())
        Assert.assertEquals(expect.first, result.first)
        Assert.assertEquals(expect.second, result.second)
    }

    @Test
    fun `Get crop center scale matrix with landscape view, landscape preview and same ratio`() {
        val viewWidth = 1400.toFloat()
        val viewHeight = 1000.toFloat()
        val previewWidth = 2100.toFloat()
        val previewHeight = 1500.toFloat()
        val result: Pair<Float, Float> = CameraV1Util.getViewScale(viewWidth, viewHeight, previewWidth, previewHeight)
        val expect = Pair(1.toFloat(), 1.toFloat())
        Assert.assertEquals(expect.first, result.first)
        Assert.assertEquals(expect.second, result.second)
    }

    @Test
    fun `Get crop center scale matrix with landscape view, square preview and view size is smaller`() {
        val viewWidth = 1000.toFloat()
        val viewHeight = 1000.toFloat()
        val previewWidth = 2100.toFloat()
        val previewHeight = 1500.toFloat()
        val result: Pair<Float, Float> = CameraV1Util.getViewScale(viewWidth, viewHeight, previewWidth, previewHeight)
        val expect = Pair(1.4.toFloat(), 1.toFloat())
        Assert.assertEquals(expect.first, result.first)
        Assert.assertEquals(expect.second, result.second)
    }

    @Test
    fun `Get crop center scale matrix with portrait view, square preview and view size is smaller`() {
        val viewWidth = 1000.toFloat()
        val viewHeight = 1000.toFloat()
        val previewWidth = 1500.toFloat()
        val previewHeight = 2100.toFloat()
        val result: Pair<Float, Float> = CameraV1Util.getViewScale(viewWidth, viewHeight, previewWidth, previewHeight)
        val expect = Pair(1.toFloat(), 1.4.toFloat())
        Assert.assertEquals(expect.first, result.first)
        Assert.assertEquals(expect.second, result.second)
    }

    @Test
    fun `Get crop center scale matrix with portrait view, portrait preview and view size is smaller`() {
        val viewWidth = 1000.toFloat()
        val viewHeight = 1500.toFloat()
        val previewWidth = 1600.toFloat()
        val previewHeight = 2000.toFloat()
        val result: Pair<Float, Float> = CameraV1Util.getViewScale(viewWidth, viewHeight, previewWidth, previewHeight)
        val expect = Pair(1.2.toFloat(), 1.toFloat())
        Assert.assertEquals(expect.first, result.first)
        Assert.assertEquals(expect.second, result.second)
    }

    @Test
    fun `Get crop center scale matrix with portrait view, portrait preview and view size is bigger`() {
        val viewWidth = 1000.toFloat()
        val viewHeight = 1400.toFloat()
        val previewWidth = 600.toFloat()
        val previewHeight = 800.toFloat()
        val result: Pair<Float, Float> = CameraV1Util.getViewScale(viewWidth, viewHeight, previewWidth, previewHeight)
        val expect = Pair(1.05.toFloat(), 1.toFloat())
        Assert.assertEquals(expect.first, result.first)
        Assert.assertEquals(expect.second, result.second)
    }

    @Test
    fun `Get crop center scale matrix with portrait view, landscape preview and view size is bigger`() {
        val viewWidth = 1400.toFloat()
        val viewHeight = 1000.toFloat()
        val previewWidth = 600.toFloat()
        val previewHeight = 800.toFloat()
        val result: Pair<Float, Float> = CameraV1Util.getViewScale(viewWidth, viewHeight, previewWidth, previewHeight)
        val expect = Pair(1.toFloat(), 1.8666667.toFloat())
        Assert.assertEquals(expect.first, result.first)
        Assert.assertEquals(expect.second, result.second)
    }

    @Test
    fun `Get crop center scale matrix with square view, landscape preview and view size is bigger`() {
        val viewWidth = 1400.toFloat()
        val viewHeight = 1000.toFloat()
        val previewWidth = 800.toFloat()
        val previewHeight = 800.toFloat()
        val result: Pair<Float, Float> = CameraV1Util.getViewScale(viewWidth, viewHeight, previewWidth, previewHeight)
        val expect = Pair(1.toFloat(), 1.4.toFloat())
        Assert.assertEquals(expect.first, result.first)
        Assert.assertEquals(expect.second, result.second)
    }

    @Test
    fun `Get crop center scale matrix with square view, portrait preview and view size is bigger`() {
        val viewWidth = 1000.toFloat()
        val viewHeight = 1400.toFloat()
        val previewWidth = 800.toFloat()
        val previewHeight = 800.toFloat()
        val result: Pair<Float, Float> = CameraV1Util.getViewScale(viewWidth, viewHeight, previewWidth, previewHeight)
        val expect = Pair(1.4.toFloat(), 1.toFloat())
        Assert.assertEquals(expect.first, result.first)
        Assert.assertEquals(expect.second, result.second)
    }

    @Test
    fun `Get crop center scale matrix with square view, square preview and view size is bigger`() {
        val viewWidth = 1400.toFloat()
        val viewHeight = 1400.toFloat()
        val previewWidth = 800.toFloat()
        val previewHeight = 800.toFloat()
        val result: Pair<Float, Float> = CameraV1Util.getViewScale(viewWidth, viewHeight, previewWidth, previewHeight)
        val expect = Pair(1.toFloat(), 1.toFloat())
        Assert.assertEquals(expect.first, result.first)
        Assert.assertEquals(expect.second, result.second)
    }

    @Test
    fun `Get crop center scale matrix with portrait view, landscape preview and view size is cross-cutting`() {
        val viewWidth = 1080.toFloat()
        val viewHeight = 1689.toFloat()
        val previewWidth = 1920.toFloat()
        val previewHeight = 1080.toFloat()
        val result: Pair<Float, Float> = CameraV1Util.getViewScale(viewWidth, viewHeight, previewWidth, previewHeight)
        val expect = Pair(2.780247.toFloat(), 1.toFloat())
        Assert.assertEquals(expect.first, result.first)
        Assert.assertEquals(expect.second, result.second)
    }

    @Test
    fun `Get exif orientation with 0 degree`() {
        Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, CameraV1Util.getOrientationExifValue(0))
    }

    @Test
    fun `Get exif orientation with 90 degree`() {
        Assert.assertEquals(ExifInterface.ORIENTATION_ROTATE_90, CameraV1Util.getOrientationExifValue(90))
    }

    @Test
    fun `Get exif orientation with 180 degree`() {
        Assert.assertEquals(ExifInterface.ORIENTATION_ROTATE_180, CameraV1Util.getOrientationExifValue(180))
    }

    @Test
    fun `Get exif orientation with 270 degree`() {
        Assert.assertEquals(ExifInterface.ORIENTATION_ROTATE_270, CameraV1Util.getOrientationExifValue(270))
    }

    @Test
    fun `Get exif orientation with 30 degree`() {
        Assert.assertEquals(ExifInterface.ORIENTATION_NORMAL, CameraV1Util.getOrientationExifValue(30))
    }

    @Test
    fun `Get surface rotation with 0 degree`() {
        Assert.assertEquals(0, CameraV1Util.getSurfaceRotation(Surface.ROTATION_0))
    }

    @Test
    fun `Get surface rotation with 90 degree`() {
        Assert.assertEquals(90, CameraV1Util.getSurfaceRotation(Surface.ROTATION_90))
    }

    @Test
    fun `Get surface rotation with 180 degree`() {
        Assert.assertEquals(180, CameraV1Util.getSurfaceRotation(Surface.ROTATION_180))
    }

    @Test
    fun `Get surface rotation with 270 degree`() {
        Assert.assertEquals(270, CameraV1Util.getSurfaceRotation(Surface.ROTATION_270))
    }

    @Test
    fun `Get surface rotation with 30 degree`() {
        Assert.assertEquals(0, CameraV1Util.getSurfaceRotation(30))
    }
}
