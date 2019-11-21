package com.cs220.ssmessaging.frontend.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.User
import com.google.firebase.firestore.FirebaseFirestore

class AddContactDialog : DialogFragment() {
    private lateinit var newContactInput: EditText
    private lateinit var newContactButton: TextView
    private lateinit var currentUser: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        currentUser = MyApplication.currentUser!!

        val contactsView =
            inflater.inflate(R.layout.dialog_add_contact, container, false)

        newContactInput = contactsView.findViewById((R.id.dialog_add_contact_field))
        newContactButton = contactsView.findViewById(R.id.dialog_add_contact_button)

        newContactButton.setOnClickListener {
            val participantUsername = newContactInput.text.toString()
            if (participantUsername.isNotEmpty()) {
                FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("canonicalId", participantUsername)
                    .get()
                    .addOnSuccessListener { documentReference ->
                        if (documentReference.size() == 1) {
                            currentUser.addContact(participantUsername)
                        } else {
                            Toast.makeText(
                                activity,
                                "User could not be found. Check the username and try again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        this.dismiss()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(activity, exception.message, Toast.LENGTH_SHORT).show()
                    }
            }
        }
        return contactsView
    }
}
