package com.cs220.ssmessaging.clientBackendUnitTests

import com.cs220.ssmessaging.clientBackend.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class EncryptedMessageUnitTests{
    private var user1 = User("Joker", "Bad", "Man")
    private var user2 = User("Ronald", "McDonald", "Clown")
    private var encryptedmessage = EncryptedMessage(ByteArray(240), "123213", "normal", user1, user2, 12)

    // Note that this constructor test also tests the getters (No setters since all properties are val)
    @Test
    fun testConstructor(){
        // Test valid message (from private immagemessage)
        assertEquals(ByteArray(240), encryptedmessage.message)
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
        assertEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals("", badMessage.messageType)
        assertEquals(0, badMessage.timestamp)

        // Test bad convo id (No id)
        badMessage = EncryptedMessage(ByteArray(25), "", "image", user1, user2, 12)
        assertEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals("", badMessage.messageType)
        assertEquals(0, badMessage.timestamp)

        // Test bad messageType
        badMessage = EncryptedMessage(ByteArray(25), "", "54", user1, user2, 12)
        assertEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals("", badMessage.messageType)
        assertEquals(0, badMessage.timestamp)

        // Test bad user (No user)
        badMessage = EncryptedMessage(ByteArray(25), "525", "image", User(), user2, 12)
        assertEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals("", badMessage.messageType)
        assertEquals(0, badMessage.timestamp)

        // Test bad timestamp
        badMessage = EncryptedMessage(ByteArray(25), "525", "image", user1, user2, -1)
        assertEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals("", badMessage.messageType)
        assertEquals(0, badMessage.timestamp)
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
}

class ImageMessageUnitTests{
    private var user1 = User("Joker", "Bad", "Man")
    private var user2 = User("Ronald", "McDonald", "Clown")
    private var imagemessage = ImageMessage(ByteArray(240), "123213", user1, user2, 12)

    // Note that this constructor test also tests the getters (No setters since all properties are val)
    @Test
    fun testConstructor(){
        // Test valid message (from private immagemessage)
        assertEquals(ByteArray(240), imagemessage.message)
        assertEquals("123213", imagemessage.conversationId)
        assertEquals(user1, imagemessage.sender)
        assertEquals(12, imagemessage.timestamp)
    }

    @Test
    fun testConstructorFailCases(){
        // Test bad messages (No bytes)
        var badMessage = ImageMessage(ByteArray(0), "123213", user1, user2, 12)
        assertEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals(0, badMessage.timestamp)

        // Test bad convo id (No id)
        badMessage = ImageMessage(ByteArray(25), "", user1, user2, 12)
        assertEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals(0, badMessage.timestamp)

        // Test bad user (No user)
        badMessage = ImageMessage(ByteArray(25), "525", User(), user2, 12)
        assertEquals(ByteArray(0), badMessage.message)
        assertEquals("", badMessage.conversationId)
        assertEquals(User(), badMessage.sender)
        assertEquals(0, badMessage.timestamp)

        // Test bad timestamp
        badMessage = ImageMessage(ByteArray(25), "525", user1, user2, -1)
        assertEquals(ByteArray(0), badMessage.message)
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

}