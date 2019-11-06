package com.cs220.ssmessaging

import com.cs220.ssmessaging.presenters.ConversationActivityPresenter
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers

class ConversationUnitTests {
    private lateinit var presenter : ConversationActivityPresenter
    private lateinit var view : ConversationActivityPresenter.View

    @Before
    fun setup() {
        presenter = ConversationActivityPresenter()
        view = mock()
        presenter.attachView(view)
    }

    // TODO fix when merge in backend branch

    /*@Test
    fun send_emptyTextMessage() {
        presenter.sendTextMessage("")
        verify(view, never()).updateDisplayedMessages()
    }

    @Test
    fun send_emptyImageMessage() {
        presenter.sendImageMessage(arrayOf())
        verify(view, never()).updateDisplayedMessages()
    }

    @Test
    fun send_validTextMessage() {
        presenter.sendTextMessage("Hi")
        verify(view).updateDisplayedMessages()
    }

    @Test
    fun send_validImageMessage() {
        presenter.sendImageMessage(arrayOf())
        verify(view).updateDisplayedMessages()
    }

    @Test
    fun send_longTextMessage() {
        presenter.sendTextMessage("Hi, this is Zene. I'm sending this really long message for testing purposes only and not for any other reason")
        verify(view).updateDisplayedMessages()
    }

    @Test
    fun send_nonAlphaTextMessage() {
        presenter.sendTextMessage("Hi --- just here...\"testing\" some th1ng5! c u l8r")
        verify(view).updateDisplayedMessages()
    }*/

}