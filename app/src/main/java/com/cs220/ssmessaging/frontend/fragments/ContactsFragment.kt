package com.cs220.ssmessaging.frontend.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.frontend.presenters.ContactsActivityPresenter

class ContactsFragment : Fragment(), ContactsActivityPresenter.View {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false)
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