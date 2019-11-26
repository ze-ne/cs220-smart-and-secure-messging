package com.cs220.ssmessaging.clientBackend

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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

    fun deleteConversationFromDb(convoId : String, callback: (() -> Unit)? = null) {
        val convo = db.collection("conversations").document(convoId)
        convo.delete()
            .addOnFailureListener {
                callback?.invoke()
            }
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
    fun getUserIdsByFirstName(firstName : String, callBack: (List<String>) -> Unit){
        val retList = mutableListOf<String>()
        val query = db.collection("users").whereEqualTo("first_name", firstName)
        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    retList.add(document.data.getValue("canonicalId") as String)
                }
                callBack(retList)
            }
    }

    // Untestable - relies on database functionality
    fun getUserIdsByLastName(lastName : String, callBack: (List<String>) -> Unit) {
        val retList = mutableListOf<String>()
        val query = db.collection("users").whereEqualTo("last_name", lastName)
        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    retList.add(document.data.getValue("canonicalId") as String)
                }
                callBack(retList)
            }
    }

    // Untestable - relies on database functionality
    fun doesUserExistByUserId(userId : String, callBack: () -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener{
                callBack()
            }
    }

    fun updateFirstName(newFirst: String) {
        val newData = hashMapOf("first_name" to newFirst)
        val userDoc = db.collection("users").document(userId)
        userDoc.set(newData, SetOptions.merge()).addOnSuccessListener {
            firstName = newFirst
        }
    }

    fun updateLastName(newLast: String) {
        val newData = hashMapOf("last_name" to newLast)
        val userDoc = db.collection("users").document(userId)
        userDoc.set(newData, SetOptions.merge()).addOnSuccessListener {
            lastName = newLast
        }
    }

    // Check if the current user has blocked the input userId
    fun checkIfInBlockList(userId : String) : Boolean {
        return blockedContacts.contains(userId)
    }

    // Untestable - relies on database functionality
    fun getBlockList(userId: String) : MutableList<String>? {
        var temp = mutableListOf<String>()
        val docref = db.collection("users").document(userId)
        docref.get()
            .addOnSuccessListener { document ->
                val blockedlist = document.data?.get("block_list") as MutableList<String>
                temp = blockedlist
            }
        return temp
    }

    // This function is untestable since it adds the blocked contact to Db
    // It also uses the addBlockedContact (local addition) if it successfully adds to Db.
    // However note that addBlockedContact has been unit tested
    fun addBlockedContactToDb(userId : String) {
        if (checkIfInBlockList(userId) || !isValidUserId(userId))
            return
        // Add to Db
        val newBlockedContacts = hashMapOf("block_list" to blockedContacts)
        db.collection("users").document(userId)
            .set(newBlockedContacts, SetOptions.merge())
            .addOnSuccessListener {
                // Add the contact locally. In addition, we need to delete every single conversation
                addBlockedContact(userId)
                for(c in conversations){
                    if(c.user1Id == userId || c.user2Id == userId)
                        deleteConversationFromDb(c.convoId)
                }
            }
            .addOnFailureListener {
                // Do nothing on failure
            }
    }

    // This function adds blocked contacts locally
    fun addBlockedContact(userId : String) : Boolean{
        //local
        if (checkIfInBlockList(userId) || !isValidUserId(userId)){
            return false
        }
        else{
            this.blockedContacts.add(userId)
            return true
        }
    }

    // This function is untestable since it deletes the blocked contact from Db
    // It also uses the deleteBlockedContact (local deletion) if it successfully deletes from Db.
    // However note that deleteBlockedContact has been unit tested
    fun deleteBlockedContactFromDb(userId: String) {
        // Check if userId exists in blocked contacts
        if (!checkIfInBlockList(userId)) {
            return
        }
        var index = blockedContacts.indexOf(userId)


        // Get a copy of the blocked contacts
        val tempBlockedContacts = blockedContacts.toMutableList()
        tempBlockedContacts.removeAt(index)

        //database deletion.
        val newBlockedContacts = hashMapOf("block_list" to tempBlockedContacts)
        db.collection("users").document(userId)
            .set(newBlockedContacts, SetOptions.merge())
            .addOnSuccessListener {
                // If successful, we delete locally
                deleteBlockedContact(userId)
            }
            .addOnFailureListener{
                // On failure, do nothing
            }
    }

    // This function deletes blocked contacts locally
    fun deleteBlockedContact(userId : String) : Boolean {
        // Check if userId exists in blocked contacts
        if (!checkIfInBlockList(userId)) {
            return false
        }
        var index = blockedContacts.indexOf(userId)

        blockedContacts.removeAt(index)
        return true
    }

    // Sends image message to server - partially testable
    fun sendImageMsg(msg : ByteArray, convo: Conversation, isVisible: Boolean = true){
        val recipient = if (convo.user1Id == this.userId) convo.user2Id else convo.user1Id
        val timestamp = Instant.now().toEpochMilli()
        val msg = ImageMessage(msg, convo.convoId,this.userId, recipient,timestamp)
        msg.isVisible = isVisible
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
                    //"is_visible" to unencryptedMsg.isVisible,
                    "sender_encrypted_aes_key" to Blob.fromBytes(encryptedMessage.encryptedSenderAESKey),
                    "recipient_encrypted_aes_key" to Blob.fromBytes(encryptedMessage.encryptedRecipientAESKey)
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
                    newImageRef.getDownloadUrl().addOnSuccessListener { bucket_uri ->
                        Log.d("URI of bucket", bucket_uri.toString())
                        val toSend = hashMapOf(
                            "bucket_url" to bucket_uri.toString(),
                            "bucket_path" to filename,
                            "data" to Blob.fromBytes(byteArrayOf(0)),
                            "message_type" to encryptedMessage.messageType,
                            "sender_id" to encryptedMessage.senderId,
                            "recipient_id" to encryptedMessage.recipientId,
                            "timestamp" to encryptedMessage.timestamp,
                            //"is_visible" to unencryptedMsg.isVisible,
                            "sender_encrypted_aes_key" to Blob.fromBytes(encryptedMessage.encryptedSenderAESKey),
                            "recipient_encrypted_aes_key" to Blob.fromBytes(encryptedMessage.encryptedRecipientAESKey)
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
        println("============ deleting ============")

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
        println("==" + possibleConvoId1 + " or " + possibleConvoId2 + "==")
        for(c in conversations){
            println("==" + c.convoId + "==")

            if(c.convoId == possibleConvoId1 || c.convoId == possibleConvoId2){
                println("============ found convo ============")

                val messages = c.messages
                val numMessages = messages.size
                for(index in 0..numMessages){
                    println("============ searching list ============")

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

    fun deleteSentMessageFromDb(convoId: String, message: Message, callback: (() -> Unit)? = null) {
        val senderId = message.senderId
        val timestamp = message.timestamp

        val convo = db.collection("conversation").document(convoId)
        val query = convo.collection("messages")
            .whereEqualTo("timestamp", timestamp)
            .whereEqualTo("sender_id", senderId)
            .limit(1)
        query.get()
            .addOnSuccessListener {documents ->
                for (document in documents) {
                    document.reference.delete()
                        .addOnFailureListener{
                            callback?.invoke()
                        }
                }
            }
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
            "block_list" to blockedContacts,
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
