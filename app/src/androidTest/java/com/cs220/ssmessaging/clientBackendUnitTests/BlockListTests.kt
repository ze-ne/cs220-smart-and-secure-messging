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
import org.junit.Assert.*
import org.junit.Test
import java.nio.charset.Charset
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.time.Instant
import java.util.*

class BlockListUnitTests{

    @Test
    fun testaddToBlockedContact(){
        //User(id,first,last,contacts,conversations,blocklist)
        val user1 = User("id1","Ronald", "McDonald", mutableListOf<String>(), mutableListOf<Conversation>())

        assertEquals(0, user1.blockedContacts.size)
        assertTrue(user1.addBlockedContact("id2"))
        assertEquals(1, user1.blockedContacts.size)
        assertTrue(user1.addBlockedContact("id11"))
        assertEquals(2, user1.blockedContacts.size)

        // Error case, malformed userid or same userid
        assertFalse(user1.addBlockedContact("id1"))
        assertEquals(2, user1.blockedContacts.size)

        assertFalse(user1.addBlockedContact(""))
        assertEquals(2, user1.blockedContacts.size)
    }

    @Test
    fun testremoveFromBlockList() {
        val user1 = User(
            "id1",
            "Ronald",
            "McDonald",
            mutableListOf<String>(),
            mutableListOf<Conversation>()
        )

        user1.blockedContacts = mutableListOf("hey", "hey2", "hey3")
        assertEquals(3, user1.blockedContacts.size)

        assertTrue(user1.deleteBlockedContact("hey2"))
        assertEquals(2, user1.blockedContacts.size)
        assertFalse("hey2" in user1.blockedContacts)

        assertTrue(user1.deleteBlockedContact("hey1"))
        assertEquals(1, user1.blockedContacts.size)
        assertFalse("hey1" in user1.blockedContacts)

        // Invalid deletions or userid does not exist
        assertFalse(user1.deleteBlockedContact(""))
        assertEquals(1, user1.blockedContacts.size)
        assertTrue("hey3" in user1.blockedContacts)

        assertFalse(user1.deleteBlockedContact("hey1"))
        assertEquals(1, user1.blockedContacts.size)
        assertTrue("hey3" in user1.blockedContacts)

        user1.blockedContacts = mutableListOf()

        assertFalse(user1.deleteBlockedContact("hey1"))
        assertEquals(0, user1.blockedContacts.size)
    }

    @Test
    fun testFindBlockedContactById() {
        val blockedUserId = "blocked_id"
        val randomId = "random_id"
        val emptyId = ""
        val user1 = User()

        // Success case
        user1.addBlockedContact(blockedUserId)
        assertTrue(user1.checkIfBlocked(blockedUserId))

        // Fail case: non-blocked ID
        assertFalse(user1.checkIfBlocked(randomId))

        // Fail case: empty ID string
        assertFalse(user1.checkIfBlocked(emptyId))
    }

    @Test
    fun testDeleteBlockedContact() {
        val blockedUserId = "blocked_id"
        val randomId = "random_id"
        val emptyId = ""
        var listSize: Int
        val user1 = User()

        // Success case
        user1.addBlockedContact(blockedUserId)
        listSize = user1.blockedContacts.size
        assertTrue(user1.deleteBlockedContact(blockedUserId))
        assertEquals(listSize - 1, user1.blockedContacts.size)

        // Fail case: already removed ID
        listSize = user1.blockedContacts.size
        assertFalse(user1.deleteBlockedContact(blockedUserId))
        assertEquals(listSize, user1.blockedContacts.size)

        // Fail case: non-blocked ID
        listSize = user1.blockedContacts.size
        assertFalse(user1.deleteBlockedContact(randomId))
        assertEquals(listSize, user1.blockedContacts.size)

        // Fail case: empty ID string
        listSize = user1.blockedContacts.size
        assertFalse(user1.deleteBlockedContact(emptyId))
        assertEquals(listSize, user1.blockedContacts.size)
    }
}