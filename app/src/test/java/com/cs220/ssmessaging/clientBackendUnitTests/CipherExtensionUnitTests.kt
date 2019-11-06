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
        assertEquals("RSA", testCipherExtension.javaCipher.algorithm)
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
}