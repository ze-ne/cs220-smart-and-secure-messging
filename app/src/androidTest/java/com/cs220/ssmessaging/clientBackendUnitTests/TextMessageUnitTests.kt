package com.cs220.ssmessaging.clientBackendUnitTests

import com.cs220.ssmessaging.clientBackend.TextMessage
import com.cs220.ssmessaging.clientBackend.User
import org.junit.Assert
import org.junit.Test


class TextMessageUnitTests{
    private var user1 = User("Joker", "Bad", "Man")
    private var user2 = User("Ronald", "McDonald", "Clown")
    private var textmessage = TextMessage("getyourhotdogs", "normal", user1, user2, 12)

    // Note that this constructor test also tests the getters (No setters since all properties are val)
    @Test
    fun testConstructor(){
        // Test valid message (from private immagemessage)
        Assert.assertEquals("getyourhotdogs", textmessage.message)
        Assert.assertEquals("normal", textmessage.conversationId)
        Assert.assertEquals(user1, textmessage.sender)
        Assert.assertEquals(user2, textmessage.recipient)
        Assert.assertEquals(12, textmessage.timestamp)
    }

    @Test
    fun testConstructorFailCases(){
        // Test bad messages (No bytes)
        var badMessage = TextMessage("", "123213", user1, user2, 12)
        Assert.assertEquals("", badMessage.message)
        Assert.assertEquals("", badMessage.conversationId)
        // FIX: changed to user.userId comparisons for assertion
        Assert.assertEquals(User().userId, badMessage.sender.userId)
        Assert.assertEquals(0, badMessage.timestamp)

        // Test bad convo id (No id)
        badMessage = TextMessage("hhhh", "", user1, user2, 12)
        Assert.assertEquals("", badMessage.message)
        Assert.assertEquals("", badMessage.conversationId)
        Assert.assertEquals(User().userId, badMessage.sender.userId)
        Assert.assertEquals(0, badMessage.timestamp)

        // Test bad user (No user)
        badMessage = TextMessage("hhh", "525", User(), user2, 12)
        Assert.assertEquals("", badMessage.message)
        Assert.assertEquals("", badMessage.conversationId)
        Assert.assertEquals(User().userId, badMessage.sender.userId)
        Assert.assertEquals(0, badMessage.timestamp)

        // Test bad timestamp
        badMessage = TextMessage("hhh", "525", user1, user2, -1)
        Assert.assertEquals("", badMessage.message)
        Assert.assertEquals("", badMessage.conversationId)
        Assert.assertEquals(User().userId, badMessage.sender.userId)
        Assert.assertEquals(0, badMessage.timestamp)
    }

    // FIX: The following tests test the added static helper functions for property checking
    @Test
    fun testIsValidMessage(){
        // Valid message
        Assert.assertTrue(TextMessage.isValidMessage(textmessage))

        // Invalid message - can only test the "null" message instance (i.e. entries are "" or empty)
        // since the constructors use the isValidMessage() static function
        Assert.assertFalse(TextMessage.isValidMessage(TextMessage("", "", User(), User(), -1)))
    }

    @Test
    fun testIsValidMessageBody(){
        Assert.assertTrue(TextMessage.isValidMessageBody("abc -!@#123"))
        Assert.assertTrue(TextMessage.isValidMessageBody("x"))
        Assert.assertFalse(TextMessage.isValidMessageBody(""))
    }
}