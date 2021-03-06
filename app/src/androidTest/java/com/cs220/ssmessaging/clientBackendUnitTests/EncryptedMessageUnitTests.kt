package com.cs220.ssmessaging.clientBackendUnitTests

import com.cs220.ssmessaging.clientBackend.EncryptedMessage
import com.cs220.ssmessaging.clientBackend.ImageMessage
import com.cs220.ssmessaging.clientBackend.User
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Assert
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class EncryptedMessageUnitTests{
    private var user1Id = "Joker"
    private var user2Id = "Ronald"
    // Changed the byte array to something substantial (Not all zeros). Also message type was wrong.
    // FIX: Added example aesKey
    private var encryptedmessage = EncryptedMessage(byteArrayOf(1,3,4), "123213",
        "image", user1Id, user2Id, 12, byteArrayOf(1,2,3), byteArrayOf(1,2,4))

    // Note that this constructor test also tests the getters (No setters since all properties are val)
    @Test
    fun testConstructor(){
        // Test valid message (from private immagemessage)
        //Fix: Change from assertEquals to assertArrayEquals
        Assert.assertArrayEquals(byteArrayOf(1,3,4), encryptedmessage.message)
        Assert.assertEquals("123213", encryptedmessage.conversationId)
        // FIX: Changed expected to image from normal
        Assert.assertEquals("image", encryptedmessage.messageType)
        Assert.assertEquals(user1Id, encryptedmessage.senderId)
        Assert.assertEquals(user2Id, encryptedmessage.recipientId)
        Assert.assertEquals(12, encryptedmessage.timestamp)
        // Fix: need to assert equality of additional aesKey array
        assertArrayEquals(byteArrayOf(1,2,3), encryptedmessage.encryptedSenderAESKey)
        assertArrayEquals(byteArrayOf(1,2,4), encryptedmessage.encryptedRecipientAESKey)
    }

    // FIX: Added new assertions and test cases to check for the new AES key bytes
    @Test
    fun testConstructorFailCases(){
        // Test bad messages (No bytes)
        var badMessage = EncryptedMessage(ByteArray(0), "123213", "image", user1Id, user2Id, 12, byteArrayOf(1,2,3), byteArrayOf(1,2,4))
        //Fix: Change from assertEquals to assertArrayEquals
        Assert.assertArrayEquals(ByteArray(0), badMessage.message)
        Assert.assertEquals("", badMessage.conversationId)
        //Fix: Change the assertion comparisons to User()
        Assert.assertEquals("", badMessage.senderId)
        Assert.assertEquals("", badMessage.messageType)
        Assert.assertEquals(0, badMessage.timestamp)
        Assert.assertArrayEquals(ByteArray(0), badMessage.encryptedSenderAESKey)
        Assert.assertArrayEquals(ByteArray(0), badMessage.encryptedRecipientAESKey)

        // Test bad convo id (No id)
        badMessage = EncryptedMessage(ByteArray(25), "", "image", user1Id, user2Id, 12, byteArrayOf(1,2,3), byteArrayOf(1,2,4))
        //Fix: Change from assertEquals to assertArrayEquals
        Assert.assertArrayEquals(ByteArray(0), badMessage.message)
        Assert.assertEquals("", badMessage.conversationId)
        //Fix: Change the assertion comparisons to User()
        Assert.assertEquals("", badMessage.senderId)
        Assert.assertEquals("", badMessage.messageType)
        Assert.assertEquals(0, badMessage.timestamp)
        Assert.assertArrayEquals(ByteArray(0), badMessage.encryptedSenderAESKey)
        Assert.assertArrayEquals(ByteArray(0), badMessage.encryptedRecipientAESKey)

        // Test bad messageType
        badMessage = EncryptedMessage(ByteArray(25), "", "54", user1Id, user2Id, 12, byteArrayOf(1,2,3), byteArrayOf(1,2,4))
        //Fix: Change from assertEquals to assertArrayEquals
        Assert.assertArrayEquals(ByteArray(0), badMessage.message)
        Assert.assertEquals("", badMessage.conversationId)
        //Fix: Change the assertion comparisons to User()
        Assert.assertEquals("", badMessage.senderId)
        Assert.assertEquals("", badMessage.messageType)
        Assert.assertEquals(0, badMessage.timestamp)
        Assert.assertArrayEquals(ByteArray(0), badMessage.encryptedSenderAESKey)
        Assert.assertArrayEquals(ByteArray(0), badMessage.encryptedRecipientAESKey)

        // Test bad user (No user)
        badMessage = EncryptedMessage(ByteArray(25), "525", "image", "", user2Id, 12, byteArrayOf(1,2,3), byteArrayOf(1,2,4))
        //Fix: Change from assertEquals to assertArrayEquals
        Assert.assertArrayEquals(ByteArray(0), badMessage.message)
        Assert.assertEquals("", badMessage.conversationId)
        //Fix: Change the assertion comparisons to User()
        Assert.assertEquals("", badMessage.senderId)
        Assert.assertEquals("", badMessage.messageType)
        Assert.assertEquals(0, badMessage.timestamp)
        Assert.assertArrayEquals(ByteArray(0), badMessage.encryptedSenderAESKey)
        Assert.assertArrayEquals(ByteArray(0), badMessage.encryptedRecipientAESKey)

        // Test bad timestamp
        badMessage = EncryptedMessage(ByteArray(25), "525", "image", user1Id, user2Id, -1,  byteArrayOf(1,2,3), byteArrayOf(1,2,4))
        //Fix: Change from assertEquals to assertArrayEquals
        Assert.assertArrayEquals(ByteArray(0), badMessage.message)
        Assert.assertEquals("", badMessage.conversationId)
        //Fix: Change the assertion comparisons to User()
        Assert.assertEquals("", badMessage.senderId)
        Assert.assertEquals("", badMessage.messageType)
        Assert.assertEquals(0, badMessage.timestamp)
        Assert.assertArrayEquals(ByteArray(0), badMessage.encryptedSenderAESKey)
        Assert.assertArrayEquals(ByteArray(0), badMessage.encryptedRecipientAESKey)

        // Test bad AES key
        badMessage = EncryptedMessage(ByteArray(25), "525", "image", user1Id, user2Id, 123,  byteArrayOf(), byteArrayOf(1,2,4))
        //Fix: Change from assertEquals to assertArrayEquals
        Assert.assertArrayEquals(ByteArray(0), badMessage.message)
        Assert.assertEquals("", badMessage.conversationId)
        //Fix: Change the assertion comparisons to User()
        Assert.assertEquals("", badMessage.senderId)
        Assert.assertEquals("", badMessage.messageType)
        Assert.assertEquals(0, badMessage.timestamp)
        Assert.assertArrayEquals(ByteArray(0), badMessage.encryptedSenderAESKey)
        Assert.assertArrayEquals(ByteArray(0), badMessage.encryptedRecipientAESKey)
    }

    // FIX: The following tests test the added static helper functions for property checking
    @Test
    fun testIsValidMessage(){
        // Valid message
        Assert.assertTrue(EncryptedMessage.isValidMessage(encryptedmessage))

        // Invalid message - can only test the "null" message instance (i.e. entries are "" or empty)
        // since the constructors use the isValidMessage() static function
        Assert.assertFalse(
            EncryptedMessage.isValidMessage(
                EncryptedMessage(
                    ByteArray(0),
                    "",
                    "",
                    "",
                    "",
                    -1,
                    ByteArray(0),
                    ByteArray(0)
                )
            )
        )
    }

    @Test
    fun testIsValidMessageBody(){
        Assert.assertTrue(EncryptedMessage.isValidMessageBody(ByteArray(3)))
        Assert.assertTrue(EncryptedMessage.isValidMessageBody(byteArrayOf(1, 2, 3)))
        Assert.assertFalse(EncryptedMessage.isValidMessageBody(byteArrayOf()))
    }

    @Test
    fun testIsValidMessageType(){
        Assert.assertTrue(EncryptedMessage.isValidMessageType("image"))
        Assert.assertTrue(EncryptedMessage.isValidMessageType("text"))
        Assert.assertFalse(EncryptedMessage.isValidMessageType(""))
        Assert.assertFalse(EncryptedMessage.isValidMessageType("545"))
        Assert.assertFalse(EncryptedMessage.isValidMessageType("images"))
    }

    @Test
    fun testMEquals(){
        var msg1 = EncryptedMessage(byteArrayOf(1,2,3), "123213", "image", user1Id, user2Id, 12, byteArrayOf(1,2,3), byteArrayOf(1,2,4))
        var msg2 = EncryptedMessage(byteArrayOf(1,2,3), "123213","image", user1Id, user2Id, 13, byteArrayOf(1,2,3), byteArrayOf(1,2,4))
        var msg3 = EncryptedMessage(byteArrayOf(1,2), "1233", "image", user1Id, user2Id, 12, byteArrayOf(1,2,3), byteArrayOf(1,2,4))
        var msg4 = EncryptedMessage(byteArrayOf(1,2,3), "123213", "text", user1Id, user2Id, 12, byteArrayOf(1,2,3), byteArrayOf(1,2,4))
        var msg5 = EncryptedMessage(byteArrayOf(1,2,3), "123213", "image", user1Id, user2Id, 12, byteArrayOf(1,2), byteArrayOf(1,2,4))

        assertTrue(msg1.mEquals(msg1))
        assertFalse(msg1.mEquals(msg2))
        assertFalse(msg1.mEquals(msg3))
        assertFalse(msg1.mEquals(msg4))
        assertFalse(msg1.mEquals(msg5))
    }
}