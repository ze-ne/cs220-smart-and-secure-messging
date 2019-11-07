package com.cs220.ssmessaging

import com.cs220.ssmessaging.presenters.ConversationsListActivityPresenter
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers

class SettingsUnitTests {
    private lateinit var presenter : SettingsActivityPresenter
    private lateinit var view : SettingsActivityPresenter.View
    private lateinit var user : User

    @Before
    fun setup() {
        presenter = SettingsActivityPresenter()
        view = mock()
        presenter.attachView(view)
        user = mock()
    }

    @Test
    fun changeFirstName_valid() {
        presenter.changeFirstName("Alex")
        assertEqual ("Alex", user.firstName)
        verify(view).changeFirstNameSuccessful()
    }

    @Test
    fun changeFirstName_invalid() {
        presenter.changeFirstName("Alex")
        presenter.changeFirstName("")
        assertEqual ("Alex", user.firstName)
        verify(view).changeFirstNameFail()
    }

    @Test
    fun changeLastName_valid() {
        presenter.changeLastName("Carter")
        assertEqual ("Carter", user.lastName)
        verify(view).changeLastNameSuccessful()
    }

    @Test
    fun changeLastName_invalid() {
        presenter.changeLastName("Carter")
        presenter.changeLastName("")
        assertEqual ("Carter", user.lastName)
        verify(view).changeLastNameFail()
    }

    fun logout_fail()
    {
        presenter.logout()
        verify(view).loginFail()
    }

    @Test
    fun logout_success()
    {
        presenter.logout()
        verify(view).logoutSuccessful()
    }
}