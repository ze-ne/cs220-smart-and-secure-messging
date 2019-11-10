package com.cs220.ssmessaging.frontend.activities

import android.media.Image
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.Message
import com.cs220.ssmessaging.frontend.presenters.ConversationActivityPresenter

class ConversationActivity : AppCompatActivity(), ConversationActivityPresenter.View {
    private lateinit var conversationToolbar: Toolbar
    private lateinit var sendMessageButton: ImageButton
    private lateinit var userMessageInput: EditText
    private lateinit var conversationScrollView: ScrollView
    private lateinit var messageDisplay: TextView
    private lateinit var conversationReceiverName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        initializeFields()

        conversationReceiverName = "filler name"
        //conversationReceiverName = intent.extras!!.get("receiver_name").toString()
        Toast.makeText(this, conversationReceiverName, Toast.LENGTH_SHORT).show()
    }

    private fun initializeFields() {
        conversationToolbar = findViewById(R.id.conversation_toolbar)
        setSupportActionBar(conversationToolbar)
        supportActionBar?.title = "Contact name"

        sendMessageButton = findViewById(R.id.send_message_button)
        userMessageInput = findViewById(R.id.message_input)
        conversationScrollView = findViewById(R.id.messages_scroll_view)
        messageDisplay = findViewById(R.id.message_text_display)
    }

    override fun updateDisplayedMessages(message: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addImageToDisplay(image: Image) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}