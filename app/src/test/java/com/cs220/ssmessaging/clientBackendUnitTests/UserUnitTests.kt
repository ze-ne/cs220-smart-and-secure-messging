package com.cs220.ssmessaging.clientBackendUnitTests

import android.graphics.Bitmap
import com.cs220.ssmessaging.clientBackend.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

import com.cs220.ssmessaging.clientBackend.User
import com.cs220.ssmessaging.clientBackend.Conversation

@RunWith(MockitoJUnitRunner::class)
class UserUnitTests{

    @Test
    fun testConstructor() {
        val user1 = User("id1","first", "last")
        assertEquals(user1.userId, "id1")
        assertEquals(user1.firstName, "first")
        assertEquals(user1.lastName, "last")
        assertEquals(user1.contacts.size, 0)
        assertEquals(user1.conversations.size, 0)

    }

    @Test
    fun testAddContact() {
        val user1 = User("id1","first", "last")
        val user2 = User("id2","first", "last")
        var listSize: Int

        // Valid add
        listSize = user1.contacts.size
        assertEquals(user1.contacts.size, listSize)
        assertFalse(user2 in user1.contacts)
        assertTrue(user1.addContact(user2))
        assertEquals(user1.contacts.size, listSize + 1)
        assertTrue(user2 in user1.contacts)

        // Invalid null add
        listSize = user1.contacts.size
        assertEquals(user1.contacts.size, listSize)
        assertFalse(user1.addContact(null as User))
        assertEquals(user1.contacts.size, listSize)

        // Invalid duplicate add
        listSize = user1.contacts.size
        assertEquals(user1.contacts.size, listSize)
        assertFalse(user1.addContact(user2))
        assertEquals(user1.contacts.size, listSize)
        assertTrue(user2 in user1.contacts)

    }

    @Test
    fun testDeleteContact() {
        val user1 = User("id1","first", "last")
        val user2 = User("id2","first", "last")
        val user3 = User("id3","first", "last")
        var listSize: Int

        // Valid delete
        user1.addContact(user2)
        listSize = user1.contacts.size
        assertEquals(user1.contacts.size, listSize)
        assertTrue(user2 in user1.contacts)
        assertTrue(user1.deleteContact(user2))
        assertFalse(user2 in user1.contacts)
        assertEquals(user1.contacts.size, listSize - 1)

        // Invalid repeated delete
        listSize = user1.contacts.size
        assertEquals(user1.contacts.size, listSize)
        assertFalse(user2 in user1.contacts)
        assertFalse(user1.deleteContact(user2))
        assertFalse(user2 in user1.contacts)
        assertEquals(user1.contacts.size, listSize)

        // Invalid unknown user delete
        listSize = user1.contacts.size
        assertEquals(user1.contacts.size, listSize)
        assertFalse(user3 in user1.contacts)
        assertFalse(user1.deleteContact(user3))
        assertFalse(user3 in user1.contacts)
        assertEquals(user1.contacts.size, listSize)

        // Invalid nonsense user delete
        listSize = user1.contacts.size
        assertEquals(user1.contacts.size, listSize)
        assertFalse(user1.deleteContact(null as User))
        assertEquals(user1.contacts.size, listSize)

    }

    @Test
    fun testGetContactById() {
        val user1 = User("id1","first", "last")
        val user2 = User("id2","first", "last")
        user1.addContact(user2)
        user2.addContact(user1)

        // Valid search
        assertTrue(user2 in user1.contacts)
        assertTrue(user1 in user2.contacts)
        assertSame(user2, user1.getContactById("id2"))
        assertSame(user1, user2.getContactById("id1"))

        // Invalid search
        assertNull(user1.getContactById("id3"))

        // Invalid empty parameter search
        assertNull(user1.getContactById(""))

        // Invalid null parameter search
        assertNull(user1.getContactById(null as String))


    }

    @Test
    fun testAddConversation() {
        val user1 = User("id1","first", "last")
        val user2 = User("id2","first", "last")
        val conversation1 = Conversation(user1, user2)
        var listSize: Int

        // Valid add
        listSize = user1.conversations.size
        assertEquals(user1.conversations.size, listSize)
        assertFalse(conversation1 in user1.conversations)
        assertTrue(user1.addConversation(conversation1))
        assertEquals(user1.conversations.size, listSize + 1)
        assertTrue(conversation1 in user1.conversations)

        // Invalid duplicate add
        listSize = user1.conversations.size
        assertEquals(user1.conversations.size, listSize)
        assertTrue(conversation1 in user1.conversations)
        assertFalse(user1.addConversation(conversation1))
        assertEquals(user1.conversations.size, listSize)

        // Invalid null add
        listSize = user1.conversations.size
        assertEquals(user1.conversations.size, listSize)
        assertFalse(user1.addConversation(null as Conversation))
        assertEquals(user1.conversations.size, listSize)

    }

    @Test
    fun testGetConversationByUserId() {
        val user1 = User("id1","first", "last")
        val user2 = User("id2","first", "last")
        val conversation1 = Conversation(user1, user2)
        user1.addConversation(conversation1)
        user2.addConversation(conversation1)

        // Valid get
        assertSame(conversation1, user1.getConversationByUserId("id2"))
        assertSame(conversation1, user2.getConversationByUserId("id1"))

        // Invalid bad get
        assertNull(user1.getConversationByUserId("id555"))

        // Invalid empty get
        assertNull(user1.getConversationByUserId(""))

        // Invalid null get
        assertNull(user1.getConversationByUserId(null as String))


    }

    fun searchForTextMessage(textMessages: MutableList<TextMessage>, query: String): Boolean {
        for (textMessage in textMessages) {
            if (textMessage.message.equals(query)) {
                return true
            }
        }
        return false
    }

    @Test
    fun send_text_message() {
        val user1 = User("userId1", "User", "One")
        val user2 = User("userId2", "User", "Two")
        val conversation = Conversation(user1, user2)

        val oldLength: Int = conversation.messages.size
        assertEquals(conversation.messages.size, 0)

        user1.sendTextMsg("Hi, how are you?", conversation)
        assertEquals(conversation.messages.size, oldLength + 1)
        assertTrue(searchForTextMessage(conversation.messages as MutableList<TextMessage>, "Hi, how are you?"))
    }

    @Test
    fun send_image_message() {
        val user1 = User("userId1", "User", "One")
        val user2 = User("userId2", "User", "Two")
        val conversation = Conversation(user1, user2)

        val oldLength: Int = conversation.messages.size
        assertEquals(conversation.messages.size, 0)

        user1.sendImageMsg(Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888, false), conversation)

        assertEquals(conversation.messages.size, oldLength + 1)
        assertEquals((conversation.messages[0] as ImageMessage).message, null)
    }

    @Test
    fun testGetConversationByConversationId() {
        val user1 = User("id1","first", "last")
        val user2 = User("id2","first", "last")
        val conversation = Conversation(user1, user2)
        val conversationBad = Conversation(user1, user2)
        val conversationId = conversation.convoId
        val conversationIdBad = conversationBad.convoId

        user1.addConversation(conversation)
        user2.addConversation(conversation)

        // Valid get
        assertSame(conversation, user1.getConversationByConversationId(conversationId))
        assertSame(conversation, user2.getConversationByConversationId(conversationId))

        // Invalid bad get
        assertNull(user2.getConversationByConversationId(conversationIdBad))

        // Invalid null get
        assertNull(user2.getConversationByConversationId(null as String))

    }
}