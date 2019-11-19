package com.cs220.ssmessaging.clientBackend

import android.graphics.Bitmap
import android.graphics.BitmapFactory

class ImageHandler {
    companion object{
        val pathToGalleryDirectory : String = ""

        // helper function that converts bitmap to byte array
        fun getByteArrayFromImage(image : Bitmap) : ByteArray {
            return ByteArray(0)
        }

        // helperfunction that converts byte array to bitmap
        fun getImageFromByteArray() : Bitmap? {
            return null
        }
    }

    var currentlySelectedImage : Bitmap? = null

    // loads an image from the gallery and convert the Bitmap to bytearray
    // returns the image as bitmap and stores the image as a bitmap
    fun getImageFromGallery(path : String) : Bitmap {
        return BitmapFactory.decodeByteArray(ByteArray(0), 0, 0)
    }

    // Takes a bitmap of an image, converts it to a bitmap, and then stores the image to local gallery
    fun storeImageToGallery(path : String, image : Bitmap) : Boolean {
        return false
    }

    // Converts a bitmap to image message when given senderId and recipientId
    fun convertImageToImageMessage(senderId : String, recipientId : String, image : Bitmap) : ImageMessage {
        return ImageMessage(ByteArray(0), "", "", "", -1)
    }

    // Converts an image message to actual image bitmap
    fun convertImageMessageToImage(imageMessage: ImageMessage) : Bitmap {
        return BitmapFactory.decodeByteArray(ByteArray(0), 0, 0)
    }
}