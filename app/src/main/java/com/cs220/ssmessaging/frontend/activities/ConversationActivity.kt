package com.cs220.ssmessaging.frontend.activities


import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.*
import com.cs220.ssmessaging.frontend.adapters.MessagesAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentChange

import kotlinx.android.synthetic.main.activity_conversation.*
import kotlin.collections.ArrayList


class ConversationActivity : AppCompatActivity() {
    private lateinit var conversationToolbar: Toolbar
    private lateinit var sendMessageButton: Button
    private lateinit var userMessageInput: EditText
    private lateinit var conversationReceiverName: String
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var currentUser: User
    private lateinit var conversation: Conversation

    private val db = FirebaseFirestore.getInstance()

    // Refreshes conversation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        conversationReceiverName = intent.extras!!.get("receiver_name").toString()

        message_recycler_view.layoutManager = LinearLayoutManager(this)

        currentUser = MyApplication.currentUser!!

        conversationToolbar = findViewById(R.id.conversation_toolbar)
        setSupportActionBar(conversationToolbar)
        supportActionBar?.title = conversationReceiverName

        conversation = currentUser.getConversationByUserId(conversationReceiverName)!!
        addMessageListener(conversation.convoId)

        sendMessageButton = findViewById(R.id.send_message_button)
        userMessageInput = findViewById(R.id.message_input)

        sendMessageButton.setOnClickListener {
            val message = userMessageInput.text
            if (message.isNotEmpty()) {
                currentUser.sendTextMsg(message.toString(), conversation)
                userMessageInput.text.clear()
            }
        }
    }

    // Adds listeners to check for new messages within a given conversation
    private fun addMessageListener(convoId: String) {

        // Query messages within target conversation, sorted in chronological order
        val msgRef = db.collection("conversations").document(convoId)
        val msgCollection = msgRef.collection("messages").orderBy("timestamp")
        msgCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            // Add messages to local backend objects
            for (dc in snapshot!!.documentChanges) {

                // Convert server data to message objects
                val decryptedMessage = TextMessage(
                    (dc.document.data.getValue("data") as String),
                    convoId,
                    dc.document.data.getValue("sender_id") as String,
                    dc.document.data.getValue("recipient_id") as String,
                    dc.document.data.getValue("timestamp") as Long
                )
                currentUser.receiveMsg(decryptedMessage)
            }
            displayMessages()
        }

    }

    // Display the messages onscreen
    private fun displayMessages() {
        messagesAdapter = MessagesAdapter(this, conversation.messages as ArrayList<TextMessage>)
        message_recycler_view.scrollToPosition(conversation.messages.size - 1)
        message_recycler_view.adapter = messagesAdapter
    }

}
