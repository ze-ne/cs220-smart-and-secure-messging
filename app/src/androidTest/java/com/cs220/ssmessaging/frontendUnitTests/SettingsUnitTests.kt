package com.cs220.ssmessaging.frontendUnitTests

import com.cs220.ssmessaging.clientBackend.User
import com.cs220.ssmessaging.frontend.presenters.SettingsActivityPresenter
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

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
        assertEquals("Alex", user.firstName)
        verify(view).changeFirstNameSuccessful()
    }

    @Test
    fun changeFirstName_invalid() {
        presenter.changeFirstName("Alex")
        presenter.changeFirstName("")
        assertEquals("Alex", user.firstName)
        verify(view).changeFirstNameFail()
    }

    @Test
    fun changeLastName_valid() {
        presenter.changeLastName("Carter")
        assertEquals("Carter", user.lastName)
        verify(view).changeLastNameSuccessful()
    }

    @Test
    fun changeLastName_invalid() {
        presenter.changeLastName("Carter")
        presenter.changeLastName("")
        assertEquals("Carter", user.lastName)
        verify(view).changeLastNameFail()
    }

    @Test
    fun logout_fail() {
        presenter.logout()
        verify(view).logoutFail()
    }

    @Test
    fun logout_success() {
        presenter.logout()
        verify(view).logoutSuccessful()
    }
}