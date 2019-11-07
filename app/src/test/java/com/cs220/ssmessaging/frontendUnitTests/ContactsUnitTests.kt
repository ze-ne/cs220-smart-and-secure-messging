package com.cs220.ssmessaging.frontendUnitTests

import com.cs220.ssmessaging.frontend.presenters.ContactsActivityPresenter
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers

class ContactsUnitTests {
    private lateinit var presenter : ContactsActivityPresenter
    private lateinit var view : ContactsActivityPresenter.View

    @Before
    fun setup() {
        presenter = ContactsActivityPresenter()
        view = mock()
        presenter.attachView(view)
    }

    @Test
    fun add_withEmptyId_doesNothing() {
        presenter.addContact("")
        verify(view, never()).showConfirmationMessage()
        verify(view, never()).showErrorMessage()
        verify(view, never()).updateContactsList(ArgumentMatchers.anyString())
    }

    @Test
    fun add_withValidId_callsShowConfirmationMessage() {
        presenter.addContact("john")
        verify(view).showConfirmationMessage()
        verify(view).updateContactsList("john")
    }

    @Test
    fun add_withInvalidId_callsShowErrorMessage() {
        presenter.addContact("bob")
        verify(view).showErrorMessage()
        verify(view, never()).updateContactsList(ArgumentMatchers.anyString())
    }

    @Test
    fun add_cannotComplete_callsShowErrorMessage() {
        presenter.addContact("bob")
        verify(view).showErrorMessage()
        verify(view, never()).updateContactsList(ArgumentMatchers.anyString())
    }
}