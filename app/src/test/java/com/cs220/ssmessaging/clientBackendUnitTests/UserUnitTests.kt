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
    fun test_addContact() {
        val user1 = User("id1","first", "last")
        val user2 = User("id2","first", "last")

        assertEquals(user1.contacts.size, 0)

        assertTrue(user1.addContact(user2))

        assertEquals(user1.contacts.size, 1)
        assertTrue(user2 in user1.contacts)
    }

    @Test
    fun test_deleteContact() {
        val user1 = User("id1","first", "last")
        val user2 = User("id2","first", "last")
        user1.addContact(user2)

        assertTrue(user2 in user1.contacts)
        assertEquals(user1.contacts.size, 1)

        assertTrue(user1.deleteContact(user2))

        assertFalse(user2 in user1.contacts)
        assertEquals(user1.contacts.size, 0)

    }

    @Test
    fun test_getContactById() {
        val user1 = User("id1","first", "last")
        val user2 = User("id2","first", "last")
        user1.addContact(user2)
        user2.addContact(user1)

        assertTrue(user2 in user1.contacts)
        assertTrue(user1 in user2.contacts)
        assertSame(user2, user1.getContactById("id2"))
        assertSame(user1, user2.getContactById("id1"))

    }

    @Test
    fun test_addConversation() {
        val user1 = User("id1","first", "last")
        val user2 = User("id2","first", "last")
        val conversation1 = Conversation(user1, user2)

        assertFalse(conversation1 in user1.conversations)
        assertEquals(user1.conversations.size, 0)

        assertTrue(user1.addConversation(conversation1))

        assertEquals(user1.conversations.size, 1)
        assertTrue(conversation1 in user1.conversations)
    }

    @Test
    fun test_getConversationByUserId() {
        val user1 = User("id1","first", "last")
        val user2 = User("id2","first", "last")
        val conversation1 = Conversation(user1, user2)
        user1.addConversation(conversation1)
        user2.addConversation(conversation1)

        assertSame(conversation1, user1.getConversationByUserId("id2"))
        assertSame(conversation1, user2.getConversationByUserId("id1"))

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
    fun test_getConversationByConversationId() {
        val user1 = User("id1","first", "last")
        val user2 = User("id2","first", "last")
        val conversation = Conversation(user1, user2)
        val conversationId = conversation.convoId
        user1.addConversation(conversation)
        user2.addConversation(conversation)

        assertSame(conversation, user1.getConversationByConversationId(conversationId))
        assertSame(conversation, user2.getConversationByConversationId(conversationId))

    }
}