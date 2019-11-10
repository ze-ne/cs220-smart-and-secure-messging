package com.cs220.ssmessaging.frontend.activities

import android.media.Image
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.Message
import com.cs220.ssmessaging.frontend.presenters.ConversationActivityPresenter

class ConversationActivity : AppCompatActivity(), ConversationActivityPresenter.View {
    private lateinit var conversationReceiverName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        conversationReceiverName = intent.extras!!.get("receiver_name").toString()
        Toast.makeText(this, conversationReceiverName, Toast.LENGTH_SHORT).show()
    }

    override fun updateDisplayedMessages(message: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addImageToDisplay(image: Image) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}