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
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.cs220.ssmessaging.clientBackend.User
import com.cs220.ssmessaging.frontend.activities.ConversationActivity


class ConversationsListFragment : Fragment(), ConversationsListActivityPresenter.View {
    private lateinit var conversationsRecyclerView: RecyclerView
    private lateinit var conversationsListAdapter: ConversationsListAdapter
    private lateinit var currentUser: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // TODO: Get current user from login screen
        currentUser = MyApplication.currentUser!!

        val conversationsView =
            inflater.inflate(R.layout.fragment_conversations_list, container, false)
        conversationsRecyclerView = conversationsView.findViewById(R.id.conversations_recycler_list)
        conversationsRecyclerView.layoutManager = LinearLayoutManager(context)

        val activity = activity as Context
        conversationsListAdapter = ConversationsListAdapter(activity)
        conversationsRecyclerView.adapter = conversationsListAdapter

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

    internal inner class ConversationsListAdapter(context: Context) :
        RecyclerView.Adapter<ViewHolder>() {

        private val layoutInflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_conversation, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val conversation = currentUser.conversations[position]
            val fullname = conversation.user2.firstName + " " + conversation.user2.lastName

            viewHolder.setData(fullname)

            viewHolder.itemView.setOnClickListener {
                // TODO: call gotoConversation here instead
                val conversationIntent = Intent(context, ConversationActivity::class.java)
                conversationIntent.putExtra("receiver_name", fullname)
                startActivity(conversationIntent)
            }
        }

        override fun getItemCount() = currentUser.conversations.size
    }

    internal inner class ViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun setData(fullname: String) {
            itemView.findViewById<TextView>(R.id.conversation_participant).text = fullname
        }
    }
}