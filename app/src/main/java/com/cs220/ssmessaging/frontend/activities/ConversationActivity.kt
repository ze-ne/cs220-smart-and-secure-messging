package com.cs220.ssmessaging.frontend.activities


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.*
import com.cs220.ssmessaging.frontend.adapters.MessagesAdapter
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import kotlinx.android.synthetic.main.activity_conversation.*
import java.lang.Exception
import kotlin.collections.ArrayList

const val REQUEST_IMAGE_GET = 1

class ConversationActivity : AppCompatActivity() {
    private lateinit var conversationToolbar: Toolbar
    private lateinit var sendMessageButton: Button
    private lateinit var imageButton: Button
    private lateinit var userMessageInput: EditText
    private lateinit var conversationReceiverName: String
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var currentUser: User
    private lateinit var conversation: Conversation

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Refreshes conversation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.window.setFlags(FLAG_SECURE, FLAG_SECURE)
        
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
        imageButton = findViewById(R.id.add_image_button)

        sendMessageButton.setOnClickListener {
            val message = userMessageInput.text
            if (message.isNotEmpty()) {
                currentUser.sendTextMsg(message.toString(), conversation)
                userMessageInput.text.clear()
            }
        }

        imageButton.setOnClickListener {
            selectImage()
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

                // TODO("Assume all messages are encrypted!")

                // Convert server data to message objects
                val type = dc.document.data.getValue("message_type")
                val senderId = dc.document.data.getValue("sender_id") as String
                val recipientId = dc.document.data.getValue("recipient_id") as String
                val timestamp = dc.document.data.getValue("timestamp") as Long
                val recipientEncryptedAesKey = (dc.document.data.getValue("recipient_encrypted_aes_key") as Blob).toBytes()
                val senderEncryptedAesKey = (dc.document.data.getValue("sender_encrypted_aes_key") as Blob).toBytes()

                when(type) {
                    "text" -> {
                        val data = (dc.document.data.getValue("data") as Blob).toBytes()
                        val encryptedMessage = EncryptedMessage(data, convoId, "text", senderId, recipientId, timestamp, senderEncryptedAesKey, recipientEncryptedAesKey)
                        val decryptedMessage = currentUser.device.cipher.decryptEncryptedMessage(encryptedMessage)
                        currentUser.receiveMsg(decryptedMessage)
                        displayMessages()
                    }
                    "image" -> {
                        val bucketUrl = dc.document.data.getValue("bucket_url") as String
                        val imgRef = storage.getReferenceFromUrl(bucketUrl)
                        imgRef.getBytes(1000000000) // 100 MB
                            .addOnSuccessListener {
                                val encryptedMessage = EncryptedMessage(it, convoId,
                                    "image", senderId, recipientId,
                                    timestamp, senderEncryptedAesKey, recipientEncryptedAesKey)
                                val decryptedMessage = currentUser.device.cipher.decryptEncryptedMessage(encryptedMessage)
                                currentUser.receiveMsg(decryptedMessage)
                                displayMessages()
                                Log.d("addMessageListener", "Success!")
                            }
                            .addOnFailureListener {
                                Log.d("addMessageListener", "Failed to retrieve bucket img")
                            }
                    }
                    else -> throw Exception("Unknown message type")
                }
            }
        }
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?){
        super.onActivityResult(requestCode,resultCode,data)
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            Log.i("TestImage", "2")
            val fullPhotoUri: Uri = data!!.data!!
            val bitmap = ImageHandler.getImageFromStorage(fullPhotoUri)
            currentUser.sendImageMsg(ImageHandler.getByteArrayFromImage(bitmap), conversation)
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }
    }

    // Display the messages onscreen
    private fun displayMessages() {
        messagesAdapter = MessagesAdapter(this, conversation.messages as ArrayList<UnencryptedMessage>)
        message_recycler_view.scrollToPosition(conversation.messages.size - 1)
        message_recycler_view.adapter = messagesAdapter
    }

}
