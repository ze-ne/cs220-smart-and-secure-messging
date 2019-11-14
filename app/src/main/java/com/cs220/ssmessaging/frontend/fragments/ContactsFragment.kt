package com.cs220.ssmessaging.frontend.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.User
import com.cs220.ssmessaging.frontend.activities.ConversationActivity
import com.cs220.ssmessaging.frontend.presenters.ContactsActivityPresenter
import com.google.firebase.firestore.FirebaseFirestore

class ContactsFragment : Fragment(), ContactsActivityPresenter.View {

    private lateinit var currentUser: User


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        // TODO: Get current user from login screen
        currentUser = MyApplication.currentUser!!
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun showConfirmationMessage() {
        Toast.makeText(activity, "Confirmation Message", Toast.LENGTH_SHORT).show()

        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showErrorMessage() {
        Toast.makeText(activity, "Error Message", Toast.LENGTH_SHORT).show()

        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateContactsList(userID: String) {
//        val db = FirebaseFirestore.getInstance()
//        db.collection("users").whereEqualTo("canonicalId", userID)
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                // Toast.makeText(this, "Added Contact", Toast.LENGTH_SHORT).show()
//                if (querySnapshot.documents.size >= 1) {
//                    val user = querySnapshot.documents.get(0)
//                }
//                boo = currentUser.addContact(newcontact)
//            }
//            .addOnFailureListener { querySnapshot ->
//                // Toast.makeText(this, "Failed to Add Contact", Toast.LENGTH_SHORT).show()
//            }
        currentUser.addContact(userID)

//        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    internal inner class ContactsAdapter(context: Context) :
        RecyclerView.Adapter<ViewHolder>() {

        private val layoutInflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ContactsFragment.ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_contact, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ContactsFragment.ViewHolder, position: Int) {
//            val contacts = currentUser.contacts[position]
//            val contactName = contacts.firstName + " " + contacts.lastName

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