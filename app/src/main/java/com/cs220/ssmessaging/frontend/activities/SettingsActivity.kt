package com.cs220.ssmessaging.frontend.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.frontend.presenters.SettingsActivityPresenter

class SettingsActivity : AppCompatActivity(), SettingsActivityPresenter.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    override fun logoutSuccessful() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun logoutFail() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun changeFirstNameSuccessful() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun changeFirstNameFail() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun changeLastNameSuccessful() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun changeLastNameFail() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}