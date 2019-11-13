package com.cs220.ssmessaging.clientBackendUnitTests

import com.cs220.ssmessaging.clientBackend.ImageMessage
import com.cs220.ssmessaging.clientBackend.User
import org.junit.Assert.*
import org.junit.Test

class ImageMessageUnitTests{
    private var user1Id = "Joker"
    private var user2Id = "Ronald"
    private var imagemessage = ImageMessage(ByteArray(240), "123213", user1Id, user2Id, 12)

    // Note that this constructor test also tests the getters (No setters since all properties are val)
    @Test
    fun testConstructor(){
        // Test valid message (from private immagemessage)
        //Fix: Change from assertEquals to assertArrayEquals
        assertArrayEquals(ByteArray(240), imagemessage.message)
        assertEquals("123213", imagemessage.conversationId)
        assertEquals(user1Id, imagemessage.senderId)
        assertEquals(12, imagemessage.timestamp)
    }

    @Test
    fun testConstructorFailCases(){
        // Test bad messages (No bytes)
        var badMessage = ImageMessage(ByteArray(0), "123213", user1Id, user2Id, 12)
        //Fix: Change from assertEquals to assertArrayEquals
        assertArrayEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        // changed to assert User()
        assertEquals("", badMessage.senderId)
        assertEquals(0, badMessage.timestamp)

        // Test bad convo id (No id)
        badMessage = ImageMessage(ByteArray(25), "", user1Id, user2Id, 12)
        //Fix: Change from assertEquals to assertArrayEquals
        assertArrayEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        // changed to assert User()
        assertEquals("", badMessage.senderId)
        assertEquals(0, badMessage.timestamp)

        // Test bad user (No user)
        badMessage = ImageMessage(ByteArray(25), "525", "", user2Id, 12)
        //Fix: Change from assertEquals to assertArrayEquals
        assertArrayEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        // changed to assert User()
        assertEquals("", badMessage.senderId)
        assertEquals(0, badMessage.timestamp)

        // Test bad timestamp
        badMessage = ImageMessage(ByteArray(25), "525", user1Id, user2Id, -1)
        //Fix: Change from assertEquals to assertArrayEquals
        assertArrayEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        // changed to assert User()
        assertEquals("", badMessage.senderId)
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
        assertFalse(ImageMessage.isValidMessage(ImageMessage(ByteArray(0), "", "", "", -1)))
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
        // FIX: We changed the behavior and empty path is not allowable
        assertFalse(ImageMessage.isValidPathToImage(""))
        assertTrue(ImageMessage.isValidPathToImage("."))
        assertFalse(ImageMessage.isValidPathToImage("Wrong!"))
        assertFalse(ImageMessage.isValidPathToImage("<>!@"))
    }

}