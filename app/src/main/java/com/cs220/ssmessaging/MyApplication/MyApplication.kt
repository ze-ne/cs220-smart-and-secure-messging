package com.cs220.ssmessaging.MyApplication

import android.app.Application
import android.content.Context
import com.cs220.ssmessaging.clientBackend.User


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        var appContext: Context? = null
            private set

        var currentUser : User? = null
    }
}