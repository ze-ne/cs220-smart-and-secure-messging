package com.cs220.ssmessaging.clientBackend

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.time.Instant
import java.util.*


const val TAG = "User"

class User() {
    /* For TAs: all accesses (except for the constructor) in Kotlin must go through the getter and setter.
     * This is because variables are properties and all properties have a private field.
     * Therefore we achieve encapsulation Kotlin by defining custom getters and setters.
     * This style is standard Kotlin convention called "Backing Fields".
     * That is why in some cases we don't need to declare variables with private modifiers.
     * In the cases that we do need private modifiers, it is when we need "Backing Properties" OR
     * when we want the variable getter and setter to be private.
     * Please go here for more information: https://kotlinlang.org/docs/reference/properties.html
     */

    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    constructor(userId: String, firstName: String, lastName: String) : this() {
        if (!isValidName(firstName) || !isValidName(lastName) || !isValidName(userId)) {
            this.userId = ""
            this.firstName = ""
            this.lastName = ""
        } else {
            this.userId = userId
            this.firstName = firstName
            this.lastName = lastName
        }
    }

    // Additional constructor allows for setting conversations and contacts
    constructor(
        userId: String,
        firstName: String,
        lastName: String,
        contacts: MutableList<String>,
        conversations: MutableList<Conversation>
    ) : this() {
        var invalidContacts: Boolean = false
        var invalidConversation: Boolean = false

        for (cont in contacts) {
            if (!isValidUserId(cont)) {
                invalidContacts = true
                break
            }
        }

        for (convo in conversations) {
            if (!Conversation.isValidConversation(convo)) {
                invalidConversation = true
                break
            }
        }

        if (!isValidName(firstName) || !isValidName(lastName) || !isValidName(userId) || invalidContacts || invalidConversation) {
            this.userId = ""
            this.firstName = ""
            this.lastName = ""
        } else {
            this.userId = userId
            this.firstName = firstName
            this.lastName = lastName
            this.contacts = contacts
            this.conversations = conversations
        }

    }

    companion object {
        fun isValidUser(user: User): Boolean =
            isValidUserId(user.userId) && isValidName(user.firstName) && isValidName(user.lastName)

        fun isValidUserId(usrId: String): Boolean =
            usrId.matches(Regex("^[a-zA-Z0-9_+@.-]*$")) && usrId.isNotEmpty()

        fun isValidName(name: String): Boolean =
            name.matches(Regex("^[a-zA-Z0-9]*$")) && name.isNotEmpty()
    }

    var userId: String = ""
        get() {
            return field
        }
        private set(userId: String) {
            if (isValidUserId(userId))
                field = userId
        }

    var firstName: String = ""
        get() {
            return field
        }
        set(firstName: String) {
            if (isValidName(firstName))
                field = firstName
        }

    var lastName: String = ""
        get() {
            return field
        }
        set(lastName: String) {
            if (isValidName(lastName))
                field = lastName
        }

    // We set the next two variables' setters to private because we don't want to expose the
    // default setter functions to the world
    var contacts: MutableList<String> = mutableListOf()
        private set

    var blockedContacts: MutableList<String> = mutableListOf()

    var conversations : MutableList<Conversation> = mutableListOf()
        private set

    var device : Device = Device()
        get(){
            return field
        }
        set(dvice : Device){
            field = dvice
        }

    // Add conversation locally to conversation list
    fun addConversation(convo : Conversation) : Boolean {
        if(convo in this.conversations) {
            return false
        }
        this.conversations.add(convo)
        return true
    }

    // Iteration 2
    // Removes conversation from conversation list
    // Note: using conversationId instead of conversation now
    fun deleteConversation(convoId : String): Boolean {
        val conversationsLen = conversations.size
        for(index in 0..conversationsLen){
            if(conversations[index].convoId == convoId){
                conversations.removeAt(index)
                return true
            }
        }
        return false
    }

    // start conversation with another user by sending conversation to database
    fun startConversation(convo : Conversation) : Boolean {
        val toAdd = hashMapOf(
            "canonicalId" to convo.convoId,
            "created" to Timestamp.now(),
            "users" to listOf<String>(convo.user1Id, convo.user2Id)
        )
        // IMPORTANT: Implement somewhere the function to delete this added conversation IF key exchange or adding conversation fails!!!
        addConversation(convo)
        db.collection("conversations").document(convo.convoId)
            .set(toAdd)
            .addOnSuccessListener {
                Log.d("startConversation", "success")
            }
            .addOnFailureListener {
                Log.d("startConversation", "failure")
            }

        return true
    }

    // Gets conversation and public keys from the database - Untestable because server
    fun receiveConversation(convo : Conversation) : Boolean {
        getUserPublicKey(convo.user1Id)
        getUserPublicKey(convo.user2Id)
        addConversation(convo)
        return false
    }

