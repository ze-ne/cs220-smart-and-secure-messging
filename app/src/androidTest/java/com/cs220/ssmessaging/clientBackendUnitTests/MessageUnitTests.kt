package com.cs220.ssmessaging.clientBackendUnitTests

import android.media.Image
import com.cs220.ssmessaging.clientBackend.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith

class MessageUnitTests{
    // FIX: This class was added to test the static helpers for property checking
    @Test
    fun testStaticIsValidTimeStamp(){
        assertTrue(Message.isValidTimestamp(0))
        assertTrue(Message.isValidTimestamp(1))
        assertTrue(Message.isValidTimestamp(65561521))
        assertFalse(Message.isValidTimestamp(-1))
        assertFalse(Message.isValidTimestamp(-500))
    }
}
