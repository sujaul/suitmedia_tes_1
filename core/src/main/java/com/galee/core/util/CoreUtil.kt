package com.galee.core.util

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Base64
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.galee.core.Constant
import id.zelory.compressor.Compressor
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

object CoreUtil {

    /**
     * FUNCTION TO DELETE FOLDER OR FILE
     * @param file
     *
     */
    @Throws(Exception::class)
    fun delete(file: File) {
        if (file.isDirectory) {
            file.listFiles().forEach {
                delete(it)
            }
        } else if (file.isFile) {
            println("is File and ${file.absolutePath}")
            file.delete()
        }
    }

    @Throws(Exception::class)
    fun delete2(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            fileOrDirectory.listFiles().forEach {
                delete2(it)
            }
        }

        if (fileOrDirectory.exists()) {
            val b = fileOrDirectory.delete()
            if (b) {
                Timber.d("FileUtil: delete success")
            } else {
                Timber.e("FileUtil: delete failed")
            }
        }
    }

    /* OLD
    fun getRealPathFromURI(activity: Activity, contentUri: Uri): String? {
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = activity.managedQuery(contentUri, proj, null, null, null)
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return contentUri.path
    }*/

    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            val result = columnIndex?.let { cursor.getString(it) } ?: contentUri.path
            cursor?.close()
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return contentUri.path
    }

    @Throws(IOException::class)
    fun galleryAddPic(context: Context, file: File?) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        file?.let {
            val contentUri = Uri.fromFile(file)
            mediaScanIntent.data = contentUri
        }
        context.sendBroadcast(mediaScanIntent)
    }

    fun convertStringToUri(context: Context, stringImage: String, title: String = "Title"): Uri {
        val bytes = ByteArrayOutputStream()
        val bitmap = convertStringToBitmap(stringImage)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            bitmap,
            title,
            null
        )
        return Uri.parse(path)
    }

    fun convertStringToBitmap(imageString: String): Bitmap {
        val decodedString = Base64.decode(imageString, Base64.NO_WRAP)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun convertFileToBitmap(fileImage: File): Bitmap {
        return imgFileToBitmap(fileImage)
    }

    fun convertFileToBitmap(context: Context, fileImage: File): Bitmap {
        return imgFileToBitmap(context, fileImage)
    }

    fun convertImgToString(fileImage: File, compressQuality: Int = 100): String {
        val bitmap = imgFileToBitmap(fileImage)
        return bitmapToString(bitmap, compressQuality)
    }

    fun convertImgToString(context: Context, fileImage: File, compressQuality: Int = 100): String {
        val bitmap = imgFileToBitmap(context, fileImage)
        return bitmapToString(bitmap, compressQuality)
    }

    fun convertImgToString(bitmap: Bitmap, compressQuality: Int = 100): String {
        return bitmapToString(bitmap, compressQuality)
    }

    private fun bitmapToString(bitmap: Bitmap, compressQuality: Int): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, outputStream)
        val byte = outputStream.toByteArray()
        return Base64.encodeToString(byte, Base64.NO_WRAP)
    }

    private fun imgFileToBitmap(fileImage: File): Bitmap {
        val filePath = fileImage.path
        return BitmapFactory.decodeFile(filePath)
    }

    private fun imgFileToBitmap(context: Context, fileImage: File): Bitmap {
        return MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.fromFile(fileImage))
    }

    fun isSelectedView(isSelected: Boolean, view: View) {
        if (isSelected) {
            view.visible()
        } else {
            view.invisible()
        }
    }

    fun isEnabledClick(
        context: Context,
        isEnabled: Boolean,
        button: View,
        @ColorRes colorEnable: Int,
        @ColorRes colorDisable: Int
    ) {
        button.isEnabled = isEnabled
        if (isEnabled) {
            button.setBackgroundColor(ContextCompat.getColor(context, colorEnable))
        } else {
            button.setBackgroundColor(ContextCompat.getColor(context, colorDisable))
        }
    }

    fun dpToPx(context: Context, dp: Int): Int {
        val resource = context.resources
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                resource.displayMetrics
            )
        )
    }

    fun drawMultilineTextToBitmap(
        context: Context,
        bitmap: Bitmap,
        text: String,
        colorText: Int
    ): Bitmap {
        val scale = context.resources.displayMetrics.density

        var bitmapConfig = bitmap.config
        if (bitmapConfig == null)
            bitmapConfig = Bitmap.Config.ARGB_8888

        val mBitmap = bitmap.copy(bitmapConfig, true)

        val canvas = Canvas(mBitmap)
        // new antialiased Paint
        val paint = TextPaint(Paint(Paint.ANTI_ALIAS_FLAG))
        // text color - #3D3D3D
        paint.color = colorText
        // text size in pixels
        paint.textSize = 12 * scale
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE)

        // set text width to canvas width minus 16dp padding
        val textWidth = (canvas.width - 16 * scale)
        val textLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val sb = StaticLayout.Builder.obtain(text, 0, text.length, paint, textWidth.toInt())
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(0.0f, 1.0f)
                .setIncludePad(false)
            sb.build()
        } else {
            StaticLayout(
                text,
                paint,
                textWidth.toInt(),
                Layout.Alignment.ALIGN_CENTER,
                1.0f,
                0.0f,
                false
            )
        }
        /*val textLayout =
            StaticLayout(
                text,
                paint,
                textWidth.toInt(),
                Layout.Alignment.ALIGN_CENTER,
                1.0f,
                0.0f,
                false
            )*/
        val textHeight = textLayout.height
        val x = (mBitmap.width - textWidth) / 2
        val y = (mBitmap.height - textHeight) * 8 / 9
        canvas.save()
        canvas.translate(x, y.toFloat())
        textLayout.draw(canvas)
        canvas.restore()
        return mBitmap
    }

    fun backupFileDB(context: Context):Boolean {
        try {
            val sd = Environment.getExternalStorageDirectory()

            if (sd.canWrite()) {
                val currentDBPath = context.getDatabasePath(Constant.DATABASE_NAME).absolutePath
                val backupDBPath = "${Constant.DATABASE_NAME}.db"
                val currentDB = File(currentDBPath)
                val backupDB = File(sd, backupDBPath)

                val src = FileInputStream(currentDB).channel
                val dst = FileInputStream(backupDB).channel
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()
                return true
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

}