    fun getConversationByUserId(recipientId : String) : Conversation? {
        var retConvoList: List<Conversation> = this.conversations
            .filter { x -> (x.user1Id == recipientId || x.user2Id == recipientId)}
        if (retConvoList.isEmpty()) {
            return null
        }
        return retConvoList[0]
    }

    fun getConversationByConversationId(convoId: String) : Conversation? {
        var retConvoList: List<Conversation> = this.conversations
            .filter { x -> x.convoId == convoId}
        if (retConvoList.isEmpty()) {
            return null
        }
        return retConvoList[0]
    }



    fun addContact(user : String) : Boolean {
        if(user in this.contacts) {
            return false
        }
        this.contacts.add(user)
        return true
    }

    fun getContactById(userId : String) : String? {
        var retUserList: List<String> = this.contacts.filter {x -> x == userId}
        if (retUserList.isEmpty()) {
            return null
        }
        return retUserList[0]
    }

    fun deleteContact(user : String) : Boolean {
        var index = this.contacts.indexOf(user)
        if (index < 0) {
            return false
        }
        this.contacts.removeAt(index)
        return true
    }

    // Untestable - relies on database functionality
    fun getUserIdsByFirstName(firstName : String) : List<String> {
        val retList = mutableListOf<String>()
        val query = db.collection("users").whereEqualTo("first_name", firstName)
        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    retList.add(document.data.getValue("canonicalId") as String)
                }
            }
        return retList
    }

    // Untestable - relies on database functionality
    fun getUserIdsByLastName(lastName : String) : List<String> {
        val retList = mutableListOf<String>()
        val query = db.collection("users").whereEqualTo("last_name", lastName)
        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    retList.add(document.data.getValue("canonicalId") as String)
                }
            }
        return retList
    }

    // Untestable - relies on database functionality
    fun doesUserExistByUserId(userId : String) : Boolean {
        var retVal = false
        val query = db.collection("users").whereEqualTo("canonicalId", userId)
        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    retVal = true
                }
            }
        return retVal
    }

    fun checkIfBlocked(userId : String) : Boolean {
        // TODO
        return false
    }

    // Untestable - relies on database functionality
    fun getBlockList(userId: String) : MutableList<String>? {
        // TODO
        return null
    }

    fun addBlockedContact(userId : String) : Boolean {
        //block any userid that exists or only those currently in contacts?
        var user1 = getContactById(userId)
        if(user1 in this.contacts) {
            this.blockedContacts.add(userId)
            return true
        }
        else {
            return false
        }
    }

    fun deleteBlockedContact(userId : String) : Boolean {
        var index = this.blockedContacts.indexOf(userId)
        if (index < 0) {
            return false
        }
        this.blockedContacts.removeAt(index)
        return true
    }

    // Sends image message to server - partially testable
    fun sendImageMsg(msg : ByteArray, convo: Conversation){
        val recipient = if (convo.user1Id == this.userId) convo.user2Id else convo.user1Id
        val timestamp = Instant.now().toEpochMilli()
        val msg = ImageMessage(msg, convo.convoId,this.userId, recipient,timestamp)
        convo.addMessage(msg)

        sendEncryptedMsg(msg, convo)
    }

    // Sends text message to server
    fun sendTextMsg(msg : String, convo : Conversation){
        val recipient = if (convo.user1Id == this.userId) convo.user2Id else convo.user1Id
        val timestamp = Instant.now().toEpochMilli()
        val txtMsg = TextMessage(msg, convo.convoId, this.userId, recipient, timestamp)
        convo.addMessage(txtMsg)

        sendEncryptedMsg(txtMsg, convo)
    }

    // Fully untestable because all this does is hit the server
    private fun sendEncryptedMsg(unencryptedMsg: UnencryptedMessage, convo: Conversation) {
        val encryptedMessage: EncryptedMessage = device.cipher.encryptUnencryptedMessage(unencryptedMsg)

        when(unencryptedMsg) {
            is TextMessage -> {
                val toSend = hashMapOf(
                    "bucket_url" to "",
                    "data" to Blob.fromBytes(encryptedMessage.message),
                    "message_type" to encryptedMessage.messageType,
                    "sender_id" to encryptedMessage.senderId,
                    "recipient_id" to encryptedMessage.recipientId,
                    "timestamp" to encryptedMessage.timestamp,
                    "encrypted_aes_key" to Blob.fromBytes(encryptedMessage.encryptedAESKey)
                )

                db.collection("conversations")
                    .document(convo.convoId)
                    .collection("messages")
                    .document()
                    .set(toSend)
                    .addOnSuccessListener {
                        Log.d("sendEncryptedMsg", "Success!")
                    }
                    .addOnFailureListener {
                        Log.d("sendTextMsg", "Failure!")
                    }
            }
            is ImageMessage -> {
                val uuid = UUID.randomUUID()
                val filename = "${convo.convoId}/${uuid}"

                val newImageRef = storage.reference.child(filename)
                val uploadTask = newImageRef.putBytes(encryptedMessage.message)

                uploadTask.addOnSuccessListener {
                    val toSend = hashMapOf(
                        "bucket_url" to newImageRef.downloadUrl,
                        "data" to "",
                        "message_type" to encryptedMessage.messageType,
                        "sender_id" to encryptedMessage.senderId,
                        "recipient_id" to encryptedMessage.recipientId,
                        "timestamp" to encryptedMessage.timestamp,
                        "encrypted_aes_key" to Blob.fromBytes(encryptedMessage.encryptedAESKey)
                    )

                    db.collection("conversations")
                        .document(convo.convoId)
                        .collection("messages")
                        .document()
                        .set(toSend)
                        .addOnSuccessListener {
                            Log.d("sendEncryptedMsg", "Success!")
                        }
                        .addOnFailureListener {
                            Log.d("sendEncryptedMsg", "Failure!")
                        }
                }

                uploadTask.addOnFailureListener {
                    Log.d("sendEncryptedMessage", "Failure!")
                }
            }
        }
    }

    // Gets and stores public key of user from server
    fun getUserPublicKey(userId : String) : Task<DocumentSnapshot> {
        val keyFactory : KeyFactory = KeyFactory.getInstance("RSA")
        val keyTask : Task<DocumentSnapshot> = db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val keyField = documentSnapshot.getBlob("publicKey")!!
                Log.d("We got the key: ", keyField.toString())
                val publicKey: PublicKey =
                    keyFactory.generatePublic(X509EncodedKeySpec(keyField.toBytes()))

                val fileUserId = if (this.userId == userId) "myKey" else userId

                device.addUserPublicKey(fileUserId, publicKey)
            }.addOnFailureListener {
                e -> throw e
            }

        return keyTask
    }

    // gets the other user in a message given yourself.
    fun getOtherUser(msg : Message) : String {
        if(msg.senderId == this.userId) {
            return msg.recipientId
        }
        return msg.senderId
    }

    // Handle incoming message from server
    fun receiveMsg(decryptedMsg: UnencryptedMessage) : Boolean {
        // var decryptedMessage = device.cipher.decryptEncryptedMessage(encryptedMsg)

        var localConvoObject = getConversationByUserId(getOtherUser(decryptedMsg))
        localConvoObject ?: return false
        localConvoObject ?. addMessage(decryptedMsg)
        return true
    }

    // Iteration 2
    // Remove a message from a conversation in conversations list
    fun deleteSentMessage(message: Message): Boolean {
        /* We delete a message based on a few criteria:
         * timestamps are the same (this should be unique since you can't send two messages at the same exact millisecond)
         * sender and receiver ids are the same
         * message content is the same
         */
        val senderId = message.senderId
        val recipientId = message.recipientId
        val timestamp = message.timestamp
        val possibleConvoId1 = senderId + "-" + recipientId
        val possibleConvoId2 = recipientId + "-" + senderId
        for(c in conversations){
            if(c.convoId == possibleConvoId1 || c.convoId == possibleConvoId2){
                val messages = c.messages
                val numMessages = messages.size
                for(index in 0..numMessages){
                    if(message.mEquals(messages[index])){
                        messages.removeAt(index)
                        return true
                    }
                }
                // Return false if we cannot find the message since conversations are unique!
                return false
            }
        }
        return false
    }

    // Add your own public key to server - Untestable because it deals with server
    fun addPublicKeyToServer(key : String, user : User) : Boolean {
        db.collection("users").document(user.userId)
            .update("publicKey", key)
            .addOnSuccessListener {
                Log.d("addPublicKeyToServer", "success")
            }
            .addOnFailureListener {
                Log.d("addPublicKeyToServer", "failure")
            }
        return false
    }

    // Adds the current user to the database
    fun addSelfToDatabase() : Boolean {
        var publicKey =
            Blob.fromBytes(device.cipher.publicKeyRing["myKey"]?.encoded!!)

        val toAdd = hashMapOf(
            "name" to this.userId,
            "canonicalId" to this.userId,
            "first_name" to this.firstName,
            "last_name" to this.lastName,
            "password_hash" to "",
            "phone" to "123",
            "publicKey" to publicKey
        )

        db.collection("users").document(this.userId)
            .set(toAdd)
            .addOnSuccessListener {
                Log.d("addSelfToDatabase", "User added")
            }
            .addOnFailureListener {
                Log.d("addSelfToDatabase", "User add failed")
            }
        return true
    }
}
