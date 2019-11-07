package com.cs220.ssmessaging.frontend.activities

import android.media.Image
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.Message
import com.cs220.ssmessaging.frontend.presenters.ConversationActivityPresenter

class ConversationActivity : AppCompatActivity(), ConversationActivityPresenter.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)
    }

    override fun updateDisplayedMessages(message: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addImageToDisplay(image: Image) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}