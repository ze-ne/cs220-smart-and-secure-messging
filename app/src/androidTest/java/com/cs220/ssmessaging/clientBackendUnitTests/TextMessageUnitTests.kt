package com.cs220.ssmessaging.clientBackendUnitTests

import com.cs220.ssmessaging.clientBackend.TextMessage
import com.cs220.ssmessaging.clientBackend.User
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Assert
import org.junit.Test


class TextMessageUnitTests{
    private var user1Id = "Joker"
    private var user2Id = "Ronald" 
    private var textmessage = TextMessage("getyourhotdogs", "normal", user1Id, user2Id, 12)

    // Note that this constructor test also tests the getters (No setters since all properties are val)
    @Test
    fun testConstructor(){
        // Test valid message (from private immagemessage)
        Assert.assertEquals("getyourhotdogs", textmessage.message)
        Assert.assertEquals("normal", textmessage.conversationId)
        Assert.assertEquals(user1Id, textmessage.senderId)
        Assert.assertEquals(user2Id, textmessage.recipientId)
        Assert.assertEquals(12, textmessage.timestamp)
    }

    @Test
    fun testConstructorFailCases(){
        // Test bad messages (No bytes)
        var badMessage = TextMessage("", "123213", user1Id, user2Id, 12)
        Assert.assertEquals("", badMessage.message)
        Assert.assertEquals("", badMessage.conversationId)
        // FIX: changed to user comparisons for assertion
        Assert.assertEquals("", badMessage.senderId)
        Assert.assertEquals(0, badMessage.timestamp)

        // Test bad convo id (No id)
        badMessage = TextMessage("hhhh", "", user1Id, user2Id, 12)
        Assert.assertEquals("", badMessage.message)
        Assert.assertEquals("", badMessage.conversationId)
        Assert.assertEquals("", badMessage.senderId)
        Assert.assertEquals(0, badMessage.timestamp)

        // Test bad user (No user)
        badMessage = TextMessage("hhh", "525", "", user2Id, 12)
        Assert.assertEquals("", badMessage.message)
        Assert.assertEquals("", badMessage.conversationId)
        Assert.assertEquals("", badMessage.senderId)
        Assert.assertEquals(0, badMessage.timestamp)

        // Test bad timestamp
        badMessage = TextMessage("hhh", "525", user1Id, user2Id, -1)
        Assert.assertEquals("", badMessage.message)
        Assert.assertEquals("", badMessage.conversationId)
        Assert.assertEquals("", badMessage.senderId)
        Assert.assertEquals(0, badMessage.timestamp)
    }

    // FIX: The following tests test the added static helper functions for property checking
    @Test
    fun testIsValidMessage(){
        // Valid message
        Assert.assertTrue(TextMessage.isValidMessage(textmessage))

        // Invalid message - can only test the "null" message instance (i.e. entries are "" or empty)
        // since the constructors use the isValidMessage() static function
        Assert.assertFalse(TextMessage.isValidMessage(TextMessage("", "", "", "", -1)))
    }

    @Test
    fun testIsValidMessageBody(){
        Assert.assertTrue(TextMessage.isValidMessageBody("abc -!@#123"))
        Assert.assertTrue(TextMessage.isValidMessageBody("x"))
        Assert.assertFalse(TextMessage.isValidMessageBody(""))
    }

    @Test
    fun testMEquals(){
        var msg1 = TextMessage("a!", "123213", user1Id, user2Id, 12)
        var msg2 = TextMessage("a!", "123213", user1Id, user2Id, 13)
        var msg3 = TextMessage("a!", "1233", user1Id, user2Id, 12)
        var msg4 = TextMessage("a!", "123213", user2Id, user2Id, 12)

        assertTrue(msg1.mEquals(msg1))
        assertFalse(msg1.mEquals(msg2))
        assertFalse(msg1.mEquals(msg3))
        assertFalse(msg1.mEquals(msg4))
    }
}