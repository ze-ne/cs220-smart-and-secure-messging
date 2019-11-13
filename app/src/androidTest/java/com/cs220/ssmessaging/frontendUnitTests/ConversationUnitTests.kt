package com.cs220.ssmessaging.frontendUnitTests

import android.media.Image
import com.cs220.ssmessaging.clientBackend.ImageMessage
import com.cs220.ssmessaging.clientBackend.Message
import java.util.Date
import com.cs220.ssmessaging.clientBackend.TextMessage
import com.cs220.ssmessaging.clientBackend.User
import com.cs220.ssmessaging.frontend.presenters.ConversationActivityPresenter
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers

class ConversationUnitTests {
    private lateinit var presenter : ConversationActivityPresenter
    private lateinit var view : ConversationActivityPresenter.View
    private lateinit var sender : User
    private lateinit var reciever : User
    private var date = Date()

    @Before
    fun setup() {
        presenter = ConversationActivityPresenter()
        view = mock()
        presenter.attachView(view)
        sender = mock()
        reciever = mock()
    }

    @Test
    fun send_emptyTextMessage() {
        presenter.sendTextMessage("")
        verify(view, never()).updateDisplayedMessages(ArgumentMatchers.any(Message::class.java))
    }

    @Test
    fun send_emptyImageMessage() {
        presenter.sendImageMessage(arrayOf())
        verify(view, never()).updateDisplayedMessages(ArgumentMatchers.any(Message::class.java))
    }

    @Test
    fun send_validTextMessage() {
        presenter.sendTextMessage("Hi")
        verify(view).updateDisplayedMessages(TextMessage("Hi","1", sender, reciever, Math.toIntExact(date.time)))
    }

    @Test
    fun send_validImageMessage() {
        presenter.sendImageMessage(arrayOf())
        verify(view).updateDisplayedMessages(ImageMessage(ArgumentMatchers.any(ByteArray::class.java), "1", sender, reciever, Math.toIntExact(date.time)))
    }

    @Test
    fun send_longTextMessage() {
        presenter.sendTextMessage("Hi, this is Zene. I'm sending this really long message for testing purposes only and not for any other reason")
        verify(view).updateDisplayedMessages(TextMessage("Hi, this is Zene. I'm sending this really long message for testing purposes only and not for any other reason","1", sender, reciever, Math.toIntExact(date.time)))
    }

    @Test
    fun send_nonAlphaTextMessage() {
        presenter.sendTextMessage("Hi --- just here...\"testing\" some th1ng5! c u l8r")
        verify(view).updateDisplayedMessages(TextMessage("Hi --- just here...\"testing\" some th1ng5! c u l8r","1", sender, reciever, Math.toIntExact(date.time)))
    }

    @Test
    fun add_image() {
        presenter.addImage(ArgumentMatchers.any(Image::class.java))
        verify(view).addImageToDisplay(ArgumentMatchers.any(Image::class.java))
    }
}