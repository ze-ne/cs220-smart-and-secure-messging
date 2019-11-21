package com.cs220.ssmessaging.clientBackend

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.sql.Timestamp

class ImageHandler {
    companion object{
        val uriToImagesDirectory : Uri = Uri.EMPTY

        // helper function that converts bitmap to byte array
        fun getByteArrayFromImage(image : Bitmap?) : ByteArray {
            return byteArrayOf(1,2,3)
        }

        // helperfunction that converts byte array to bitmap
        fun getImageFromByteArray(image : ByteArray) : Bitmap? {
            return null
        }
    }

    var currentlySelectedImage : Bitmap? = BitmapFactory.decodeByteArray(ByteArray(0), 0, 0)

    // loads an image from the gallery and convert the Bitmap to bytearray
    // returns the image as bitmap and stores the image as a bitmap in currentlySelectedImage
    fun getImageFromStorage(uri : Uri) : Bitmap? {
        return null
    }

    // Takes a bitmap of an image, converts it to a bitmap, and then stores the image to local gallery
    // Returns null if image was not stored. Otherwise, return the Uri to the image
    fun storeImageToStorage(name : String, image : Bitmap) : Uri? {
        return Uri.EMPTY
    }

    // Converts a bitmap to image message when given senderId and recipientId
    fun convertImageToImageMessage(image : Bitmap, conversationId : String, senderId : String, recipientId : String, timestamp: Long) : ImageMessage {
        return ImageMessage(ByteArray(0), "", "", "", -1)
    }

    // Converts an image message to actual image bitmap
    fun convertImageMessageToImage(imageMessage: ImageMessage) : Bitmap? {
        return BitmapFactory.decodeByteArray(ByteArray(0), 0, 0)
    }
}