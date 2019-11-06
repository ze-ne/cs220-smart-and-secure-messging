package com.cs220.ssmessaging.clientBackendUnitTests

import com.cs220.ssmessaging.clientBackend.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

import org.junit.Before
import org.w3c.dom.Text
import java.lang.Exception
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

@RunWith(MockitoJUnitRunner::class)
class CipherExtensionUnitTests{

    private val keyPairGen : KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
    private val keyPair1 = keyPairGen.generateKeyPair()
    private val keyPair2 = keyPairGen.generateKeyPair()
    private var testPrivateKey : PrivateKey = keyPair1.private
    private var testPrivateKey2 : PrivateKey = keyPair2.private
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
    fun testSetPublicKeyRings(){
        // Test setting the public key rings for 0 size, 1 size, and multiple size
        var testCipherExtension =
            CipherExtension(testPrivateKey, mutableMapOf("person1" to testPublicKey, "person2" to testPublicKey2))
    }

    @Test
    fun testSetPrivateKey(){
    }

    @Test
    fun testAddPublicKeyToKeyRing(){
        // Test
    }

    @Test
    fun testEncryptAndDecryptMessageText(){
        // Tests the case when only one message encrypted and multiple messages encrypted.
        var testCipherExtension =
            CipherExtension(testPrivateKey2, mutableMapOf("person1" to testPublicKey, "person2" to testPublicKey2))

        // Create new user
        var sendUser : User = User("person1", "test", "User")
        var receiveUser : User = User("person2", "test", "User2")
        // Create new unencrypted text messages
        var textMessage : TextMessage = TextMessage("ABC", "testId", sendUser, receiveUser, 0)
        var textMessage2 : TextMessage = TextMessage("sad46546 ----", "testId2", sendUser, receiveUser, 123)

        var encryptedMessage = testCipherExtension.encryptUnencryptedMessage(textMessage)
        var encryptedMessage2 = testCipherExtension.encryptUnencryptedMessage(textMessage2)

        // Decrypt and verify
        var decryptedMessage = testCipherExtension.decryptEncryptedMessage(encryptedMessage)
        var decryptedMessage2 = testCipherExtension.decryptEncryptedMessage(encryptedMessage2)

        var message : String = (decryptedMessage2 as TextMessage).message
        var message2 : String = (decryptedMessage as TextMessage).message

        assertEquals("ABC", message)
        assertEquals("testId", encryptedMessage.conversationId)
        assertEquals("person1", encryptedMessage.sender.userId)
        assertEquals("person2", encryptedMessage.sender.userId)
        assertEquals(0, encryptedMessage.timestamp)

        assertEquals("sad46546 ----", message2)
        assertEquals("testId2", encryptedMessage2.conversationId)
        assertEquals("person1", encryptedMessage2.sender.userId)
        assertEquals("person2", encryptedMessage2.sender.userId)
        assertEquals(123, encryptedMessage.timestamp)
    }

    @Test
    fun testEncryptAndDecryptMessageImage(){
        // Tests the case when only one message encrypted and multiple messages encrypted.
        var testCipherExtension =
            CipherExtension(testPrivateKey2, mutableMapOf("person1" to testPublicKey, "person2" to testPublicKey2))

        // Create new user
        var sendUser : User = User("person1", "test", "User")
        var receiveUser : User = User("person2", "test", "User2")
        // Create new unencrypted text messages
        var imgMessage : ImageMessage = ImageMessage(ByteArray(10), "testId", sendUser, receiveUser, 0)
        var imgMessage2 : ImageMessage = ImageMessage(ByteArray(10), "testId2", sendUser, receiveUser, 123)

        var encryptedMessage = testCipherExtension.encryptUnencryptedMessage(imgMessage)
        var encryptedMessage2 = testCipherExtension.encryptUnencryptedMessage(imgMessage2)

        // Decrypt and verify
        var decryptedMessage = testCipherExtension.decryptEncryptedMessage(encryptedMessage)
        var decryptedMessage2 = testCipherExtension.decryptEncryptedMessage(encryptedMessage2)

        var message : ByteArray = (decryptedMessage2 as ImageMessage).message
        var message2 : ByteArray = (decryptedMessage as ImageMessage).message

        assertEquals(imgMessage.message, message)
        assertEquals("testId", encryptedMessage?.conversationId)
        assertEquals("person1", encryptedMessage?.sender?.userId)
        assertEquals("person2", encryptedMessage?.sender?.userId)
        assertEquals(0, encryptedMessage?.timestamp)

        assertEquals(imgMessage2.message, message2)
        assertEquals("testId2", encryptedMessage2?.conversationId)
        assertEquals("person1", encryptedMessage2?.sender?.userId)
        assertEquals("person2", encryptedMessage2?.sender?.userId)
        assertEquals(123, encryptedMessage?.timestamp)
    }

    // The next tests test the case where encryption/decryption can fail
    // (e.g. public key not found in key ring, or message is bad)
    @Test(expected = Exception::class)
    fun testEncryptUnencryptedMessageFailNoPublicKey(){
    }

    @Test(expected = Exception::class)
    fun testEncryptUnencryptedMessageFailBadMessage(){
        // No Bytes in message
    }

    @Test(expected = Exception::class)
    fun testDecryptEncryptedMessageFailBadMessage(){
    }
}