package com.cs220.ssmessaging.frontend.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.frontend.presenters.ContactsActivityPresenter

class ContactsActivity : AppCompatActivity(), ContactsActivityPresenter.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
    }

    override fun showConfirmationMessage() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showErrorMessage() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateContactsList(userID: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}