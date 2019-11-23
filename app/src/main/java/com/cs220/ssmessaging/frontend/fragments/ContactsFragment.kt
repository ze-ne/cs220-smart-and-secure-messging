package com.cs220.ssmessaging.frontend.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class ContactsFragment : Fragment() {

    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var contactsListAdapter: ContactsListAdapter
    private lateinit var searchContactInput: EditText
    private lateinit var searchContactButton: Button
    private lateinit var newContactButton: FloatingActionButton
    private lateinit var currentUser: User
    private val db = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentUser = MyApplication.currentUser!!

        val contactsView =
            inflater.inflate(R.layout.fragment_contacts, container, false)
        contactsRecyclerView = contactsView.findViewById(R.id.contactsRecyclerList)
        contactsRecyclerView.layoutManager = LinearLayoutManager(context)

        val activity = activity as Context
        contactsListAdapter = ContactsListAdapter(activity)
        contactsRecyclerView.adapter = contactsListAdapter

        searchContactInput = contactsView.findViewById(R.id.search_contacts_field)
        searchContactButton = contactsView.findViewById(R.id.search_contacts_button)
        newContactButton = contactsView.findViewById(R.id.new_contact_button)

        searchContactButton.setOnClickListener {
            // TODO
        }

        newContactButton.setOnClickListener {
            val addContactDialog = AddContactDialog()
            addContactDialog.show(fragmentManager!!, "AddContactDialog")
        }

        return contactsView
    }


    internal inner class ContactsListAdapter(context: Context) :
        RecyclerView.Adapter<ViewHolder>() {

        private val layoutInflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(
            viewGroup: ViewGroup,
            position: Int
        ): ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_contact, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val contactUserId: String = currentUser.contacts[position]

            viewHolder.setData(contactUserId)
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