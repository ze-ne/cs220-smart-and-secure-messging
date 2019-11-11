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
        assertEquals("id1", user1.userId)
        assertEquals("first", user1.firstName)
        assertEquals("last", user1.lastName)
        assertEquals(0, user1.contacts.size)
        assertEquals(0, user1.conversations.size)

    }

    @Test
    fun testSecondaryConstructor(){
        // invalid case
        var user1 = User("id1","first", "last", mutableListOf(User()), mutableListOf())
        assertEquals("", user1.userId)
        assertEquals("", user1.firstName)
        assertEquals("", user1.lastName)
        assertEquals(0, user1.contacts.size)
        assertEquals(0, user1.conversations.size)

        // Valid case
        var dummyUser1 : User = User("a","b", "c")
        var dummyUser2 : User = User("d","e", "f")
        var convo : Conversation = Conversation(dummyUser1, dummyUser2, mutableListOf())
        user1 = User("id1","first", "last", mutableListOf(User("hello", "dumb", "boy")), mutableListOf(convo))
        assertEquals("id1", user1.userId)
        assertEquals("first", user1.firstName)
        assertEquals("last", user1.lastName)
        assertEquals(1, user1.contacts.size)
        assertEquals(1, user1.conversations.size)
    }

    @Test
    fun testAddContact() {
        val user1 = User("id1","first", "last")
        val user2 = User("id2","first", "last")
        var listSize: Int

        // Valid add
        listSize = user1.contacts.size
        assertFalse(user2 in user1.contacts)
        assertTrue(user1.addContact(user2))
        assertEquals(listSize + 1, user1.contacts.size)
        assertTrue(user2 in user1.contacts)

        // Invalid null add
        listSize = user1.contacts.size
        assertFalse(user1.addContact(null as User))
        assertEquals(listSize, user1.contacts.size)

        // Invalid duplicate add
        listSize = user1.contacts.size
        assertFalse(user1.addContact(user2))
        assertEquals(listSize, user1.contacts.size)
        assertTrue(user2 in user1.contacts)

    }

    @Test
    fun testDeleteContact() {
        val user1 = User("id1","first", "last")
        val user2 = User("id2","first", "last")
        val user3 = User("id3","first", "last")
        var listSize: Int
        user1.addContact(user2)

        // Valid delete
        listSize = user1.contacts.size
        assertTrue(user2 in user1.contacts)
        assertTrue(user1.deleteContact(user2))
        assertFalse(user2 in user1.contacts)
        assertEquals(listSize - 1, user1.contacts.size)

        // Invalid repeated delete
        listSize = user1.contacts.size
        assertFalse(user2 in user1.contacts)
        assertFalse(user1.deleteContact(user2))
        assertFalse(user2 in user1.contacts)
        assertEquals(listSize, user1.contacts.size)

        // Invalid unknown user delete
        listSize = user1.contacts.size
        assertFalse(user3 in user1.contacts)
        assertFalse(user1.deleteContact(user3))
        assertFalse(user3 in user1.contacts)
        assertEquals(listSize, user1.contacts.size)

        // Invalid nonsense user delete
        listSize = user1.contacts.size
        assertFalse(user1.deleteContact(null as User))
        assertEquals(listSize, user1.contacts.size)

    }

    @Test
    fun testGetContactById() {
        val user1 = User("id1","first", "last")
        val user2 = User("id2","first", "last")
        user1.addContact(user2)
        user2.addContact(user1)

        // Valid get
        assertTrue(user2 in user1.contacts)
        assertTrue(user1 in user2.contacts)
        assertSame(user2, user1.getContactById("id2"))
        assertSame(user1, user2.getContactById("id1"))

        // Invalid get
        assertNull(user1.getContactById("id3"))

        // Invalid empty parameter get
        assertNull(user1.getContactById(""))

        // Invalid null parameter get
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
        assertFalse(conversation1 in user1.conversations)
        assertTrue(user1.addConversation(conversation1))
        assertEquals(listSize + 1, user1.conversations.size)
        assertTrue(conversation1 in user1.conversations)

        // Invalid duplicate add
        listSize = user1.conversations.size
        assertTrue(conversation1 in user1.conversations)
        assertFalse(user1.addConversation(conversation1))
        assertEquals(listSize, user1.conversations.size)

        // Invalid null add
        listSize = user1.conversations.size
        assertFalse(user1.addConversation(null as Conversation))
        assertEquals(listSize, user1.conversations.size)

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

        // Invalid get unknown parameter
        assertNull(user1.getConversationByUserId("id555"))

        // Invalid get empty parameter
        assertNull(user1.getConversationByUserId(""))

        // Invalid get null parameter
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

        user1.sendImageMsg(ByteArray(0), conversation)

        assertEquals(conversation.messages.size, oldLength + 1)
        assertEquals((conversation.messages[0] as ImageMessage).message, null)
    }

    @Test
    fun testGetConversationByConversationId() {
        val user1 = User("id1","first", "last")
        val user2 = User("id2","first", "last")
        val conversation = Conversation(user1, user2)
        val conversationId = conversation.convoId
        val conversationIdBad = "1234567"

        user1.addConversation(conversation)
        user2.addConversation(conversation)

        // Valid get
        assertSame(conversation, user1.getConversationByConversationId(conversationId))
        assertSame(conversation, user2.getConversationByConversationId(conversationId))

        // Invalid get unknown parameter
        assertNull(user2.getConversationByConversationId(conversationIdBad))

        // Invalid get empty parameter
        assertNull(user2.getConversationByConversationId(""))

        // Invalid get null parameter
        assertNull(user2.getConversationByConversationId(null as String))

    }

    // FIX: The following tests test the added static helper functions for property checking
    @Test
    fun testIsValidUser(){
        assertTrue(User.isValidUser(User("id1","first", "last")))

        // Can only test the "null" user instance (i.e. entries are "" or empty)
        // since the constructors use the isValidUser() static function
        assertFalse(User.isValidUser(User("","", "")))
    }

    @Test
    fun testIsValidUserId(){
        assertTrue(User.isValidUserId("abc123"))
        assertTrue(User.isValidUserId("1.2"))
        assertTrue(User.isValidUserId("1_X_1-1"))
        assertTrue(User.isValidUserId("@"))
        assertFalse(User.isValidUserId("!"))
        assertFalse(User.isValidUserId("<>"))
        assertFalse(User.isValidUserId(""))
    }

    @Test
    fun testIsValidName(){
        assertTrue(User.isValidName("Henri"))
        assertTrue(User.isValidName("J"))
        assertTrue(User.isValidName("123Jane"))
        assertFalse(User.isValidName("John 1"))
        assertFalse(User.isValidName("!!!@___"))
        assertFalse(User.isValidName("<"))
        assertFalse(User.isValidName(""))
    }
}