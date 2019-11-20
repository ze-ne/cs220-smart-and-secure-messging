package com.cs220.ssmessaging.clientBackendUnitTests

import com.cs220.ssmessaging.clientBackend.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import kotlin.coroutines.coroutineContext


class ConversationUnitTests{
    private var user1Id = "user1Id"
    private var user2Id = "user2Id"
    private var nullUser = ""
    // FIX: conversationId changed to a valid value also byteArray of image changed to valid value
    private var imgMessage : ImageMessage = ImageMessage(byteArrayOf(1,2), "user1-user2", user1Id, user2Id, 0)
    private var textMessage : TextMessage = TextMessage("troglobite", "user1-user2", user1Id, user2Id, 0)
    private var textMessageDifferentUsers : TextMessage = TextMessage("troglobitdse", "users-user2", nullUser, user2Id, 0)

    @Test
    fun testDefaultConstructor() {
        var conversation = Conversation()
        assertEquals("", conversation.convoId)
        // FIX: Changed to user1Id and user2Id
        assertEquals("", conversation.user1Id)
        assertEquals("", conversation.user2Id)
        assertEquals(0, conversation.lastTimeSynced)
        assertEquals(0, conversation.messages.size)
    }

    @Test
    fun testSecondaryConstructorNoMessages() {
        // Valid users
        var conversation = Conversation(user1Id, user2Id)
        assertEquals("user1Id-user2Id", conversation.convoId)
        // FIX: Changed to user1Id and user2Id
        assertEquals("user1Id", conversation.user1Id)
        assertEquals("user2Id", conversation.user2Id)
        assertEquals(0, conversation.lastTimeSynced)
        assertEquals(0, conversation.messages.size)

        // Invalid second user
        conversation = Conversation(user1Id, nullUser)
        assertEquals("", conversation.convoId)
        // FIX: Changed to user1Id and user2Id
        assertEquals("", conversation.user1Id)
        assertEquals("", conversation.user2Id)
        assertEquals(0, conversation.lastTimeSynced)
        assertEquals(0, conversation.messages.size)

        // Invalid first user
        conversation = Conversation(nullUser, user2Id)
        assertEquals("", conversation.convoId)
        // FIX: Changed to user1Id and user2Id
        assertEquals("", conversation.user1Id)
        assertEquals("", conversation.user2Id)
        assertEquals(0, conversation.lastTimeSynced)
        assertEquals(0, conversation.messages.size)
    }

    @Test
    fun testSecondaryConstructorWithMessages() {
        // Valid users and messages
        var conversation = Conversation(user1Id, user2Id, mutableListOf(imgMessage, textMessage))
        assertEquals("user1Id-user2Id", conversation.convoId)
        // FIX: Changed to user1Id and user2Id
        assertEquals("user1Id", conversation.user1Id)
        assertEquals("user2Id", conversation.user2Id)
        assertEquals(0, conversation.lastTimeSynced)
        assertEquals(2, conversation.messages.size)
        // FIX: changed to assert array equals rather than equals
        assertArrayEquals(imgMessage.message, (conversation.messages[0] as ImageMessage).message)
        // FIX: Changed the actual to messages[1] as TextMessage
        assertEquals(textMessage.message, (conversation.messages[1] as TextMessage).message)

        // Valid users and no message
        conversation = Conversation(user1Id, user2Id, mutableListOf())
        assertEquals("user1Id-user2Id", conversation.convoId)
        // FIX: Changed to user1Id and user2Id
        assertEquals("user1Id", conversation.user1Id)
        assertEquals("user2Id", conversation.user2Id)
        assertEquals(0, conversation.lastTimeSynced)
        assertEquals(0, conversation.messages.size)

        // Invalid second user
        conversation = Conversation(user1Id, nullUser, mutableListOf(imgMessage, textMessage))
        assertEquals("", conversation.convoId)
        // FIX: Changed to user1Id and user2Id
        assertEquals("", conversation.user1Id)
        assertEquals("", conversation.user2Id)
        assertEquals(0, conversation.lastTimeSynced)
        assertEquals(0, conversation.messages.size)

        // Invalid first user
        conversation = Conversation(nullUser, user1Id, mutableListOf(imgMessage, textMessage))
        assertEquals("", conversation.convoId)
        // FIX: Changed to user1Id and user2Id
        assertEquals("", conversation.user1Id)
        assertEquals("", conversation.user2Id)
        assertEquals(0, conversation.lastTimeSynced)
        assertEquals(0, conversation.messages.size)

        // Invalid message in messages (userid conflict)
        conversation = Conversation(nullUser, user2Id, mutableListOf(imgMessage, textMessageDifferentUsers))
        assertEquals("", conversation.convoId)
        // FIX: Changed to user1Id and user2Id
        assertEquals("", conversation.user1Id)
        assertEquals("", conversation.user2Id)
        assertEquals(0, conversation.lastTimeSynced)
        assertEquals(0, conversation.messages.size)
    }

