package com.cs220.ssmessaging.frontend.activities

import android.media.Image
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.Conversation
import com.cs220.ssmessaging.clientBackend.Message
import com.cs220.ssmessaging.clientBackend.TextMessage
import com.cs220.ssmessaging.clientBackend.User
import com.cs220.ssmessaging.frontend.adapters.MessagesAdapter
import com.cs220.ssmessaging.frontend.presenters.ConversationActivityPresenter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_conversation.*
import java.util.*

class ConversationActivity : AppCompatActivity(), ConversationActivityPresenter.View {
    private lateinit var conversationToolbar: Toolbar
    private lateinit var sendMessageButton: ImageButton
    private lateinit var userMessageInput: EditText
    private lateinit var conversationReceiverName: String
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var currentUser: User
    private lateinit var messageParticipant: User
    private lateinit var conversationId: String
    private lateinit var conversation: Conversation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        messageList.layoutManager = LinearLayoutManager(this)
        messagesAdapter = MessagesAdapter(this)
        messageList.adapter = messagesAdapter

        currentUser = MyApplication.currentUser!!

        conversationToolbar = findViewById(R.id.conversation_toolbar)
        setSupportActionBar(conversationToolbar)
        supportActionBar?.title = "Contact name"

        sendMessageButton = findViewById(R.id.send_message_button)
        userMessageInput = findViewById(R.id.message_input)

        //conversationReceiverName = "filler name"
        conversationReceiverName = intent.extras!!.get("receiver_name").toString()
        getRecipient()
        conversationId = currentUser.userId + "-" + messageParticipant.userId
        getConversation()

        sendMessageButton.setOnClickListener {
            val message = userMessageInput.text
            // TROY MODIFICAITON
            if (message.isNotEmpty()) {
                val newMessage = TextMessage(
                    message.toString(),
                    conversationId,
                    currentUser.userId,
                    messageParticipant.userId,
                    Calendar.getInstance().timeInMillis.toInt()
                )
                conversation.addMessage(newMessage)
            }
        }
    }

    private fun getRecipient() {
        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("canonicalId", conversationReceiverName)
            .get()
            .addOnSuccessListener { documentReference ->
                val users = documentReference.toObjects(User::class.java)
                if (users.size == 1) {
                    messageParticipant = users[0]
                } else {
                    Toast.makeText(
                        this,
                        "Invalid user in conversation.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }

    }

    private fun getConversation() {
        FirebaseFirestore.getInstance().collection("conversations")
            .whereEqualTo("canonicalId", conversationId)
            .get()
            .addOnSuccessListener { documentReference ->
                val conversations = documentReference.toObjects(Conversation::class.java)
                if (conversations.size == 1) {
                    conversation = conversations[0]
                } else {
                    Toast.makeText(
                        this,
                        "Invalid conversation.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }

    }

    override fun updateDisplayedMessages(message: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addImageToDisplay(image: Image) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}