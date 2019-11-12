package com.cs220.ssmessaging.clientBackendUnitTests

import com.cs220.ssmessaging.clientBackend.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith


class ConversationUnitTests{
    private var user1 = User("user1", "Tom", "gordon")
    private var user2 = User("user2", "Jim", "t246531")
    private var nullUser = User("", "", "")
    private var imgMessage : ImageMessage = ImageMessage(ByteArray(0), "", user1, user2, 0)
    private var textMessage : TextMessage = TextMessage("troglobite", "", user1, user2, 0)
    private var textMessageDifferentUsers : TextMessage = TextMessage("troglobitdse", "", nullUser, user2, 0)

    @Test
    fun testDefaultConstructor() {
        var conversation = Conversation()
        assertEquals("", conversation.convoId)
        assertEquals("", conversation.user1)
        assertEquals("", conversation.user2)
        assertEquals(0, conversation.lastTimeSynced)
        assertEquals(0, conversation.messages.size)
    }

    @Test
    fun testSecondaryConstructorNoMessages() {
        // Valid users
        var conversation = Conversation(user1, user2)
        assertEquals("user1-user2", conversation.convoId)
        assertEquals("user1", conversation.user1)
        assertEquals("user2", conversation.user2)
        assertEquals(0, conversation.lastTimeSynced)
        assertEquals(0, conversation.messages.size)

        // Invalid second user
        conversation = Conversation(user1, nullUser)
        assertEquals("", conversation.convoId)
        assertEquals("", conversation.user1)
        assertEquals("", conversation.user2)
        assertEquals(0, conversation.lastTimeSynced)
        assertEquals(0, conversation.messages.size)

        // Invalid first user
        conversation = Conversation(nullUser, user2)
        assertEquals("", conversation.convoId)
        assertEquals("", conversation.user1)
        assertEquals("", conversation.user2)
        assertEquals(0, conversation.lastTimeSynced)
        assertEquals(0, conversation.messages.size)
    }

    @Test
    fun testSecondaryConstructorWithMessages() {
        // Valid users and messages
        var conversation = Conversation(user1, user2, mutableListOf(imgMessage, textMessage))
        assertEquals("user1-user2", conversation.convoId)
        assertEquals("user1", conversation.user1)
        assertEquals("user2", conversation.user2)
        assertEquals(0, conversation.lastTimeSynced)
        assertEquals(2, conversation.messages.size)
        assertEquals(imgMessage.message, (conversation.messages[0] as ImageMessage).message)
        assertEquals(textMessage.message, (conversation.messages[0] as ImageMessage).message)

        // Valid users and no message
        conversation = Conversation(user1, user2, mutableListOf())
        assertEquals("user1-user2", conversation.convoId)
        assertEquals("user1", conversation.user1)
        assertEquals("user2", conversation.user2)
        assertEquals(0, conversation.lastTimeSynced)
        assertEquals(0, conversation.messages.size)

        // Invalid second user
        conversation = Conversation(user1, nullUser, mutableListOf(imgMessage, textMessage))
        assertEquals("", conversation.convoId)
        assertEquals("", conversation.user1)
        assertEquals("", conversation.user2)
        assertEquals(0, conversation.lastTimeSynced)
        assertEquals(0, conversation.messages.size)

        // Invalid first user
        conversation = Conversation(nullUser, user1, mutableListOf(imgMessage, textMessage))
        assertEquals("", conversation.convoId)
        assertEquals("", conversation.user1)
        assertEquals("", conversation.user2)
        assertEquals(0, conversation.lastTimeSynced)
        assertEquals(0, conversation.messages.size)

        // Invalid message in messages (userid conflict)
        conversation = Conversation(nullUser, user2, mutableListOf(imgMessage, textMessageDifferentUsers))
        assertEquals("", conversation.convoId)
        assertEquals("", conversation.user1)
        assertEquals("", conversation.user2)
        assertEquals(0, conversation.lastTimeSynced)
        assertEquals(0, conversation.messages.size)
    }

    @Test
    fun testMessageListGetterAndSetter() {
        // Valid settings.
        var conversation = Conversation(user1, user2)
        assertEquals(0, conversation.messages.size)

        conversation.messages = mutableListOf(imgMessage)
        assertEquals(mutableListOf(imgMessage), conversation.messages)

        conversation.messages = mutableListOf(imgMessage, textMessage)
        assertEquals(mutableListOf(imgMessage, textMessage), conversation.messages)

        // Invalid setting because of different user
        conversation.messages = mutableListOf(imgMessage, textMessageDifferentUsers)
        assertEquals(mutableListOf(imgMessage, 0), conversation.messages)
    }

    @Test
    fun testConversationIdGetter(){
        // Only two cases: where the conversation is invalid and where the conversation is valid
        var conversation = Conversation(user1, user2)
        assertEquals("user1-user2", conversation.convoId)

        conversation = Conversation(nullUser, user2)
        assertEquals("", conversation.convoId)
    }

    @Test
    fun testLastTimeSynchedGetterAndSetter(){
        var conversation = Conversation(user1, user2)
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
        var conversation = Conversation(user1, user2)

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
        assertFalse(conversation.addMessage(TextMessage("", "", User(), User(), 0)))
        assertEquals(mutableListOf(textMessage, imgMessage), conversation.messages)
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
        assertTrue(Conversation.isValidConversation(Conversation(user1, user2, mutableListOf(imgMessage, textMessage))))
        assertTrue(Conversation.isValidConversation(Conversation(user1, user2, mutableListOf(imgMessage))))
        assertTrue(Conversation.isValidConversation(Conversation(user1, user2, mutableListOf())))
        assertFalse(Conversation.isValidConversation(Conversation(User(), user2, mutableListOf(imgMessage))))
        assertFalse(Conversation.isValidConversation(Conversation(user1, User(), mutableListOf(imgMessage))))

        // Can't test invalid last time synched because setting it to a negative is blocked by the setter
    }
}