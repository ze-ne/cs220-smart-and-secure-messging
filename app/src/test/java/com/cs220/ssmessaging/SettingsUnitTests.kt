package com.cs220.ssmessaging

import com.cs220.ssmessaging.frontend.presenters.ConversationsListActivityPresenter
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers

class ConversationsListUnitTests {
    private lateinit var presenter : ConversationsListActivityPresenter
    private lateinit var view : ConversationsListActivityPresenter.View

    @Before
    fun setup() {
        presenter = ConversationsListActivityPresenter()
        view = mock()
        presenter.attachView(view)
    }

    @Test
    fun open_conversationThatAlreadyExists() {
        verify(view).gotoConversation("alex")
    }

    @Test
    fun create_newConversation_withValidUserID() {
        presenter.startNewConversation("john")
        verify(view).gotoConversation("johnConvo")
        verify(view).updateConversationsList("johnConvo")
    }

    @Test
    fun create_newConversation_withInvalidUserID() {
        presenter.startNewConversation("bob")
        verify(view, never()).gotoConversation(ArgumentMatchers.anyString())
        verify(view).showIDNotFoundMessage("bob")
    }
}