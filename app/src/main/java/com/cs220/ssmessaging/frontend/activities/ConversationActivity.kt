package com.cs220.ssmessaging.frontend.activities


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.*
import com.cs220.ssmessaging.frontend.adapters.MessagesAdapter
import com.cs220.ssmessaging.frontend.fragments.SetTimerDialog
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_conversation.*
import java.lang.Exception
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.switch_item.view.*

const val REQUEST_IMAGE_GET = 1
const val REQUEST_HIDDEN_IMAGE_GET = 2

class ConversationActivity : AppCompatActivity() {
    private lateinit var conversationToolbar: Toolbar
    private lateinit var sendMessageButton: ImageButton
    private lateinit var sendHiddenMessageButton: ImageButton
    private lateinit var imageButton: ImageButton
    private lateinit var hiddenImageButton: ImageButton
    private lateinit var userMessageInput: EditText
    private lateinit var conversationReceiverName: String
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var currentUser: User
    private lateinit var conversation: Conversation

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var timedDeletion: Boolean = false

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
        sendHiddenMessageButton = findViewById(R.id.send_hidden_text_button)

        userMessageInput = findViewById(R.id.message_input)
        imageButton = findViewById(R.id.add_image_button)
        hiddenImageButton = findViewById(R.id.add_hidden_image_button)

        sendMessageButton.setOnClickListener {
            onSendTextMessage(true)
        }

        sendHiddenMessageButton.setOnClickListener {
            onSendTextMessage(false)
        }

        imageButton.setOnClickListener {
            selectImage()
        }

