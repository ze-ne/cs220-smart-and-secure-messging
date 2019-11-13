package com.cs220.ssmessaging.clientBackendUnitTests

import android.media.Image
import com.cs220.ssmessaging.clientBackend.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith

class MessageUnitTests{
    // FIX: This class was added to test the static helpers for property checking
    @Test
    fun testStaticIsValidTimeStamp(){
        assertTrue(Message.isValidTimestamp(0))
        assertTrue(Message.isValidTimestamp(1))
        assertTrue(Message.isValidTimestamp(65561521))
        assertFalse(Message.isValidTimestamp(-1))
        assertFalse(Message.isValidTimestamp(-500))
    }
}

class EncryptedMessageUnitTests{
    private var user1 = User("Joker", "Bad", "Man")
    private var user2 = User("Ronald", "McDonald", "Clown")
    private var encryptedmessage = EncryptedMessage(ByteArray(240), "123213", "normal", user1, user2, 12)

    // Note that this constructor test also tests the getters (No setters since all properties are val)
    @Test
    fun testConstructor(){
        // Test valid message (from private immagemessage)
        //Fix: Change from assertEquals to assertArrayEquals
        assertArrayEquals(ByteArray(240), encryptedmessage.message)
        assertEquals("123213", encryptedmessage.conversationId)
        assertEquals("normal", encryptedmessage.messageType)
        assertEquals(user1, encryptedmessage.sender)
        assertEquals(user2, encryptedmessage.recipient)
        assertEquals(12, encryptedmessage.timestamp)
    }

    @Test
    fun testConstructorFailCases(){
        // Test bad messages (No bytes)
        var badMessage = EncryptedMessage(ByteArray(0), "123213", "image", user1, user2, 12)
        //Fix: Change from assertEquals to assertArrayEquals
        assertArrayEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals("", badMessage.messageType)
        assertEquals(0, badMessage.timestamp)

        // Test bad convo id (No id)
        badMessage = EncryptedMessage(ByteArray(25), "", "image", user1, user2, 12)
        //Fix: Change from assertEquals to assertArrayEquals
        assertArrayEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals("", badMessage.messageType)
        assertEquals(0, badMessage.timestamp)

        // Test bad messageType
        badMessage = EncryptedMessage(ByteArray(25), "", "54", user1, user2, 12)
        //Fix: Change from assertEquals to assertArrayEquals
        assertArrayEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals("", badMessage.messageType)
        assertEquals(0, badMessage.timestamp)

        // Test bad user (No user)
        badMessage = EncryptedMessage(ByteArray(25), "525", "image", User(), user2, 12)
        //Fix: Change from assertEquals to assertArrayEquals
        assertArrayEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals("", badMessage.messageType)
        assertEquals(0, badMessage.timestamp)

        // Test bad timestamp
        badMessage = EncryptedMessage(ByteArray(25), "525", "image", user1, user2, -1)
        //Fix: Change from assertEquals to assertArrayEquals
        assertArrayEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals("", badMessage.messageType)
        assertEquals(0, badMessage.timestamp)
    }

    // FIX: The following tests test the added static helper functions for property checking
    @Test
    fun testIsValidMessage(){
        // Valid message
        assertTrue(EncryptedMessage.isValidMessage(encryptedmessage))

        // Invalid message - can only test the "null" message instance (i.e. entries are "" or empty)
        // since the constructors use the isValidMessage() static function
        assertFalse(EncryptedMessage.isValidMessage(EncryptedMessage(ByteArray(0), "", "", User(), User(), -1)))
    }

    @Test
    fun testIsValidMessageBody(){
        assertTrue(EncryptedMessage.isValidMessageBody(ByteArray(3)))
        assertTrue(EncryptedMessage.isValidMessageBody(byteArrayOf(1,2,3)))
        assertFalse(EncryptedMessage.isValidMessageBody(byteArrayOf()))
    }

    @Test
    fun testIsValidMessageType(){
        assertTrue(EncryptedMessage.isValidMessageType("image"))
        assertTrue(EncryptedMessage.isValidMessageType("text"))
        assertFalse(EncryptedMessage.isValidMessageType(""))
        assertFalse(EncryptedMessage.isValidMessageType("545"))
        assertFalse(EncryptedMessage.isValidMessageType("images"))
    }
}

class TextMessageUnitTests{
    private var user1 = User("Joker", "Bad", "Man")
    private var user2 = User("Ronald", "McDonald", "Clown")
    private var textmessage = TextMessage("getyourhotdogs", "normal", user1, user2, 12)

    // Note that this constructor test also tests the getters (No setters since all properties are val)
    @Test
    fun testConstructor(){
        // Test valid message (from private immagemessage)
        assertEquals("getyourhotdogs", textmessage.message)
        assertEquals("normal", textmessage.conversationId)
        assertEquals(user1, textmessage.sender)
        assertEquals(user2, textmessage.recipient)
        assertEquals(12, textmessage.timestamp)
    }

