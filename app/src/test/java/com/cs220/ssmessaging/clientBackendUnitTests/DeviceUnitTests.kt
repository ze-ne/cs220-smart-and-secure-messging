package com.cs220.ssmessaging.clientBackendUnitTests

import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

import com.cs220.ssmessaging.clientBackend.Device
import org.junit.After

import java.io.File
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec


@RunWith(MockitoJUnitRunner::class)
class DeviceUnitTests{

    @After
    fun deleteTestKeys(){
        var privateKeyFile = File("res/keys/myKey.privateKey")
        var publicKeyFile = File("res/keys/myKey.publicKey")
        privateKeyFile.delete()
        publicKeyFile.delete()
    }

    @Test
    fun testDefaultConstructor() {
        val testDevice = Device()

        // Assert that path is correct
        assertEquals("res/keys/myKey.privateKey", testDevice.pathToMyPublicKey)
        assertEquals("res/keys/myKey.publicKey", testDevice.pathToMyPrivateKey)

        // Assert that the files exist
        var privateKeyFile = File("res/keys/myKey.privateKey")
        assertTrue(privateKeyFile.exists())
        var publicKeyFile = File("res/keys/myKey.publicKey")
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
        var privateKeyFile = File("res/keys/myKey.privateKey")
        assertTrue(privateKeyFile.exists())
        var publicKeyFile = File("res/keys/myKey.publicKey")
        assertTrue(publicKeyFile.exists())

        var oldPublicKeyBytes : ByteArray = Files.readAllBytes(publicKeyFile.toPath())
        var oldPrivateKeyBytes : ByteArray = Files.readAllBytes(privateKeyFile.toPath())
        val testDevice2 = Device()

        // Assert that the files still exist
        privateKeyFile = File("res/keys/myKey.privateKey")
        assertTrue(privateKeyFile.exists())
        publicKeyFile = File("res/keys/myKey.publicKey")
        assertTrue(publicKeyFile.exists())

        // Finally assert that keys have not changed
        var newPublicKeyBytes : ByteArray = Files.readAllBytes(publicKeyFile.toPath())
        var newPrivateKeyBytes : ByteArray = Files.readAllBytes(privateKeyFile.toPath())

        assertEquals(oldPublicKeyBytes, newPublicKeyBytes)
        assertEquals(oldPrivateKeyBytes, newPrivateKeyBytes)
    }

    @Test
    fun testGetPathToMyPrivateKey(){
        // Since there is only one path, there is only one test case
        val testDevice = Device()
        assertEquals("res/keys/myKey.privateKey", testDevice.pathToMyPrivateKey)
    }

    @Test
    fun testGetPathToMYPublicKey(){
        // Since there is only one path, there is only one test case
        val testDevice = Device()
        assertEquals("res/keys/myKey.publicKey", testDevice.pathToMyPublicKey)
    }

    @Test
    fun testGenerateNewKeyPair(){
        var testDevice = Device()
        var privateKeyFile = File("res/keys/myKey.privateKey")
        var publicKeyFile = File("res/keys/myKey.publicKey")
        var oldPublicKeyBytes : ByteArray
        var oldPrivateKeyBytes : ByteArray

        try {
            oldPublicKeyBytes = Files.readAllBytes(publicKeyFile.toPath())
            oldPrivateKeyBytes = Files.readAllBytes(privateKeyFile.toPath())


            testDevice.generateNewKeyPair()

            // Verify that the files still exist
            privateKeyFile = File("res/keys/myKey.privateKey")
            assertTrue(privateKeyFile.exists())
            publicKeyFile = File("res/keys/myKey.publicKey")
            assertTrue(publicKeyFile.exists())

            // Finally assert that keys have actually changed
            var newPublicKeyBytes : ByteArray = Files.readAllBytes(publicKeyFile.toPath())
            var newPrivateKeyBytes : ByteArray = Files.readAllBytes(privateKeyFile.toPath())

            assertNotEquals(oldPublicKeyBytes, newPublicKeyBytes)
            assertNotEquals(oldPrivateKeyBytes, newPrivateKeyBytes)
        }
        catch(e : NoSuchFileException){
            fail("Keys not found in path")
        }
    }
}