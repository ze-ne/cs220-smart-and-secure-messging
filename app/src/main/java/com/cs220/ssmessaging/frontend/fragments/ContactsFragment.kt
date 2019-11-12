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
import com.cs220.ssmessaging.frontend.activities.ConversationActivity
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

    internal inner class ContactsAdapter(context: Context) :
        RecyclerView.Adapter<ViewHolder>() {

        private val layoutInflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ConversationsListFragment.ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_conversation, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ConversationsListFragment.ViewHolder, position: Int) {
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