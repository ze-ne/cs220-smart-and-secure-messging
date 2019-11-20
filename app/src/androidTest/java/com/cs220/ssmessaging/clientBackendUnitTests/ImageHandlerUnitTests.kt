package com.cs220.ssmessaging.clientBackendUnitTests

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.cs220.ssmessaging.clientBackend.EncryptedMessage
import com.cs220.ssmessaging.clientBackend.ImageHandler
import com.cs220.ssmessaging.clientBackend.ImageMessage
import com.cs220.ssmessaging.clientBackend.User
import junit.framework.Assert.*
import org.junit.Assert
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File

class ImageHandlerUnitTests{
    private val pathToSunflowerImage = "testResources/sunflower.jpg"
    private val pathToDiceImage = "testResources/dice.png"
    private val sunflowerImage : Bitmap = BitmapFactory.decodeFile(pathToSunflowerImage)
    private val diceImage : Bitmap = BitmapFactory.decodeFile(pathToDiceImage)

    private val sunflowerByteArray : ByteArray
    private val diceByteArray : ByteArray

    init {
        // In order to get byte array, need to store image bitmap in stream
        var sunflowerStream : ByteArrayOutputStream = ByteArrayOutputStream()
        sunflowerImage.compress(Bitmap.CompressFormat.JPEG, 100, sunflowerStream)
        sunflowerByteArray = sunflowerStream.toByteArray()

        var diceStream : ByteArrayOutputStream = ByteArrayOutputStream()
        diceImage.compress(Bitmap.CompressFormat.PNG, 100, diceStream)
        diceByteArray = diceStream.toByteArray()
    }

    @Test
    fun testImageHandlerConstructor(){
        // Only one thing to test in image handler constructor and that is what the currently selected image is
        val imageHandler : ImageHandler = ImageHandler()
        assertNull(imageHandler.currentlySelectedImage)
    }

    @Test
    fun testGetUriToImagesDirectory(){
        var uriToImagesDirectory : Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val imageHandler : ImageHandler = ImageHandler()
        assertTrue(uriToImagesDirectory.equals(ImageHandler.uriToImagesDirectory))
    }

    @Test
    fun testGetByteArrayFromImage(){
        assertArrayEquals(sunflowerByteArray, ImageHandler.getByteArrayFromImage(sunflowerImage))
        assertArrayEquals(diceByteArray, ImageHandler.getByteArrayFromImage(diceImage))
        assertArrayEquals(ByteArray(0), ImageHandler.getByteArrayFromImage(null))
    }

    @Test
    fun testGetImageFromByteArray(){
        assertTrue(sunflowerImage.sameAs(ImageHandler.getImageFromByteArray(sunflowerByteArray)))
        assertTrue(diceImage.sameAs(ImageHandler.getImageFromByteArray(diceByteArray)))
        assertNull(ImageHandler.getImageFromByteArray(ByteArray(0)))
    }

    @Test
    fun testGetPNGImageFromStorage(){
        // First need to store image
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MessagingAppImages/sunflower.jpg")
        values.put(MediaStore.Images.Media.IS_PENDING, true)

        val uri: Uri? = MyApplication.appContext!!.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            var outputStream = MyApplication.appContext?.contentResolver?.openOutputStream(uri)
            if (outputStream != null) {
                try {
                    sunflowerImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, false)
            MyApplication.appContext!!.contentResolver.update(uri, values, null, null)

            // Start Test
            val imageHandler = ImageHandler()
            val pngImage = imageHandler.getImageFromStorage(uri)

            // Need to delete image from external storage
            
            // Finally assert equals
            assertTrue(sunflowerImage.sameAs(pngImage))
        }


    }

    @Test
    fun testStoreImageToStorage(){

    }

    @Test
    fun testConvertImageToImageMessage(){
        val imageHandler : ImageHandler = ImageHandler()
        val sunflowerImageMessage : ImageMessage = ImageMessage(sunflowerByteArray, "u1-u2", "u1", "u2", 123)
        val diceImageMessage : ImageMessage = ImageMessage(diceByteArray,"u1-u2", "u1", "u2", 123)

        // Testing begins
        val sunflowerActualMessage = imageHandler.convertImageToImageMessage(sunflowerImage,"u1-u2", "u1", "u2", 123)
        val diceActualMessage = imageHandler.convertImageToImageMessage(diceImage, "u1-u2", "u1", "u2", 123)
        assertTrue(sunflowerImageMessage.mEquals(sunflowerActualMessage))
        assertTrue(diceImageMessage.mEquals(diceActualMessage))
    }

    @Test
    fun testConvertImageMessageToImage(){
        val imageHandler : ImageHandler = ImageHandler()
        val sunflowerImageMessage : ImageMessage = ImageMessage(sunflowerByteArray, "u1-u2", "u1", "u2", 123)
        val diceImageMessage : ImageMessage = ImageMessage(diceByteArray,"u1-u2", "u1", "u2", 123)

        // Testing begins
        assertTrue(sunflowerImage.sameAs(imageHandler.convertImageMessageToImage(sunflowerImageMessage)))
        assertTrue(diceImage.sameAs(imageHandler.convertImageMessageToImage(sunflowerImageMessage)))
    }

    @Test
    fun testConvertImageMessageToImageFailCases(){
        // Only fail cases are if the byte array of the image is nothing or garbled
        val imageHandler : ImageHandler = ImageHandler()
        assertNull(imageHandler.convertImageMessageToImage(ImageMessage(ByteArray(0), "u1-u2", "u1", "u2", 123)))
        assertNull(imageHandler.convertImageMessageToImage(ImageMessage(byteArrayOf(1,2,3), "u1-u2", "u1", "u2", 123)))
    }

}