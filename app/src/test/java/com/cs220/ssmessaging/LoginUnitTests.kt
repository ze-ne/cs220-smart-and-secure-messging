package com.cs220.ssmessaging
import com.cs220.ssmessaging.presenters.LoginActivityPresenter
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
        presenter.handleRegister ("newUsername")
        verify(view).registerSuccessful()
    }

    @Test
    fun register_invalidUsername ()
    {
        presenter.handleRegister ("()...")
        verify(view).registerFail()
    }

    @Test
    fun register_emptyUsername()
    {
        presenter.handleRegister("")
        verify(view).registerFail()
    }
}