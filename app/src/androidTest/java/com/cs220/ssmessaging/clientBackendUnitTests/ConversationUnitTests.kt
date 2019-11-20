package com.cs220.ssmessaging.clientBackendUnitTests

import com.cs220.ssmessaging.clientBackend.*
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.json.JSONObject
import org.junit.Test
import org.junit.Assert.*


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

        assertEquals(mutableListOf<Message>(imgMessageTime1), fullConversation.getSubConversation(1,1).messages)
        assertEquals(mutableListOf<Message>(), emptyConversation.getSubConversation(0,10).messages)
        assertEquals(mutableListOf<Message>(), fullConversation.getSubConversation(5,0).messages)
        assertEquals(mutableListOf(imgMessageTime1, textMessageTime2, imgMessageTime3), fullConversation.getSubConversation(0,4).messages)
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

        assertEquals(emptyConversationJSON, emptyConversation.formatConversationData())
        assertEquals(oneSidedConversationJSON, oneSidedConversation.formatConversationData())
        assertEquals(twoSidedConversationJSON, twoSidedConversation.formatConversationData())
    }

    @Test
    fun testGetAnalytics() {

        // Since unit tests shouldn't make calls to API and we don't have implementation yet,
        // test that JSON object allows us to access properties we expect

        val documentTone1 = JSONObject()
        documentTone1.put("score", 0.6165)
        documentTone1.put("tone_id", "sadness")
        documentTone1.put("tone_name", "Sadness")

        val documentTone2 = JSONObject()
        documentTone2.put("score", 0.829888)
        documentTone2.put("tone_id", "analytical")
        documentTone2.put("tone_name", "Analytical")

        val document = JSONObject()
        document.put("tones", arrayOf(documentTone1, documentTone2))

        val sentenceTone1 = JSONObject()
        sentenceTone1.put("score", 0.801827)
        sentenceTone1.put("tone_id", "analytical")
        sentenceTone1.put("tone_name", "Analytical")

        val sentence1 = JSONObject()
        sentence1.put("sentence_id", 0)
        sentence1.put("text", "Team, I know that times are tough!")
        sentence1.put("tones", arrayOf(sentenceTone1))

        val sentenceTone2 = JSONObject()
        sentenceTone2.put("score", 0.771241)
        sentenceTone2.put("tone_id", "sadness")
        sentenceTone2.put("tone_name", "Sadness")

        val sentenceTone3 = JSONObject()
        sentenceTone3.put("score", 0.687768)
        sentenceTone3.put("tone_id", "analytical")
        sentenceTone3.put("tone_name", "Analytical")

        val sentence2 = JSONObject()
        sentence2.put("sentence_id", 1)
        sentence2.put("text", "Product sales have been disappointing for the past three quarters.")
        sentence2.put("tones", arrayOf(sentenceTone2, sentenceTone3))

        val sentenceTone4 = JSONObject()
        sentenceTone4.put("score", 0.506763)
        sentenceTone4.put("tone_id", "analytical")
        sentenceTone4.put("tone_name", "Analytical")

        val sentence3 = JSONObject()
        sentence3.put("sentence_id", 2)
        sentence3.put("text", "We have a competitive product, but we need to do a better job of selling it!")
        sentence3.put("tones", arrayOf(sentenceTone4))

        val sentences: Array<JSONObject> = arrayOf(sentence1, sentence2, sentence3)

        val mockResponse = JSONObject()
        mockResponse.put("document_tone", document)
        mockResponse.put("sentences_tone", sentences)

        val conversationMock = mock<Conversation> {
            on { getAnalytics() } doReturn mockResponse
        }

        val response: JSONObject = conversationMock.getAnalytics()

        assertNotNull(response.getJSONObject("document_tone"))
        assertNotNull(response.getJSONArray("sentences_tone"))

        assertEquals(response.getJSONObject("document_tone").getJSONArray("tones").length(), 2)
        assertEquals(response.getJSONArray("sentences_tone"), 3)
    }
}