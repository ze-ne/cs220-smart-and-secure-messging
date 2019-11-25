package com.cs220.ssmessaging.clientBackendUnitTests

import android.content.ContentValues
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.Image
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
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
    private val sunflowerImage : Bitmap
    private val diceImage : Bitmap

    private val sunflowerByteArray : ByteArray
    private val diceByteArray : ByteArray

    init {
        // Get the images from the resources folder
        val assets = InstrumentationRegistry.getInstrumentation().context.assets
        val sunflowerInput = assets.open("sunflower.jpg")
        val diceInput = assets.open("dice.png")
        sunflowerImage = BitmapFactory.decodeStream(sunflowerInput)
        diceImage = BitmapFactory.decodeStream(diceInput)

        // In order to get byte array, need to store image bitmap in stream
        var sunflowerStream : ByteArrayOutputStream = ByteArrayOutputStream()
        // Fix: We don't want to lose quality on each send. Thus, we compress with PNG (even when the bitmap is from a jpeg)
        sunflowerImage.compress(Bitmap.CompressFormat.PNG, 100, sunflowerStream)
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
    fun testGetJPGImageFromStorage(){
        // First need to store image
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MessagingAppImages")
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "sunflower.jpg")
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
            values.clear()

            // Start Test
            val imageHandler = ImageHandler()
            val jpgImage = imageHandler.getImageFromStorage(uri)

            // Need to delete image from external storage
            val deleteUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendQueryParameter(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MessagingAppImages").build()
            MyApplication.appContext?.contentResolver?.delete(deleteUri, null, null)

            // Finally assert equals
            assertTrue(sunflowerImage.sameAs(jpgImage))
            assertNotNull(imageHandler.currentlySelectedImage)
            assertTrue(sunflowerImage.sameAs(imageHandler.currentlySelectedImage))
        }
        else {
            fail()
        }
    }

    @Test
    fun testGetPNGImageFromStorage(){
        // First need to store image
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MessagingAppImages")
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "dice.png")
        values.put(MediaStore.Images.Media.IS_PENDING, true)

        val uri: Uri? = MyApplication.appContext!!.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            var outputStream = MyApplication.appContext?.contentResolver?.openOutputStream(uri)
            if (outputStream != null) {
                try {
                    diceImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, false)
            MyApplication.appContext!!.contentResolver.update(uri, values, null, null)
            values.clear()

            // Start Test
            val imageHandler = ImageHandler()
            val pngImage = imageHandler.getImageFromStorage(uri)

            // Need to delete image from external storage
            val deleteUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendQueryParameter(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MessagingAppImages").build()
            MyApplication.appContext?.contentResolver?.delete(deleteUri, null, null)

            // Finally assert equals
            assertTrue(diceImage.sameAs(pngImage))
            assertNotNull(imageHandler.currentlySelectedImage)
            assertTrue(diceImage.sameAs(imageHandler.currentlySelectedImage))
        }
        else {
            fail()
        }
    }

    @Test
    fun testStoreImageToStorage(){
        val imageHandler = ImageHandler()

        val sunflowerUri = imageHandler.storeImageToStorage("sunflower.jpg", sunflowerImage)
        val diceUri = imageHandler.storeImageToStorage("dice.png", diceImage)

        // FIX: We discovered that building a URI for expected not the same is MediaStore generated URIs.
        /*val expectedSunflowerUri =  MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
            .appendQueryParameter(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MessagingAppImages")
            .appendQueryParameter(MediaStore.Images.Media.DISPLAY_NAME, "sunflower.jpg")
            .build()*/
        assertNotNull(sunflowerUri)
        //assertTrue(sunflowerUri!!.equals(expectedSunflowerUri))

       /* val expectedDiceUri =  MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
            .appendQueryParameter(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MessagingAppImages")
            .appendQueryParameter(MediaStore.Images.Media.DISPLAY_NAME, "dice.png")
            .build()*/
        assertNotNull(diceUri)
        //assertTrue(diceUri!!.equals(expectedDiceUri))

        // FIX: Instead of using expected URI, we are using the returned URI for the same reason above: MediaStore stores images differently
        // FIX: Used wrong method for retrieving bitmap from URI. Now using the correct code.
        val sunflowerBitmap  = MediaStore.Images.Media.getBitmap(MyApplication.appContext!!.contentResolver, sunflowerUri)
        val diceBitmap  = MediaStore.Images.Media.getBitmap(MyApplication.appContext!!.contentResolver, diceUri)

        // FIX: Need to delete images after getting the bitmap
        val deleteUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendQueryParameter(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MessagingAppImages").build()
        MyApplication.appContext?.contentResolver?.delete(deleteUri, null, null)

        assertTrue(sunflowerImage.sameAs(sunflowerBitmap))
        assertTrue(diceImage.sameAs(diceBitmap))
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
        // FIX: Wrong expected image. Changed to correct image.
        assertTrue(diceImage.sameAs(imageHandler.convertImageMessageToImage(diceImageMessage)))
    }

    @Test
    fun testConvertImageMessageToImageFailCases(){
        // Only fail cases are if the byte array of the image is nothing or garbled
        val imageHandler : ImageHandler = ImageHandler()
        assertNull(imageHandler.convertImageMessageToImage(ImageMessage(ByteArray(0), "u1-u2", "u1", "u2", 123)))
        assertNull(imageHandler.convertImageMessageToImage(ImageMessage(byteArrayOf(1,2,3), "u1-u2", "u1", "u2", 123)))
    }

}