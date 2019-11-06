package com.cs220.ssmessaging.clientBackendUnitTests

import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

import com.cs220.ssmessaging.clientBackend.CipherExtension

@RunWith(MockitoJUnitRunner::class)
class CipherExtensionUnitTests{

    @Test
    fun testConstructor() {
        // Test the constructor for correct platform.
        // There is only one constructor test case because the constructor takes in no parameters
        val testCipherExtension = CipherExtension()
        assertEquals("RSA", testCipherExtension.javaEncryptCipher.algorithm)
        assertEquals("RSA", testCipherExtension.javaDecryptCipher.algorithm)
    }

    @Test
    fun testEncryptUnencryptedMessageText(){

    }

    @Test
    fun testEncryptUnencryptedMessageImage(){

    }

    @Test
    fun testDecryptEncryptedMessageImage(){

    }

    @Test
    fun testDecryptEncryptedMessageText(){

    }

    //The next tests test the case where encryption/decryption can fail
    @Test
    fun testEncryptUnencryptedMessageFail(){
        // If there is a failure,
    }
}