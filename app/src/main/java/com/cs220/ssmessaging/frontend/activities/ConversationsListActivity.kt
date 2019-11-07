package com.cs220.ssmessaging.frontend.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.frontend.presenters.ConversationsListActivityPresenter

class ConversationsListActivity : AppCompatActivity(), ConversationsListActivityPresenter.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversations_list)
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