    @Test
    fun testConstructorFailCases(){
        // Test bad messages (No bytes)
        var badMessage = TextMessage("", "123213", user1, user2, 12)
        assertEquals("", badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals(0, badMessage.timestamp)

        // Test bad convo id (No id)
        badMessage = TextMessage("hhhh", "", user1, user2, 12)
        assertEquals("", badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals(0, badMessage.timestamp)

        // Test bad user (No user)
        badMessage = TextMessage("hhh", "525", User(), user2, 12)
        assertEquals("", badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals(0, badMessage.timestamp)

        // Test bad timestamp
        badMessage = TextMessage("hhh", "525", user1, user2, -1)
        assertEquals("", badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals(0, badMessage.timestamp)
    }

    // FIX: The following tests test the added static helper functions for property checking
    @Test
    fun testIsValidMessage(){
        // Valid message
        assertTrue(TextMessage.isValidMessage(textmessage))

        // Invalid message - can only test the "null" message instance (i.e. entries are "" or empty)
        // since the constructors use the isValidMessage() static function
        assertFalse(TextMessage.isValidMessage(TextMessage("", "", User(), User(), -1)))
    }

    @Test
    fun testIsValidMessageBody(){
        assertTrue(TextMessage.isValidMessageBody("abc -!@#123"))
        assertTrue(TextMessage.isValidMessageBody("x"))
        assertFalse(TextMessage.isValidMessageBody(""))
    }
}

class ImageMessageUnitTests{
    private var user1 = User("Joker", "Bad", "Man")
    private var user2 = User("Ronald", "McDonald", "Clown")
    private var imagemessage = ImageMessage(ByteArray(240), "123213", user1, user2, 12)

    // Note that this constructor test also tests the getters (No setters since all properties are val)
    @Test
    fun testConstructor(){
        // Test valid message (from private immagemessage)
        //Fix: Change from assertEquals to assertArrayEquals
        assertArrayEquals(ByteArray(240), imagemessage.message)
        assertEquals("123213", imagemessage.conversationId)
        assertEquals(user1, imagemessage.sender)
        assertEquals(12, imagemessage.timestamp)
    }

    @Test
    fun testConstructorFailCases(){
        // Test bad messages (No bytes)
        var badMessage = ImageMessage(ByteArray(0), "123213", user1, user2, 12)
        //Fix: Change from assertEquals to assertArrayEquals
        assertArrayEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals(0, badMessage.timestamp)

        // Test bad convo id (No id)
        badMessage = ImageMessage(ByteArray(25), "", user1, user2, 12)
        //Fix: Change from assertEquals to assertArrayEquals
        assertArrayEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals(0, badMessage.timestamp)

        // Test bad user (No user)
        badMessage = ImageMessage(ByteArray(25), "525", User(), user2, 12)
        //Fix: Change from assertEquals to assertArrayEquals
        assertArrayEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals(0, badMessage.timestamp)

        // Test bad timestamp
        badMessage = ImageMessage(ByteArray(25), "525", user1, user2, -1)
        //Fix: Change from assertEquals to assertArrayEquals
        assertArrayEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals(0, badMessage.timestamp)
    }

    @Test
    fun testPathToImageGetterAndSetter(){
        // Valid image paths
        assertEquals("", imagemessage.pathToImage)
        imagemessage.pathToImage = "./relative"
        assertEquals("./relative", imagemessage.pathToImage)

        imagemessage.pathToImage = "relative"
        assertEquals("relative", imagemessage.pathToImage)

        imagemessage.pathToImage = "."
        assertEquals(".", imagemessage.pathToImage)

        imagemessage.pathToImage = "../test"
        assertEquals("../test", imagemessage.pathToImage)

        imagemessage.pathToImage = "C://absolute"
        assertEquals("C://absolute", imagemessage.pathToImage)

        // Invalid path
        imagemessage.pathToImage = "C://absolute!!"
        assertEquals("C://absolute", imagemessage.pathToImage)

        imagemessage.pathToImage = "C://absolute!!+-*"
        assertEquals("C://absolute", imagemessage.pathToImage)

        imagemessage.pathToImage = ""
        assertEquals("C://absolute", imagemessage.pathToImage)
    }

    // FIX: The following tests test the added static helper functions for property checking
    @Test
    fun testIsValidMessage(){
        // Valid message
        assertTrue(ImageMessage.isValidMessage(imagemessage))

        // Invalid message - can only test the "null" message instance (i.e. entries are "" or empty)
        // since the constructors use the isValidMessage() static function
        assertFalse(ImageMessage.isValidMessage(ImageMessage(ByteArray(0), "", User(), User(), -1)))
    }

    @Test
    fun testIsValidMessageBody(){
        assertTrue(ImageMessage.isValidMessageBody(ByteArray(3)))
        assertTrue(ImageMessage.isValidMessageBody(byteArrayOf(1,2,3)))
        assertFalse(ImageMessage.isValidMessageBody(byteArrayOf()))
    }

    @Test
    fun testIsValidPathToImage(){
        assertTrue(ImageMessage.isValidPathToImage("C://absolute"))
        assertTrue(ImageMessage.isValidPathToImage("../test"))
        assertTrue(ImageMessage.isValidPathToImage("../test-tree_1"))
        assertTrue(ImageMessage.isValidPathToImage(""))
        assertTrue(ImageMessage.isValidPathToImage("."))
        assertFalse(ImageMessage.isValidPathToImage("Wrong!"))
        assertFalse(ImageMessage.isValidPathToImage("<>!@"))
    }

}