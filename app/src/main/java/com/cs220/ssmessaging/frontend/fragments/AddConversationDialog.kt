package com.cs220.ssmessaging.frontend.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.cs220.ssmessaging.MyApplication.MyApplication.Companion.currentUser
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.Conversation
import com.cs220.ssmessaging.clientBackend.User
import com.cs220.ssmessaging.frontend.activities.ConversationActivity
import com.google.firebase.firestore.FirebaseFirestore

class AddConversationDialog : DialogFragment() {
    private lateinit var currentUser: User
    private lateinit var newConversationInput: EditText
    private lateinit var newConversationButton: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        currentUser = MyApplication.currentUser!!

        val conversationsView =
            inflater.inflate(R.layout.dialog_add_conversation, container, false)

        newConversationInput = conversationsView.findViewById(R.id.dialog_add_conversation_field)
        newConversationButton = conversationsView.findViewById(R.id.dialog_new_conversation_button)

        newConversationButton.setOnClickListener {
            val participantUsername = newConversationInput.text.toString()
            var stop = false
            for(c in currentUser.conversations){
                if(c.user1Id == participantUsername || c.user2Id == participantUsername){
                    Toast.makeText(
                        activity,
                        "Unable to start conversation. You already have a conversation with $participantUsername",
                        Toast.LENGTH_LONG
                    ).show()
                    stop = true
                    break
                }
            }
            if (currentUser.checkIfInBlockList(participantUsername) && !stop) {
                Toast.makeText(
                    activity,
                    "Unable to start conversation. $participantUsername has been blocked",
                    Toast.LENGTH_LONG
                ).show()
            } else if (participantUsername.isNotEmpty() && !stop) {
                FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("canonicalId", participantUsername)
                    .get()
                    .addOnSuccessListener { documentReference ->
                        if (documentReference.size() == 1) {
                            val newConvo = Conversation(currentUser.userId, participantUsername)
                            // Check if you're in the other user's block list
                            val blockList: MutableList<String>? =
                                documentReference.documents[0].get("block_list") as MutableList<String>?

                            // Only start new conversation if the other person's blockList does not exist or you are not in it
                            if (blockList == null || !blockList.contains(currentUser.userId) || currentUser.blockedContacts.contains(
                                    participantUsername
                                )
                            ) {
                                currentUser.startConversation(newConvo)
                                newConversationInput.text.clear()
                                val conversationIntent =
                                    Intent(activity, ConversationActivity::class.java)
                                conversationIntent.putExtra("receiver_name", participantUsername)
                                startActivity(conversationIntent)
                                this.dismiss()
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Unable to start conversation. You have been blocked by $participantUsername",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        } else {
                            Toast.makeText(
                                activity,
                                "User could not be found. Check the username and try again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(activity, exception.message, Toast.LENGTH_SHORT).show()
                    }
            }
        }
        return conversationsView
    }
}