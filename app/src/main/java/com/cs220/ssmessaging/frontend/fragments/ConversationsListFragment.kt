package com.cs220.ssmessaging.frontend.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.Conversation
import com.cs220.ssmessaging.frontend.presenters.ConversationsListActivityPresenter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class ConversationsListFragment : Fragment(), ConversationsListActivityPresenter.View {
    private lateinit var conversationsRecyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var myAuth: FirebaseAuth
    private lateinit var currentUserId: String

    // TODO: user/conversation fields need to match firebase fields for updates I believe


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val conversationsView = inflater.inflate(R.layout.fragment_conversations_list, container, false)
        conversationsRecyclerView = conversationsView.findViewById(R.id.conversations_recycler_list)
        conversationsRecyclerView.layoutManager = LinearLayoutManager(context)

        myAuth = FirebaseAuth.getInstance()
        currentUserId = myAuth.currentUser!!.uid
        databaseReference = FirebaseDatabase.getInstance().reference.child("Conversations").child(currentUserId)

        return conversationsView
    }

    override fun onStart() {
        super.onStart()

        val options = FirebaseRecyclerOptions.Builder<Conversation>()
            .setQuery(databaseReference, Conversation::class.java)
            .build()

        val adapter = object : FirebaseRecyclerAdapter<Conversation, ViewHolder>(options) {
            override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Conversation) {
                // TODO: who is user1 and who is user2
                val fullname = model.user2.firstName + model.user2.lastName
                holder.fullname.text = fullname
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_conversation, parent, false)
                return ViewHolder(view)
            }
        }

        conversationsRecyclerView.adapter = adapter
        adapter.startListening()
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

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var fullname: TextView = v.findViewById(R.id.conversation_participant) as TextView

    }
}