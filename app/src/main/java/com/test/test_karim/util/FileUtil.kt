package com.test.test_karim.util

import android.app.Activity
import android.content.*
import android.content.res.Resources
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Base64
import android.util.Log
import com.test.test_karim.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.net.HttpURLConnection
import java.nio.channels.FileChannel


object FileUtil {

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
            file.delete()
        }
    }

    fun getRealPathFromURI(activity: Activity, contentUri: Uri): String {
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = activity.managedQuery(contentUri, proj, null, null, null)
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } catch (e: Exception) {
            return contentUri.path!!
        }
    }

    @Throws(IOException::class)
    fun galleryAddPic(context: Context, file: File) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(file)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }

    fun copyTextToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        var cp = clipboard!!.primaryClip
        cp = ClipData.newPlainText("DeviceID", text)
    }

    fun image2String(context: Context, img: File) : String {
        try{
            val bitmap = resize(
                MediaStore.Images.Media.getBitmap(
                    context.contentResolver, Uri.fromFile(
                        img
                    )
                ), 780, 780
            )
            val outputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val imgbyte = outputStream.toByteArray()
            val base64 = Base64.encodeToString(imgbyte, Base64.NO_WRAP)

            return base64
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun string2Image(img: String?) : Bitmap? {
        try{
            img?.let {
                if (it.isEmpty()){
                    return null
                }
                val decodedString = Base64.decode(img, Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(
                    decodedString,
                    0,
                    decodedString.size
                )

                return decodedByte
            } ?: return null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(IllegalArgumentException::class)
    fun convert(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(
            base64Str.substring(base64Str.indexOf(",") + 1),
            Base64.DEFAULT
        )

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    fun resizeBitmap20(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, outputStream)

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    fun resizeBitmap90(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    fun resize(image: Bitmap?, maxWidth: Int, maxHeight: Int): Bitmap? {
        try {
            var image = image
            image?.let {
                if (maxHeight > 0 && maxWidth > 0) {
                    val width = it.width
                    val height = it.height
                    val ratioBitmap = width.toFloat() / height.toFloat()
                    val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

                    var finalWidth = maxWidth
                    var finalHeight = maxHeight
                    if (ratioMax > ratioBitmap) {
                        finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
                    } else {
                        finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
                    }
                    image = Bitmap.createScaledBitmap(it, finalWidth, finalHeight, true)
                    return image
                } else {
                    return image
                }
            } ?: return null
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun setFolderImage(context: Context): Boolean {
        //val folder = File(Environment.getExternalStorageDirectory().toString() + "/SMM-DK/Pictures/")
        val folder = File(ContextWrapper(context).filesDir.absolutePath + "/camera/")
        var success = true
        if (!folder.exists()) {
            success = folder.mkdirs()
        }
        if (success) {
            println("Sukses buat folder images!")
        } else {
            println("Tidak bisa membuat folder !")
        }
        return success
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun backupDB(dbPath: String, backupPath: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val fileDB = File(dbPath)

            if (fileDB.exists()) {
                val folder = File(backupPath).parentFile
                var success = true
                if (!folder.exists()) {
                    success = folder.mkdirs()
                }
                if (success) {
                    println("Sukses buat folder backup!")
                } else {
                    println("Tidak bisa membuat folder !")
                }

                val pathBackup = backupPath
                val fileBackup = File(pathBackup)
                fileDB.copyTo(fileBackup, true)
            }
        }
    }

    fun backUpSMMDKDATA(){
        CoroutineScope(Dispatchers.IO).launch {
            val pathBack = Environment.getExternalStorageDirectory().toString() + "/SMM-DK"
            val pathDest = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.duakelinci.smmdk/.BackUp"
            val src = File(pathBack)
            if (src.exists()) {
                /*val dest = File(pathDest)
                if (dest.exists()) deleteRecursiveFolder(dest)*/
                copyFileOrDirectory(pathBack, pathDest)
            }
        }
    }

    fun restoreSMMDKDATA(saveListener: (Boolean) -> Unit){
        CoroutineScope(Dispatchers.IO).launch {
            val pathBack = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.duakelinci.smmdk/.BackUp/SMM-DK"
            val pathDest = Environment.getExternalStorageDirectory().toString()
            val src = File(pathBack)
            if (src.exists()) {
                try {
                    copyFileOrDirectory(pathBack, pathDest)
                    saveListener(true)
                } catch (e: IOException){
                    saveListener(false)
                    e.printStackTrace()
                } catch (e: Exception){
                    saveListener(false)
                    e.printStackTrace()
                }
            } else saveListener(false)
        }
    }

    fun copyFileOrDirectory(srcDir: String?, dstDir: String?) {
        try {
            val src = File(srcDir)
            val dst = File(dstDir, src.name)
            if (src.isDirectory) {
                val files = src.list()
                val filesLength = files.size
                for (i in 0 until filesLength) {
                    val src1 = File(src, files[i]).path
                    val dst1 = dst.path
                    copyFileOrDirectory(src1, dst1)
                }
            } else {
                copyFile(src, dst)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun copyFile(sourceFile: File?, destFile: File) {
        if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()
        if (!destFile.exists()) {
            destFile.createNewFile()
        }
        var source: FileChannel? = null
        var destination: FileChannel? = null
        try {
            source = FileInputStream(sourceFile).getChannel()
            destination = FileOutputStream(destFile).channel
            destination.transferFrom(source, 0, source.size())
        } finally {
            if (source != null) {
                source.close()
            }
            if (destination != null) {
                destination.close()
            }
        }
    }

    fun restoreDB(dbPath: String, restorePath: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val fileDB = File(dbPath)

            if (fileDB.exists()) {
                val folder = File(restorePath).parentFile
                var success = true
                if (!folder.exists()) {
                    success = folder.mkdirs()
                }
                if (success) {
                    println("Sukses buat folder backup!")
                } else {
                    println("Tidak bisa membuat folder !")
                }

                val pathRestore = restorePath
                val fileRestore = File(pathRestore)
                if (fileDB.copyTo(fileRestore, true).exists()) {
                    println("Success restore")
                }
            }
        }
    }

    @Throws(Exception::class)
    fun deleteImages(context: Context) {
        return
        CoroutineScope(Dispatchers.IO).launch {
            //val folder = File(Environment.getExternalStorageDirectory().toString() + "/SMM-DK/Pictures/")
            val folder = File(ContextWrapper(context).filesDir.absolutePath + "/camera/")
            val child = folder.walk()
            child.forEach {
                if (!it.isDirectory) {
                    it.delete()
                }
            }
        }
    }
    fun apkRoot(): String{
        return android.os.Environment.getExternalStorageDirectory().path+"/SMM-DK/aplikasi/"
    }
    fun imageRoot(): String{
        return android.os.Environment.getExternalStorageDirectory().path+"/SMM-DK/imgAbsensi/"
    }
    fun saveTempoprary(imageBitmap: Bitmap){
        val myDir = File(imageRoot())
        if (!myDir.exists()) myDir.mkdirs()

        val file = File(myDir, "temporary.jpg")
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out)
            out.flush()
            out.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun saveTempopraryWithNAme(imageBitmap: Bitmap, name: String){
        val myDir = File(imageRoot())
        if (!myDir.exists()) myDir.mkdirs()

        val file = File(myDir, name)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out)
            out.flush()
            out.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun doSaveImageToFolder(
        path: String,
        image_name: String,
        imgBitmap: Bitmap?,
        isOkEmptyImage: Boolean = false,
        saveListener: (Boolean, String) -> Unit
    ){
        imgBitmap?.let {
            val myDir = File(path)
            if (!myDir.exists()) myDir.mkdirs()
            val file = File(myDir, image_name)

            if (file.exists()) {
                file.delete()
            }

            try {
                val out = FileOutputStream(file)
                it.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.flush()
                out.close()
                saveListener(true, "Simpan image suksess")
            } catch (e: Exception) {
                e.printStackTrace()
                saveListener(
                    false,
                    "Space penyimpanan internal tidak cukup, hapus sebagian data terlebih dahulu"
                )
            }
        } ?: saveListener(isOkEmptyImage, "Image kosong")
    }

    fun deleteImage(path: String){
        CoroutineScope(Dispatchers.IO).launch {
            val file = File(path)

            if (file.exists()) {
                file.delete()
            }
        }
    }

    fun deleteRecursiveFolder(fileOrDirectory: File) {
        try {
            if (fileOrDirectory.isDirectory)
                for (child in fileOrDirectory.listFiles())
                    deleteRecursiveFolder(child)

            fileOrDirectory.delete()
        } catch (e: IOException){
            e.printStackTrace()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun drawMultilineTextToBitmap(
        gContext: Context,
        gResId: Bitmap,
        gText: String?
    ): Bitmap {

        // prepare canvas
        val resources = gContext.resources
        val scale = resources.displayMetrics.density
        var bitmap = gResId
        var bitmapConfig = bitmap.config
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true)
        val canvas = Canvas(bitmap)

        // new antialiased Paint
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        // text color - #3D3D3D
        //paint.color = ContextCompat.getColor(gContext, R.color.green_default)
        paint.color = Color.GREEN
        // text size in pixels
        paint.setTextSize(12 * scale)
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE)

        // set text width to canvas width minus 16dp padding
        val textWidth = canvas.width - (16 * scale).toInt()

        // init StaticLayout for text
        val textLayout = StaticLayout(
            gText, paint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false
        )

        // get height of multiline text
        val textHeight = textLayout.height

        // get position of text's top left corner
        val x = (bitmap.width - textWidth) / 2
        //val y = (bitmap.height - textHeight) * (4 / 3)
        val y = (bitmap.height - textHeight) * 98 / 100

        // draw text to the Canvas center
        canvas.save()
        canvas.translate(x.toFloat(), y.toFloat())
        textLayout.draw(canvas)
        canvas.restore()
        return bitmap
    }

    fun getBitmapFromURL(src: String): Bitmap? {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            val url = java.net.URL(src)
            val connection = url
                .openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input = connection.getInputStream()
            return BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    fun createNomedia(filePath: String, context: Context){

        var roots: File? = null
        roots = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_AUDIOBOOKS)
                    .toString() + "/" + filePath
            )
        } else {
            File(Environment.getExternalStorageDirectory().toString() + "/" + filePath)
        }
        val path = File(Environment.getExternalStorageDirectory().toString() + "/Android/media/"+BuildConfig.APPLICATION_ID+"/imagess")

        val root = File(path, filePath)
        //val root = File(filePath)
        if (!root.exists()) {
            root.mkdirs()
        }
        val str = "/storage/emulated/0/Android/media/com.test.test_karim/Images/pppp.jpg"
        val gpxfile = File(root, "nomedia.jpg")
        Log.d("lmalldld", gpxfile.absolutePath)
        if (!gpxfile.exists()) {
            val writer = FileWriter(gpxfile)
            writer.flush()
            writer.close()
        }
    }
}