    @Test
    fun testMessageListGetterAndSetter() {
        // Valid settings.
        var conversation = Conversation(user1Id, user2Id)
        assertEquals(0, conversation.messages.size)

        conversation.messages = mutableListOf(imgMessage)
        assertEquals(mutableListOf(imgMessage), conversation.messages)

        conversation.messages = mutableListOf(imgMessage, textMessage)
        assertEquals(mutableListOf(imgMessage, textMessage), conversation.messages)

        // FIX: Removed one case where you had messages of different users. This will never happen according to spec.
    }

    @Test
    fun testConversationIdGetter(){
        // Only two cases: where the conversation is invalid and where the conversation is valid
        var conversation = Conversation(user1Id, user2Id)
        assertEquals("user1Id-user2Id", conversation.convoId)

        conversation = Conversation(nullUser, user2Id)
        assertEquals("", conversation.convoId)
    }

    @Test
    fun testLastTimeSynchedGetterAndSetter(){
        var conversation = Conversation(user1Id, user2Id)
        assertEquals(0, conversation.lastTimeSynced)
        conversation.lastTimeSynced = 10
        assertEquals(10, conversation.lastTimeSynced)
        conversation.lastTimeSynced = 0
        assertEquals(0, conversation.lastTimeSynced)
        conversation.lastTimeSynced = 10339428
        assertEquals(10339428, conversation.lastTimeSynced)

        // Invalid sync
        conversation.lastTimeSynced = -5
        assertEquals(10339428, conversation.lastTimeSynced)
    }

    @Test
    fun testAddMessage() {
        var conversation = Conversation(user1Id, user2Id)

        // Valid adds
        assertTrue(conversation.addMessage(textMessage))
        assertEquals(mutableListOf(textMessage), conversation.messages)

        assertTrue(conversation.addMessage(imgMessage))
        assertEquals(mutableListOf(textMessage, imgMessage), conversation.messages)

        // if you add the same message, then don't update message list
        assertFalse(conversation.addMessage(imgMessage))
        assertEquals(mutableListOf(textMessage, imgMessage), conversation.messages)

        // if you add a bad message, then don't update message list
        assertFalse(conversation.addMessage(textMessageDifferentUsers))
        assertEquals(mutableListOf(textMessage, imgMessage), conversation.messages)

        // if you add a bad message, then don't update message list
        assertFalse(conversation.addMessage(TextMessage("", "", "", "", 0)))
        assertEquals(mutableListOf(textMessage, imgMessage), conversation.messages)
    }

    @Test
    fun testRemoveMessage(){
        var conversation = Conversation(user1Id, user2Id)

        // Invalid remove from empty
        assertEquals(conversation.messages.size, 0)
        assertFalse(conversation.removeMessage(textMessage))
        assertEquals(conversation.messages.size, 0)

        // Valid remove
        conversation.addMessage(textMessage)
        assertEquals(conversation.messages.size, 1)
        assertTrue(conversation.removeMessage(textMessage))
        assertEquals(conversation.messages.size, 0)

        // Invalid duplicate remove
        assertFalse(conversation.removeMessage(textMessage))
        assertEquals(conversation.messages.size, 0)

        // Valid remove image
        conversation.addMessage(imgMessage)
        assertEquals(conversation.messages.size, 1)
        assertTrue(conversation.removeMessage(imgMessage))
        assertEquals(conversation.messages.size, 0)


        // Invalid remove message not in conversation
        conversation.addMessage(textMessage)
        assertEquals(conversation.messages.size, 1)
        assertFalse(conversation.removeMessage(imgMessage))
        assertEquals(conversation.messages.size, 1)

        assertFalse(conversation.removeMessage(textMessageDifferentUsers))
        assertEquals(conversation.messages.size, 1)
    }
    // FIX: The following tests test the added static helper functions for property checking
    @Test
    fun testIsValidLastTimeSynched(){
        assertTrue(Conversation.isValidLastTimeSynched(0))
        assertTrue(Conversation.isValidLastTimeSynched(45423))
        assertTrue(Conversation.isValidLastTimeSynched(1))
        assertFalse(Conversation.isValidLastTimeSynched(-1))
        assertFalse(Conversation.isValidLastTimeSynched(-1000))
    }

    @Test
    fun testIsValidConversationId(){
        assertTrue(Conversation.isValidConversationId("abc123"))
        assertTrue(Conversation.isValidConversationId("a-b,."))
        assertFalse(Conversation.isValidConversationId(""))
        assertFalse(Conversation.isValidConversationId("!!!><"))
    }

    @Test
    fun testIsValidConversation(){
        assertTrue(Conversation.isValidConversation(Conversation(user1Id, user2Id, mutableListOf(imgMessage, textMessage))))
        assertTrue(Conversation.isValidConversation(Conversation(user1Id, user2Id, mutableListOf(imgMessage))))
        assertTrue(Conversation.isValidConversation(Conversation(user1Id, user2Id, mutableListOf())))
        assertFalse(Conversation.isValidConversation(Conversation("", user2Id, mutableListOf(imgMessage))))
        assertFalse(Conversation.isValidConversation(Conversation(user1Id, "", mutableListOf(imgMessage))))

        // Can't test invalid last time synched because setting it to a negative is blocked by the setter
    }
}