package com.cs220.ssmessaging.clientBackendUnitTests

import com.cs220.ssmessaging.clientBackend.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.lang.ClassCastException

import org.junit.Before
import org.w3c.dom.Text
import java.lang.Exception
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

@RunWith(MockitoJUnitRunner::class)
class CipherExtensionUnitTests{

    // FIX: add BouncyCastle for key provider
    init{
        // Need to add bouncy castle provider
        Security.addProvider(BouncyCastleProvider())
    }

    // FIX: Change provider to BouncyCastle instead of SunJCE (Android uses BouncyCastle)
    private val keyPairGen : KeyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC")
    private val keyPair1 = keyPairGen.generateKeyPair()
    private val keyPair2 = keyPairGen.generateKeyPair()
    private var testPrivateKey : PrivateKey = keyPair1.private
    private var testPrivateKey2 : PrivateKey = keyPair2.private
    private var testPublicKey : PublicKey = keyPair1.public
    private var testPublicKey2 : PublicKey = keyPair2.public
    private var sendUser : User = User("person1", "test", "User")
    private var receiveUser : User = User("person2", "test", "User2")

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

        // FIX: As described in Device unit tests, we need to use assertArrayEquals rather than assertEquals
        // FIX: Wrong expected value entered into the third assertion
        assertArrayEquals(testCipherExtension.publicKeyRing["person1"]?.encoded, testPublicKey.encoded)
        assertArrayEquals(testCipherExtension.publicKeyRing["person2"]?.encoded, testPublicKey2.encoded)
        assertArrayEquals(testCipherExtension.privateKey.encoded, testPrivateKey.encoded)
    }

    @Test
    fun testSetPublicKeyRings(){
        // Test setting the public key rings for 0 size, 1 size, and multiple size
        var testCipherExtension =
            CipherExtension(testPrivateKey, mutableMapOf())

        testCipherExtension.publicKeyRing = mutableMapOf("person1" to testPublicKey, "person2" to testPublicKey2)
        assertEquals(mutableMapOf("person1" to testPublicKey, "person2" to testPublicKey2), testCipherExtension.publicKeyRing)

        testCipherExtension.publicKeyRing = mutableMapOf("person1" to testPublicKey)
        assertEquals(mutableMapOf("person1" to testPublicKey), testCipherExtension.publicKeyRing)

        testCipherExtension.publicKeyRing = mutableMapOf()
        assertEquals(mutableMapOf<String, PublicKey>(), testCipherExtension.publicKeyRing)
    }

    @Test
    fun testSetPrivateKey(){
        // Test private key override
        var testCipherExtension =
            CipherExtension(testPrivateKey, mutableMapOf())
        testCipherExtension.privateKey = testPrivateKey2
        assertEquals(testPrivateKey2, testCipherExtension.privateKey)
    }

    @Test
    fun testAddPublicKeyToKeyRing(){
        // Test adding a key to the key ring with no keys in ring, one key in ring, and multiple keys in ring, and same key but different person
        // Also test key overriding.
        var testCipherExtension =
            CipherExtension(testPrivateKey, mutableMapOf())

        testCipherExtension.addKeyToPublicKeyRing("person1",testPublicKey)
        assertEquals(testPublicKey, testCipherExtension.publicKeyRing["person1"])

        testCipherExtension.addKeyToPublicKeyRing("person2", testPublicKey)
        assertEquals(testPublicKey, testCipherExtension.publicKeyRing["person2"])

        // FIX: Wrong expected value. It should be testPublicKey2 instead of testPublicKey
        testCipherExtension.addKeyToPublicKeyRing("person3", testPublicKey2)
        assertEquals(testPublicKey2, testCipherExtension.publicKeyRing["person3"])

        testCipherExtension.addKeyToPublicKeyRing("person1", testPublicKey2)
        assertEquals(testPublicKey2, testCipherExtension.publicKeyRing["person1"])
    }

    @Test
    fun testEncryptAndDecryptMessageText(){
        // Tests the case when only one message encrypted and multiple messages encrypted.
        var testCipherExtension =
            CipherExtension(testPrivateKey2, mutableMapOf("person1" to testPublicKey, "person2" to testPublicKey2))

        // Create new unencrypted text messages
        var textMessage : TextMessage = TextMessage("ABC", "testId", sendUser, receiveUser, 0)
        var textMessage2 : TextMessage = TextMessage("sad46546 ----", "testId2", sendUser, receiveUser, 123)

        var encryptedMessage = testCipherExtension.encryptUnencryptedMessage(textMessage)
        var encryptedMessage2 = testCipherExtension.encryptUnencryptedMessage(textMessage2)

        // Decrypt and verify
        var decryptedMessage = testCipherExtension.decryptEncryptedMessage(encryptedMessage)
        var decryptedMessage2 = testCipherExtension.decryptEncryptedMessage(encryptedMessage2)

        try{
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
        catch(e : ClassCastException){
            fail("decrypt returned the wrong type of message")
        }
    }

    @Test
    fun testEncryptAndDecryptMessageImage(){
        // Tests the case when only one message encrypted and multiple messages encrypted.
        var testCipherExtension =
            CipherExtension(testPrivateKey2, mutableMapOf("person1" to testPublicKey, "person2" to testPublicKey2))

        // Create new unencrypted text messages
        var imgMessage : ImageMessage = ImageMessage(ByteArray(10), "testId", sendUser, receiveUser, 0)
        var imgMessage2 : ImageMessage = ImageMessage(ByteArray(10), "testId2", sendUser, receiveUser, 123)

        var encryptedMessage = testCipherExtension.encryptUnencryptedMessage(imgMessage)
        var encryptedMessage2 = testCipherExtension.encryptUnencryptedMessage(imgMessage2)

        // Decrypt and verify
        var decryptedMessage = testCipherExtension.decryptEncryptedMessage(encryptedMessage)
        var decryptedMessage2 = testCipherExtension.decryptEncryptedMessage(encryptedMessage2)

        try{
            var message : ByteArray = (decryptedMessage2 as ImageMessage).message
            var message2 : ByteArray = (decryptedMessage as ImageMessage).message

            assertEquals(imgMessage.message, message)
            assertEquals("testId", encryptedMessage.conversationId)
            assertEquals("person1", encryptedMessage.sender.userId)
            assertEquals("person2", encryptedMessage.sender.userId)
            assertEquals(0, encryptedMessage.timestamp)

            assertEquals(imgMessage2.message, message2)
            assertEquals("testId2", encryptedMessage2.conversationId)
            assertEquals("person1", encryptedMessage2.sender.userId)
            assertEquals("person2", encryptedMessage2.sender.userId)
            assertEquals(123, encryptedMessage.timestamp)
        }
        catch(e : ClassCastException){
            fail("decrypt returned the wrong type of message")
        }

    }

    // The next tests test the case where encryption/decryption can fail
    // (e.g. public key not found in key ring, or message is bad)
    @Test(expected = Exception::class)
    fun testEncryptUnencryptedMessageFailNoPublicKey(){
        // Tests the case when only one message encrypted and multiple messages encrypted.
        var testCipherExtension =
            CipherExtension(testPrivateKey2, mutableMapOf())

        var imgMessage : ImageMessage = ImageMessage(ByteArray(10), "testId", sendUser, receiveUser, 0)
        testCipherExtension.encryptUnencryptedMessage(imgMessage)

        var textMessage : TextMessage = TextMessage("ABC", "testId", sendUser, receiveUser, 0)
        testCipherExtension.encryptUnencryptedMessage(textMessage)
    }

    @Test(expected = Exception::class)
    fun testEncryptImageMessageFailBadMessage(){
        // No Bytes or string in message
        var testCipherExtension =
            CipherExtension(testPrivateKey2, mutableMapOf("person1" to testPublicKey, "person2" to testPublicKey2))

        var imgMessage : ImageMessage = ImageMessage(ByteArray(0), "testId", sendUser, receiveUser, 0)
        testCipherExtension.encryptUnencryptedMessage(imgMessage)
    }

    @Test(expected = Exception::class)
    fun testEncryptImageMessageFailBadConversationId(){
        // No conversation Id
        var testCipherExtension =
            CipherExtension(testPrivateKey2, mutableMapOf("person1" to testPublicKey, "person2" to testPublicKey2))

        var imgMessage : ImageMessage = ImageMessage(ByteArray(61), "", sendUser, receiveUser, 0)
        testCipherExtension.encryptUnencryptedMessage(imgMessage)
    }

    @Test(expected = Exception::class)
    fun testEncryptImageMessageFailBadUser(){
        // At least one user is null
        var testCipherExtension =
            CipherExtension(testPrivateKey2, mutableMapOf("person1" to testPublicKey, "person2" to testPublicKey2))

        var imgMessage : ImageMessage = ImageMessage(ByteArray(61), "sad", User(), receiveUser, 0)
        testCipherExtension.encryptUnencryptedMessage(imgMessage)
    }

    @Test(expected = Exception::class)
    fun testEncryptImageMessageFailBadTimestamp(){
        // time is < 0
        var testCipherExtension =
            CipherExtension(testPrivateKey2, mutableMapOf("person1" to testPublicKey, "person2" to testPublicKey2))

        var imgMessage : ImageMessage = ImageMessage(ByteArray(61), "sad", sendUser, receiveUser, -1)
        testCipherExtension.encryptUnencryptedMessage(imgMessage)
    }

    @Test(expected = Exception::class)
    fun testEncryptTextMessageFailBadMessage(){
        // No Bytes or string in message
        var testCipherExtension =
            CipherExtension(testPrivateKey2, mutableMapOf("person1" to testPublicKey, "person2" to testPublicKey2))
        var textMessage : TextMessage = TextMessage("", "testId", sendUser, receiveUser, 0)
        testCipherExtension.encryptUnencryptedMessage(textMessage)
    }

    @Test(expected = Exception::class)
    fun testEncryptTextMessageFailBadUser(){
        // Bad user in message
        var testCipherExtension =
            CipherExtension(testPrivateKey2, mutableMapOf("person1" to testPublicKey, "person2" to testPublicKey2))
        var textMessage : TextMessage = TextMessage("dsadsa", "testId", User(), receiveUser, 0)
        testCipherExtension.encryptUnencryptedMessage(textMessage)
    }

    @Test(expected = Exception::class)
    fun testEncryptTextMessageFailBadConversationId(){
        // Bad user in message
        var testCipherExtension =
            CipherExtension(testPrivateKey2, mutableMapOf("person1" to testPublicKey, "person2" to testPublicKey2))
        var textMessage : TextMessage = TextMessage("dsadsa", "", User(), receiveUser, 0)
        testCipherExtension.encryptUnencryptedMessage(textMessage)
    }

    @Test(expected = Exception::class)
    fun testEncryptTextMessageFailBadTimestamp(){
        // bad time
        var testCipherExtension =
            CipherExtension(testPrivateKey2, mutableMapOf("person1" to testPublicKey, "person2" to testPublicKey2))
        var textMessage : TextMessage = TextMessage("sda", "testId", sendUser, receiveUser, -1)
        testCipherExtension.encryptUnencryptedMessage(textMessage)
    }

    @Test(expected = Exception::class)
    fun testDecryptEncryptedImageMessageFailBadMessage(){
        // Unlike encryption, decryption should only fail when there is a bad message or bad type
        var testCipherExtension =
            CipherExtension(testPrivateKey2, mutableMapOf("person1" to testPublicKey, "person2" to testPublicKey2))
        var encryptedMessage : EncryptedMessage = EncryptedMessage(ByteArray(0), "testId", "image", sendUser, receiveUser, -1)
        testCipherExtension.decryptEncryptedMessage(encryptedMessage)
    }

    @Test(expected = Exception::class)
    fun testDecryptEncryptedTextMessageFailBadType(){
        // Unlike encryption, decryption should only fail when there is a bad message or bad type
        var testCipherExtension =
            CipherExtension(testPrivateKey2, mutableMapOf("person1" to testPublicKey, "person2" to testPublicKey2))
        var encryptedMessage : EncryptedMessage = EncryptedMessage(ByteArray(566), "testId", "", sendUser, receiveUser, -1)
        testCipherExtension.decryptEncryptedMessage(encryptedMessage)
    }
}