package com.cs220.ssmessaging.frontend.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.frontend.presenters.ConversationsListActivityPresenter

class ConversationsListFragment : Fragment(), ConversationsListActivityPresenter.View {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversations_list, container, false)
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
}