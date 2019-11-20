package com.cs220.ssmessaging.clientBackendUnitTests

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import com.cs220.ssmessaging.clientBackend.Conversation
import com.cs220.ssmessaging.clientBackend.Message
import com.cs220.ssmessaging.clientBackend.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert
import org.junit.Test
import java.nio.charset.Charset
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.time.Instant
import java.util.*

class BlockListUnitTests{

    @Test
    fun testaddToBlockList{
        //User(id,first,last,contacts,conversations,blocklist)
        val user1 = User("id1","Ronald", "McDonald", mutableListOf<String>(), mutableListOf<Conversation>(), mutableListOf<User>())
        val user2 = User("id2","Pooh", "Bear", mutableListOf<String>(), mutableListOf<Conversation>(), mutableListOf<User>())
        Assert.assertEquals(0, user1.blocklist.size)
        user1.addToBlockList(user2)
        Assert.assertEquals(1, user1.blocklist.size)
    }

    @Test
    fun testremoveFromBlockList{
        val user1 = User("id1","Ronald", "McDonald", mutableListOf<String>(), mutableListOf<Conversation>(), mutableListOf<User>())
        val user2 = User("id2","Pooh", "Bear", mutableListOf<String>(), mutableListOf<Conversation>(), mutableListOf<User>())
        user1.addToBlockList(user2)
        Assert.assertEquals(1, user1.blocklist.size)
        user1.removeFromBlockList(user2)
        Assert.assertEquals(0, user1.blocklist.size)
    }

    @Test
    fun testaddContactIfBlocked{
        val user1 = User("id1","Ronald", "McDonald", mutableListOf<String>(), mutableListOf<Conversation>(), mutableListOf<User>())
        val user2 = User("id2","Pooh", "Bear", mutableListOf<String>(), mutableListOf<Conversation>(), mutableListOf<User>())
        user1.addToBlockList(user2)
        user2.addContact(user1)
        user1.addContact(user2)
        Assert.assertEquals(0, user2.contacts.size)
        Assert.assertEquals(0,user1.contacts.size)
    }

    @Test
    fun testaddConversationIfBlocked{
        val user1 = User("id1","Ronald", "McDonald", mutableListOf<String>(), mutableListOf<Conversation>(), mutableListOf<User>())
        val user2 = User("id2","Pooh", "Bear", mutableListOf<String>(), mutableListOf<Conversation>(), mutableListOf<User>())
        user1.addToBlockList(user2)
        val convo1 = Conversation(user1, user2, mutableListOf<Message>())
        Assert.assertEquals(null,convo1)
    }

    @Test
    fun testautoRemoveContact{
        val user1 = User("id1","Ronald", "McDonald", mutableListOf<String>(), mutableListOf<Conversation>(), mutableListOf<User>())
        val user2 = User("id2","Pooh", "Bear", mutableListOf<String>(), mutableListOf<Conversation>(), mutableListOf<User>())
        user1.addToBlockList(user2)
        Assert.assertEquals(false, (user1 in user2.contacts))
        Assert.assertEquals(false, (user2 in user1.contacts))
    }
}