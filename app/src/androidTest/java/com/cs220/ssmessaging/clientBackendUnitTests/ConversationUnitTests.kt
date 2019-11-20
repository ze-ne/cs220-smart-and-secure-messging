package com.cs220.ssmessaging.clientBackendUnitTests

import com.cs220.ssmessaging.clientBackend.*
import org.json.JSONObject
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith


class ConversationUnitTests{
    private var user1Id = "user1Id"
    private var user2Id = "user2Id"
    private var nullUser = ""
    // FIX: conversationId changed to a valid value also byteArray of image changed to valid value
    private var imgMessage : ImageMessage = ImageMessage(byteArrayOf(1,2), "user1-user2", user1Id, user2Id, 0)
    private var textMessage : TextMessage = TextMessage("troglobite", "user1-user2", user1Id, user2Id, 0)
    private var textMessageDifferentUsers : TextMessage = TextMessage("troglobitdse", "users-user2", nullUser, user2Id, 0)
    private var imgMessageTime1 : ImageMessage = ImageMessage(byteArrayOf(1,2), "user1-user2", user1Id, user2Id, 1)
    private var textMessageTime2 : TextMessage = TextMessage("troglobite", "user1-user2", user1Id, user2Id, 2)
    private var imgMessageTime3 : ImageMessage = ImageMessage(byteArrayOf(1,2), "user1-user2", user1Id, user2Id, 3)
    private var textMessageU2U1 : TextMessage = TextMessage("troglobite", "user1-user2", user2Id, user1Id, 0)

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

    @Test
    fun testGetSubConversation() {
        var emptyConversation = Conversation(user1Id, user2Id)
        var fullConversation = Conversation(user1Id, user2Id, mutableListOf(imgMessageTime1, textMessageTime2, imgMessageTime3))

        assertEquals(fullConversation.getSubConversation(1,1).messages, mutableListOf<Message>(imgMessageTime1))
        assertEquals(emptyConversation.getSubConversation(0,10).messages, mutableListOf<Message>())
        assertEquals(fullConversation.getSubConversation(5,0).messages, mutableListOf<Message>())
        assertEquals(fullConversation.getSubConversation(0,4).messages, mutableListOf(imgMessageTime1, textMessageTime2, imgMessageTime3))
    }

    @Test
    fun testFormatConversationData() {
        var emptyConversation = Conversation(user1Id, user2Id)
        var oneSidedConversation = Conversation(user1Id, user2Id, mutableListOf(textMessageTime2))
        var twoSidedConversation = Conversation(user1Id, user2Id, mutableListOf(textMessageTime2, textMessageU2U1))
        var ut1 = JSONObject()
        ut1.put("text:", textMessageTime2.message)
        ut1.put("user:", textMessageTime2.senderId)

        var ut2 = JSONObject()
        ut1.put("text:", textMessageU2U1.message)
        ut1.put("user:", textMessageU2U1.senderId)

        var conv1Uts = listOf(ut1)
        var conv2Uts = listOf(ut1, ut2)

        var emptyConversationJSON = JSONObject()
        emptyConversationJSON.put("utterances", listOf<JSONObject>())

        var oneSidedConversationJSON = JSONObject()
        oneSidedConversationJSON.put("utterances", conv1Uts)

        var twoSidedConversationJSON = JSONObject()
        twoSidedConversationJSON.put("utterances", conv2Uts)

        assertEquals(emptyConversation.formatConversationData(), emptyConversationJSON)
        assertEquals(oneSidedConversation.formatConversationData(), oneSidedConversationJSON)
        assertEquals(twoSidedConversation.formatConversationData(), twoSidedConversationJSON)
    }
}