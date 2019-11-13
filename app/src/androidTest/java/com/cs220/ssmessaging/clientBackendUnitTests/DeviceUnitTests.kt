package com.cs220.ssmessaging.clientBackendUnitTests

import com.cs220.ssmessaging.MyApplication.MyApplication
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith

import com.cs220.ssmessaging.clientBackend.Device
import org.junit.After

import java.io.File
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*


class DeviceUnitTests{
    private val expectedPublicKeyPath : String = MyApplication.appContext?.getFilesDir()?.path.toString() + "myKey.publicKey"
    private val expectedPrivateKeyPath : String = MyApplication.appContext?.getFilesDir()?.path.toString() + "myKey.privateKey"

    @After
    fun deleteTestKeys(){
        var privateKeyFile = File(expectedPrivateKeyPath)
        var publicKeyFile = File(expectedPublicKeyPath)
        privateKeyFile.delete()
        publicKeyFile.delete()
    }

    @Test
    fun testDefaultConstructor() {
        val testDevice = Device()

        // Assert that path is correct
        // UNIT TEST FIX: the expected values were flipped
        assertEquals(expectedPublicKeyPath, testDevice.pathToMyPublicKey)
        assertEquals(expectedPrivateKeyPath, testDevice.pathToMyPrivateKey)

        // Assert that the files exist
        var privateKeyFile = File(expectedPrivateKeyPath)
        assertTrue(privateKeyFile.exists())
        var publicKeyFile = File(expectedPublicKeyPath)
        assertTrue(publicKeyFile.exists())

        // Verify that the keys are valid by
        // creating new keys from the bytes of the files
        try {
            var publicKeyBytes : ByteArray = Files.readAllBytes(publicKeyFile.toPath())
            var privateKeyBytes : ByteArray = Files.readAllBytes(privateKeyFile.toPath())
            val keyFactory = KeyFactory.getInstance("RSA") // or "EC" or whatever
            // If these two functions pass, we are fine. If these fail, we are not fine.
            keyFactory.generatePrivate(PKCS8EncodedKeySpec(privateKeyBytes))
            keyFactory.generatePublic(X509EncodedKeySpec(publicKeyBytes))
        }
        catch(e : Exception){
            fail("Failed to create valid keys")
        }

        // If cipher not initialized, this will throw an exception.
        try{
            testDevice.cipher
        }
        catch(e : UninitializedPropertyAccessException){
            fail("Cipher uninitialized when it should be")
        }
    }

    @Test
    fun testDefaultConstructorExistingKeys() {
        // Test to see if constructor correctly uses existing keys
        val testDevice = Device()

        // First assert that. We need this to know the behavior of the second called constructor
        var privateKeyFile = File(expectedPrivateKeyPath)
        assertTrue(privateKeyFile.exists())
        var publicKeyFile = File(expectedPublicKeyPath)
        assertTrue(publicKeyFile.exists())

        var oldPublicKeyBytes : ByteArray = Files.readAllBytes(publicKeyFile.toPath())
        var oldPrivateKeyBytes : ByteArray = Files.readAllBytes(privateKeyFile.toPath())
        val testDevice2 = Device()

        // Assert that the files still exist
        privateKeyFile = File(expectedPrivateKeyPath)
        assertTrue(privateKeyFile.exists())
        publicKeyFile = File(expectedPublicKeyPath)
        assertTrue(publicKeyFile.exists())

        // Finally assert that keys have not changed
        var newPublicKeyBytes : ByteArray = Files.readAllBytes(publicKeyFile.toPath())
        var newPrivateKeyBytes : ByteArray = Files.readAllBytes(privateKeyFile.toPath())

        /* Unit Test FIX: We used the wrong assert when asserting if arrays are equal.
         * We have to use assertArrayEquals rather than assertEquals.
         * assertEquals only asserts whether the arrays are the same exact object, not their contents.
         */
        assertArrayEquals(oldPublicKeyBytes, newPublicKeyBytes)
        assertArrayEquals(oldPrivateKeyBytes, newPrivateKeyBytes)
    }

    @Test
    fun testGetPathToMyPrivateKey(){
        // Since there is only one path, there is only one test case
        val testDevice = Device()
        assertEquals(expectedPrivateKeyPath, testDevice.pathToMyPrivateKey)
    }

    @Test
    fun testGetPathToMYPublicKey(){
        // Since there is only one path, there is only one test case
        val testDevice = Device()
        assertEquals(expectedPublicKeyPath, testDevice.pathToMyPublicKey)
    }

    @Test
    fun testGenerateNewKeyPair(){
        var testDevice = Device()
        var privateKeyFile = File(expectedPrivateKeyPath)
        var publicKeyFile = File(expectedPublicKeyPath)
        var oldPublicKeyBytes : ByteArray
        var oldPrivateKeyBytes : ByteArray

        try {
            oldPublicKeyBytes = Files.readAllBytes(publicKeyFile.toPath())
            oldPrivateKeyBytes = Files.readAllBytes(privateKeyFile.toPath())


            testDevice.generateNewKeyPair()

            // Verify that the files still exist
            privateKeyFile = File(expectedPrivateKeyPath)
            assertTrue(privateKeyFile.exists())
            publicKeyFile = File(expectedPublicKeyPath)
            assertTrue(publicKeyFile.exists())

            // Finally assert that keys have actually changed
            var newPublicKeyBytes : ByteArray = Files.readAllBytes(publicKeyFile.toPath())
            var newPrivateKeyBytes : ByteArray = Files.readAllBytes(privateKeyFile.toPath())

            /* Unit Test FIX: We used the wrong assert when asserting if arrays are equal.
             * assertEquals only asserts whether the arrays are the same exact object, not their contents.
             */
            assertFalse(oldPublicKeyBytes.contentEquals(newPublicKeyBytes))
            assertFalse(oldPrivateKeyBytes.contentEquals(newPrivateKeyBytes))
        }
        catch(e : NoSuchFileException){
            fail("Keys not found in path")
        }
    }
}