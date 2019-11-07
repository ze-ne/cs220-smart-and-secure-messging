package com.cs220.ssmessaging.clientBackendUnitTests

import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

import com.cs220.ssmessaging.clientBackend.CipherExtension
import org.junit.Before
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

@RunWith(MockitoJUnitRunner::class)
class CipherExtensionUnitTests{

    private val keyPairGen : KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
    private val keyPair1 = keyPairGen.generateKeyPair()
    private val keyPair2 = keyPairGen.generateKeyPair()
    private var testPrivateKey : PrivateKey = keyPair1.private
    private var testPublicKey : PublicKey = keyPair1.public
    private var testPublicKey2 : PublicKey = keyPair2.public

    @Test
    fun testConstructorAndKeyAndCipherGetters() {
        // Test normal conditions
        // Note that this test also tests the key and getters because it uses the
        // getters to verify results.
        var testCipherExtension =
            CipherExtension(testPrivateKey, mutableMapOf("person1" to testPublicKey, "person2" to testPublicKey2))
        assertEquals("RSA", testCipherExtension.decryptorCipher.algorithm)
        assertEquals("RSA", testCipherExtension.encryptorCipher.algorithm)
        assertEquals(testCipherExtension.publicKeyRing.size, 2)
        assertEquals(testCipherExtension.publicKeyRing["person1"]?.encoded, testPublicKey.encoded)
        assertEquals(testCipherExtension.publicKeyRing["person2"]?.encoded, testPublicKey2.encoded)
        assertEquals(testCipherExtension.privateKey.encoded, testPublicKey2.encoded)
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

    // The next tests test the case where encryption/decryption can fail
    // (e.g. public key not found in key ring, or message is bad)
    @Test
    fun testEncryptUnencryptedMessageFailNoPublicKey(){
    }

    @Test
    fun testEncryptUnencryptedMessageFailBadMessage(){
        // No Bytes in message
    }

    @Test
    fun testDecryptEncryptedMessageFailBadMessage(){
    }
}