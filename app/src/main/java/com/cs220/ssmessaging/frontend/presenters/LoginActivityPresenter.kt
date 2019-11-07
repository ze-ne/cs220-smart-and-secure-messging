package com.cs220.ssmessaging.frontend.presenters

class LoginActivityPresenter : BasePresenter<LoginActivityPresenter.View>() {

    // NOTE: We test for empty string here, any actual string gets sent to backend for testing
    fun handleLogin(username: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // NOTE: We test for empty string here, any actual string gets sent to backend for testing
    fun handleRegister(username: String, firstname: String, lastname: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    interface View {
        fun loginFail()
        fun loginSuccessful()
        fun registerFail()
        fun registerSuccessful()
    }
}