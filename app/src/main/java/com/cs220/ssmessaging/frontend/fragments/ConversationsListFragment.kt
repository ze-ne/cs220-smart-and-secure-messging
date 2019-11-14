package com.cs220.ssmessaging.frontend.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.frontend.presenters.ConversationsListActivityPresenter
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.cs220.ssmessaging.clientBackend.Conversation
import com.cs220.ssmessaging.clientBackend.Message
import com.cs220.ssmessaging.clientBackend.User
import com.cs220.ssmessaging.frontend.activities.ConversationActivity
import com.cs220.ssmessaging.frontend.activities.HomeActivity
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore


class ConversationsListFragment : Fragment(), ConversationsListActivityPresenter.View {
    private lateinit var conversationsRecyclerView: RecyclerView
    private lateinit var conversationsListAdapter: ConversationsListAdapter
    private lateinit var newConversationInput: EditText
    private lateinit var newConversationButton: Button
    private lateinit var currentUser: User
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        currentUser = MyApplication.currentUser!!

        val conversationsView =
            inflater.inflate(R.layout.fragment_conversations_list, container, false)
        conversationsRecyclerView = conversationsView.findViewById(R.id.conversations_recycler_list)
        conversationsRecyclerView.layoutManager = LinearLayoutManager(context)

        val activity = activity as Context
        conversationsListAdapter = ConversationsListAdapter(activity)
        conversationsRecyclerView.adapter = conversationsListAdapter

        newConversationInput = conversationsView.findViewById((R.id.add_conversation_field))
        newConversationButton = conversationsView.findViewById(R.id.new_conversation_button)

        newConversationButton.setOnClickListener {
            val participantUsername = newConversationInput.text.toString()
            if (participantUsername.isNotEmpty()) {
                FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("canonicalId", participantUsername)
                    .get()
                    .addOnSuccessListener { documentReference ->
                        if (documentReference.size() == 1) {
                            val newConvo = Conversation(currentUser.userId, participantUsername)
                            currentUser.startConversation(newConvo)
                            newConversationInput.text.clear()
                            val conversationIntent = Intent(activity, ConversationActivity::class.java)
                            conversationIntent.putExtra("receiver_name", participantUsername)
                            startActivity(conversationIntent)
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

        addConversationListener()

        return conversationsView
    }

    override fun gotoConversation(conversationID: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showIDNotFoundMessage(userID: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateConversationsList(conversationID: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun addConversationListener() {
        db.collection("conversations").whereArrayContains("users", currentUser.userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty()) {
                    for (dc: DocumentChange in snapshot.documentChanges) {
                        val document = dc.document
                        val users = document.get("users") as List<String>
                        val conversation = Conversation(users.get(0), users.get(1), ArrayList())

                        when (dc.type) {
                            DocumentChange.Type.ADDED -> currentUser.addConversation(conversation)
                            DocumentChange.Type.MODIFIED -> println("TODO")
                            DocumentChange.Type.REMOVED -> println("TODO")
                        }
                    }
                }
            }
    }

    internal inner class ConversationsListAdapter(context: Context) :
        RecyclerView.Adapter<ViewHolder>() {

        private val layoutInflater = LayoutInflater.from(context)

        fun getOtherUserId(conversation: Conversation) : String {
            if(currentUser.userId == conversation.user1Id) {
                return conversation.user2Id
            }
            return conversation.user1Id
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_conversation, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val conversation = currentUser.conversations[position]
            // TROY MODIFICATION
            val fullname = conversation.user1Id + "-" + conversation.user2Id

            viewHolder.bind(fullname)

            viewHolder.itemView.setOnClickListener {
                // TODO: call gotoConversation here instead
                val conversationIntent = Intent(context, ConversationActivity::class.java)
                conversationIntent.putExtra("receiver_name", getOtherUserId(conversation))
                startActivity(conversationIntent)
            }
        }

        override fun getItemCount() = currentUser.conversations.size
    }

    internal inner class ViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(fullname: String) {
            itemView.findViewById<TextView>(R.id.conversation_participant).text = fullname
        }
    }
}