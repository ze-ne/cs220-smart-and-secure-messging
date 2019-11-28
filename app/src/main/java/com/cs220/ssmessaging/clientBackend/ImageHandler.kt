package com.cs220.ssmessaging.clientBackend

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.cs220.ssmessaging.MyApplication.MyApplication
import java.io.ByteArrayOutputStream

object ImageHandler {
    val uriToImagesDirectory : Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    // helper function that converts bitmap to byte array
    fun getByteArrayFromImage(image : Bitmap?) : ByteArray {
        if(image == null)
            return byteArrayOf()

        val imageByteStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, imageByteStream)

        return imageByteStream.toByteArray()
    }

    // helperfunction that converts byte array to bitmap
    fun getImageFromByteArray(image : ByteArray) : Bitmap? {
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }

    var currentlySelectedImage : Bitmap? = BitmapFactory.decodeByteArray(ByteArray(0), 0, 0)

    // loads an image from the gallery and convert the Bitmap to bytearray
    // returns the image as bitmap and stores the image as a bitmap in currentlySelectedImage
    fun getImageFromStorage(uri : Uri) : Bitmap? {
        try{
            val bitmap = MediaStore.Images.Media.getBitmap(MyApplication.appContext!!.contentResolver, uri)
            currentlySelectedImage = bitmap
            return bitmap
        } catch (error : Exception){
            return null
        }
    }

    // Takes a bitmap of an image, converts it to a bitmap, and then stores the image to local gallery
    // Returns null if image was not stored. Otherwise, return the Uri to the image
    // The name must include the extension: jpg, jpeg, or png
    fun storeImageToStorage(fileName : String, image : Bitmap) : Uri? {
        val fileNameComponents : List<String> = fileName.split(".")
        val mimeType : String

        if(fileNameComponents.size == 2){
            mimeType = fileNameComponents[1]
            if(!(mimeType.equals( "png", true) ||
                mimeType.equals( "jpg", true) ||
                mimeType.equals( "jpeg", true)))
                return null
        }
        else
            return null

        // properties of the image to be stored
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + mimeType)
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MessagingAppImages")
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Images.Media.IS_PENDING, true)

        // The intended URI of the image to be stored. uri can be null and thus we mus
        val uri: Uri? = MyApplication.appContext!!.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            var outputStream = MyApplication.appContext?.contentResolver?.openOutputStream(uri)
            if (outputStream != null) {
                try {
                    // Compressing with PNG is lossless and should be fine if the quality is 100
                    image.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, false)
            MyApplication.appContext!!.contentResolver.update(uri, values, null, null)
            values.clear()
            return uri
        }
        else {
            return null
        }
    }

    // Converts a bitmap to image message when given senderId and recipientId
    fun convertImageToImageMessage(image : Bitmap, conversationId : String, senderId : String, recipientId : String, timestamp: Long) : ImageMessage {
        return ImageMessage(getByteArrayFromImage(image), conversationId, senderId, recipientId, timestamp)
    }

    // Converts an image message to actual image bitmap
    fun convertImageMessageToImage(imageMessage: ImageMessage) : Bitmap? {
        return getImageFromByteArray(imageMessage.message)
    }
}