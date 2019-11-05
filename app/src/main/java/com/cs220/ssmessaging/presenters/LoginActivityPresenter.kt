package com.cs220.ssmessaging.presenters

class LoginActivityPresenter : BasePresenter<LoginActivityPresenter.View>() {

    fun handleLogin(username: String, password: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    interface View {
        fun loginFail()
        fun loginSuccessful()
    }
}