package com.cs220.ssmessaging.clientBackendUnitTests

import android.graphics.Bitmap
import com.cs220.ssmessaging.clientBackend.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UserUnitTests{
    @Test
    fun dummy_test() {
        // Delete this test when you have added actual tests!
        // This is just to allow for the program to compile.
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
}