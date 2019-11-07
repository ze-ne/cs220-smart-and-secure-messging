package com.cs220.ssmessaging.frontendUnitTests
import com.cs220.ssmessaging.frontend.presenters.LoginActivityPresenter
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test

class LoginUnitTests {
    private lateinit var presenter: LoginActivityPresenter
    private lateinit var view: LoginActivityPresenter.View

    @Before
    fun setup ()
    {
        presenter = LoginActivityPresenter()
        view = mock()
        presenter.attachView (view)
    }

    @Test
    fun login_validLogin()
    {
        presenter.handleLogin ("myUsername")
        verify(view).loginSuccessful()
    }

    @Test
    fun login_invalidLogin()
    {
        presenter.handleLogin ("wrongUsername")
        verify(view).loginFail()
    }

    @Test
    fun login_emptyUsername()
    {
        presenter.handleLogin ("")
        verify(view).loginFail()
    }

    @Test
    fun register_validUsername()
    {
        presenter.handleRegister ("newUsername", "John", "Smith")
        verify(view).registerSuccessful()
    }

    @Test
    fun register_invalidUsername ()
    {
        presenter.handleRegister ("()...", "John", "Smith")
        verify(view).registerFail()
    }

    @Test
    fun register_invalidFirstname()
    {
        presenter.handleRegister("newUsername", "...", "Smith")
        verify(view).registerFail()
    }

    @Test
    fun register_invalidLastname()
    {
        presenter.handleRegister("newUsername", "John", "...")
        verify(view).registerFail()
    }

    @Test
    fun register_emptyUsername()
    {
        presenter.handleRegister("", "John", "Smith")
        verify(view).registerFail()
    }

    @Test
    fun register_emptyFirstname()
    {
        presenter.handleRegister("newUsername", "", "Smith")
        verify(view).registerFail()
    }

    @Test
    fun register_emptyLastname()
    {
        presenter.handleRegister("newUsername", "John", "")
        verify(view).registerFail()
    }
}