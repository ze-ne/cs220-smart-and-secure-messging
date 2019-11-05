package com.cs220.ssmessaging
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

@LargeTest
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
    fun login_validLogin ()
    {
        presenter.handleLogin ("abc", "def")
        verify(view).loginSuccessful()
    }

    @Test
    fun login_invalidLogin ()
    {
        presenter.handleLogin ("abc", "defg")
        verify(view).loginFail()
    }

    @Test
    fun login_nullUsername ()
    {
        presenter.handleLogin (null, "abc")
        verify(view).loginFail()
    }

    @Test
    fun login_emptyUsername ()
    {
        presenter.handleLogin ("", "abc")
        verify(view).loginFail()
    }

    @Test
    fun login_nullPassword ()
    {
        presenter.handleLogin ("abc", null)
        verify(view).loginFail()
    }

    @Test
    fun login_emptyPassword ()
    {
        presenter.handleLogin ("abc", "")
        verify(view).loginFail()
    }
}