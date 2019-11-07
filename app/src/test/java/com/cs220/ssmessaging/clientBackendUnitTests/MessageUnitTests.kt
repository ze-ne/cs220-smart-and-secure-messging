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

    @Test
    fun testGetterByteArray(){
        assertEquals(ByteArray(240), encryptedmessage.message)
    }

    fun testGetterConversationID(){
        assertEquals("123213", encryptedmessage.conversationId)
    }

    fun testGetterMessageType(){
        assertEquals("normal", encryptedmessage.messageType)
    }

    fun testGetterSender(){
        assertEquals(user1, encryptedmessage.sender)
    }

    fun testGetterRecipient(){
        assertEquals(user2, encryptedmessage.recipient)
    }

    fun testGetterTimeStamp(){
        assertEquals(12, encryptedmessage.timestamp)
    }
}

class TextMessageUnitTests{
    private var user1 = User("Joker", "Bad", "Man")
    private var user2 = User("Ronald", "McDonald", "Clown")
    private var textmessage = TextMessage("getyourhotdogs", "normal", user1, user2, 12)

    @Test
    fun testGetterMessage(){
        assertEquals("getyourhotdogs", textmessage.message)
    }

    @Test
    fun testGetterConversationId(){
        assertEquals("normal", textmessage.conversationId)
    }

    @Test
    fun testGetterSender(){
        assertEquals(user1, textmessage.sender)
    }

    @Test
    fun testGetterRecipient(){
        assertEquals(user2, textmessage.recipient)
    }

    @Test
    fun testGetterTimeStamp(){
        assertEquals(12, textmessage.timestamp)
    }
}

class ImageMessageUnitTests{
    private var user1 = User("Joker", "Bad", "Man")
    private var user2 = User("Ronald", "McDonald", "Clown")
    private var user3 = User("Big", "Bird", "Sesame")
    private var imagemessage = ImageMessage(ByteArray(240), "123213", user1, user2, 12)

    @Test
    fun testGetterByteArray(){
        assertEquals(ByteArray(240), imagemessage.message)
    }

    @Test
    fun testGetterConversationId(){
        assertEquals("123213", imagemessage.conversationId)
    }

    @Test
    fun testGetterSender(){
        assertEquals(user1, imagemessage.sender)
    }

    @Test
    fun testGetterRecipient(){
        assertEquals(user2, imagemessage.recipient)
    }

    @Test
    fun testGetterTimeStamp(){
        assertEquals(12, imagemessage.timestamp)
    }

}