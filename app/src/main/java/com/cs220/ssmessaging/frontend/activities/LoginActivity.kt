package com.cs220.ssmessaging.frontend.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.frontend.presenters.LoginActivityPresenter

class LoginActivity : AppCompatActivity(), LoginActivityPresenter.View {
    private val presenter: LoginActivityPresenter = LoginActivityPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun loginSuccessful() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loginFail() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerFail() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerSuccessful() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
