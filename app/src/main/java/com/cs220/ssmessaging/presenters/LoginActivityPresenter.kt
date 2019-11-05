package com.cs220.ssmessaging.presenters

class LoginActivityPresenter {
    private val view: View? = null

    fun handleLogin(username: String, password: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    interface View {
        fun showValidationErrorMsg()
        fun loginSuccessFully()
        fun loginFail()
    }
}