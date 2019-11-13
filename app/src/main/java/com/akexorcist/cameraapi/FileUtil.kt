package com.akexorcist.cameraapi

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object FileUtil {
    private fun getCurrentDate(): String {
        val simpleDateFormat = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val date = Date()
        return simpleDateFormat.format(date)
    }

    fun savePicture(context: Context, data: ByteArray): File? {
        val fileName = "${getCurrentDate()}.jpg"
        val filePath = context.getExternalFilesDir(null)?.absolutePath
        val file = File(filePath, fileName)
        try {
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
            val fos = FileOutputStream(file)
            fos.write(data)
            fos.flush()
            fos.close()
            return file
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun updateMediaScanner(context: Context, file: File?) {
        if (file != null) {
            return
        }
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.fromFile(file)
        context.sendBroadcast(intent)
    }
}