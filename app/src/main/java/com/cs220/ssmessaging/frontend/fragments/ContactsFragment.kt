package com.cs220.ssmessaging.frontend.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.User
import com.cs220.ssmessaging.frontend.activities.ConversationActivity
import com.cs220.ssmessaging.frontend.presenters.ContactsActivityPresenter

class ContactsFragment : Fragment(), ContactsActivityPresenter.View {

    private lateinit var currentUser: User


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        // TODO: Get current user from login screen
        currentUser = User("johndoe", "John", "Doe")
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

    internal inner class ContactsAdapter(context: Context) :
        RecyclerView.Adapter<ViewHolder>() {

        private val layoutInflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ContactsFragment.ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_contact, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ContactsFragment.ViewHolder, position: Int) {
            val contacts = currentUser.contacts[position]
            val contactName = contacts.firstName + " " + contacts.lastName

            viewHolder.setData(contactName)
        }

        override fun getItemCount() = currentUser.contacts.size
    }

    internal inner class ViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun setData(contactName: String) {
            itemView.findViewById<TextView>(R.id.contact).text = contactName
        }
    }
}