        hiddenImageButton.setOnClickListener {
            selectHiddenImage()
        }
    }

    fun onSendTextMessage(isVisible: Boolean) {
        val message = userMessageInput.text
        if (message.isEmpty())
            return
        if (this.timedDeletion) {
            val setTimerFragmentDialog = SetTimerDialog(
                message.toString(),
                null,
                false,
                conversation,
                isVisible
            )
            setTimerFragmentDialog.show(supportFragmentManager, "SetTimerDialog")
            userMessageInput.text.clear()
        } else {
            currentUser.sendTextMsg(message.toString(), conversation, isVisible = isVisible)
            userMessageInput.text.clear()
        }
    }

    val analyticsCallback = {
        runOnUiThread {
            // Stuff that updates the UI
            messagesAdapter.display = true
            messagesAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.conversation_options_menu, menu)

        val deletionSwitch: Switch =
            menu!!.findItem(R.id.deletion_switch).actionView.switchForActionBar

        deletionSwitch.setOnCheckedChangeListener { _, isChecked -> timedDeletion = isChecked }

        val sentimentsSwitch: Switch =
            menu!!.findItem(R.id.analytics_switch).actionView.switchForActionBar
        sentimentsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                conversation.getGeneralTone { str ->
                    Log.d("toneSwitch", str)
                    runOnUiThread {
                        Toast.makeText(this@ConversationActivity, str, Toast.LENGTH_LONG).show()
                    }
                }
                if (!conversation.getAnalytics(analyticsCallback)) {
                    messagesAdapter.display = true
                }
            } else {
                messagesAdapter.display = false
            }
            messagesAdapter.notifyDataSetChanged()
        }

        return true
    }

    private fun addMessage(dc: DocumentChange, convoId: String) {
        // TODO("Assume all messages are encrypted!")

        // Convert server data to message objects
        val type = dc.document.data.getValue("message_type")
        val senderId = dc.document.data.getValue("sender_id") as String
        val recipientId = dc.document.data.getValue("recipient_id") as String
        val timestamp = dc.document.data.getValue("timestamp") as Long
        val recipientEncryptedAesKey =
            (dc.document.data.getValue("recipient_encrypted_aes_key") as Blob).toBytes()
        val senderEncryptedAesKey =
            (dc.document.data.getValue("sender_encrypted_aes_key") as Blob).toBytes()

        when (type) {
            "text" -> {
                val data = (dc.document.data.getValue("data") as Blob).toBytes()
                val encryptedMessage = EncryptedMessage(
                    data,
                    convoId,
                    "text",
                    senderId,
                    recipientId,
                    timestamp,
                    senderEncryptedAesKey,
                    recipientEncryptedAesKey
                )
                val decryptedMessage =
                    currentUser.device.cipher.decryptEncryptedMessage(encryptedMessage)

                val isVisible = dc.document.data.get("is_visible")
                val deletionTimer = dc.document.data.get("deletion_timer")
                if (isVisible != null) {
                    decryptedMessage.isVisible = isVisible as Boolean
                }
                if (deletionTimer != null) {
                    decryptedMessage.deletionTimer = deletionTimer as Long
                }

                currentUser.receiveMsg(decryptedMessage)
                displayMessages()
            }
            "image" -> {
                val bucketUrl = dc.document.data.getValue("bucket_url") as String
                val imgRef = storage.getReferenceFromUrl(bucketUrl)
                imgRef.getBytes(1000000000) // 100 MB
                    .addOnSuccessListener {
                        val encryptedMessage = EncryptedMessage(
                            it, convoId,
                            "image", senderId, recipientId,
                            timestamp, senderEncryptedAesKey, recipientEncryptedAesKey
                        )
                        val decryptedMessage =
                            currentUser.device.cipher.decryptEncryptedMessage(encryptedMessage)
                        val isVisible = dc.document.data.get("is_visible")
                        val deletionTimer = dc.document.data.get("deletion_timer")
                        if (isVisible != null) {
                            decryptedMessage.isVisible = isVisible as Boolean
                        }
                        if (deletionTimer != null) {
                            decryptedMessage.deletionTimer = deletionTimer as Long
                        }
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

    private fun deleteMessage(dc: DocumentChange, convoId: String) {

        val type = dc.document.data.getValue("message_type")
        val senderId = dc.document.data.getValue("sender_id") as String
        val recipientId = dc.document.data.getValue("recipient_id") as String
        val timestamp = dc.document.data.getValue("timestamp") as Long
        val fillerText = "filler_text"

        when (type) {
            "text" -> {
                val textMessage =
                    TextMessage("filler_text", convoId, senderId, recipientId, timestamp)
                currentUser.deleteSentMessage(textMessage)


            }
            "image" -> {
                val imageMessage =
                    ImageMessage(
                        fillerText.toByteArray(),
                        convoId,
                        senderId,
                        recipientId,
                        timestamp
                    )
                currentUser.deleteSentMessage(imageMessage)
            }
            else -> throw Exception("Unknown message type")
        }
    }

    // Adds listeners to check for new messages within a given conversation
    private fun addMessageListener(convoId: String) {

        // Query messages within target conversation, sorted in chronological order
        val msgRef = db.collection("conversations").document(convoId)
        val msgCollection = msgRef.collection("messages").orderBy("timestamp")
        val messsageListener = msgCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            // Add messages to local backend objects
            for (dc in snapshot!!.documentChanges) {

                when (dc.type) {

                    DocumentChange.Type.ADDED -> addMessage(dc, convoId)
                    DocumentChange.Type.REMOVED -> deleteMessage(dc, convoId)
                    else -> println("====== neither added nor removed =======")

                }
            }
            displayMessages()
        }
        currentUser.addListener(messsageListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_IMAGE_GET || requestCode == REQUEST_HIDDEN_IMAGE_GET) && resultCode == Activity.RESULT_OK) {
            Log.i("TestImage", "2")
            val fullPhotoUri: Uri = data!!.data!!
            val bitmap = ImageHandler.getImageFromStorage(fullPhotoUri)
            val hidden = requestCode == REQUEST_IMAGE_GET
            if (this.timedDeletion) {
                val setTimerFragmentDialog = SetTimerDialog(
                    "",
                    bitmap!!,
                    true,
                    conversation,
                    isMsgVisible = hidden
                )
                setTimerFragmentDialog.show(supportFragmentManager, "SetTimerDialog")
            } else {
                currentUser.sendImageMsg(
                    ImageHandler.getByteArrayFromImage(bitmap),
                    conversation,
                    isVisible = hidden
                )
            }
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

    private fun selectHiddenImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_HIDDEN_IMAGE_GET)
        }
    }

    // Display the messages onscreen
    private fun displayMessages() {
        messagesAdapter =
            MessagesAdapter(
                this,
                conversation.messages as ArrayList<UnencryptedMessage>,
                conversation.convoId,
                false
            )
        scrollToBottom()
        message_recycler_view.adapter = messagesAdapter
    }

    fun scrollToBottom() {
        message_recycler_view.scrollToPosition(conversation.messages.size - 1)
    }
}
