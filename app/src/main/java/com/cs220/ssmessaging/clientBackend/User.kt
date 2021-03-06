package com.cs220.ssmessaging.clientBackend

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.time.Instant
import java.util.Timer
import kotlin.concurrent.schedule
import java.util.*
import android.os.Handler
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.firebase.firestore.*

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
        if (!isValidName(firstName) || !isValidName(lastName) || !isValidUserId(userId)) {
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

        if (!isValidName(firstName) || !isValidName(lastName) || !isValidUserId(userId) || invalidContacts || invalidConversation) {
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
            name.matches(Regex("^[a-zA-Z]*$")) && name.isNotEmpty()
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

    var conversations: MutableList<Conversation> = mutableListOf()
        private set

    var listeners: MutableList<ListenerRegistration> = mutableListOf()

    var phoneNumber: String = ""

    var device: Device = Device()
        get() {
            return field
        }
        set(dvice: Device) {
            field = dvice
        }

    // Add conversation locally to conversation list
    fun addConversation(convo: Conversation): Boolean {
        for (c in this.conversations){
            if(c.convoId == convo.convoId)
                return false
        }
        this.conversations.add(convo)
        return true
    }

    // Iteration 2
    // Removes conversation from conversation list
    // Note: using conversationId instead of conversation now
    fun deleteConversation(convoId: String): Boolean {

        for (index in conversations.indices) {
            if (conversations[index].convoId == convoId) {
                conversations.removeAt(index)
                return true
            }
        }
        return false
    }

    fun deleteConversationFromDb(convoId: String, callback: (() -> Unit)? = null) {
        val convo = db.collection("conversations").document(convoId)
        convo.delete()
            .addOnFailureListener {
                callback?.invoke()
            }
    }

    // start conversation with another user by sending conversation to database
    fun startConversation(convo: Conversation, activity : FragmentActivity?, callback: (() -> Unit)? = null): Boolean {
        val toAdd = hashMapOf(
            "canonicalId" to convo.convoId,
            "created" to Timestamp.now(),
            "users" to listOf<String>(convo.user1Id, convo.user2Id)
        )

        // Check if the conversation exists
        for (c in this.conversations){
            if(c.convoId == convo.convoId)
                return false
        }
        db.collection("conversations").document(convo.convoId)
            .set(toAdd)
            .addOnSuccessListener {
                addConversation(convo)
                callback?.invoke()
                if(activity != null)
                    Toast.makeText(activity, "Successfully started conversation in database", Toast.LENGTH_LONG).show()
                Log.d("startConversation", "success")
            }
            .addOnFailureListener {
                if(activity != null)
                    Toast.makeText(activity, "Failed to start conversation in database", Toast.LENGTH_LONG).show()
                Log.d("startConversation", "failure")
            }

        return true
    }

    fun getConversationByUserId(recipientId: String): Conversation? {
        var retConvoList: List<Conversation> = this.conversations
            .filter { x -> (x.user1Id == recipientId || x.user2Id == recipientId) }
        if (retConvoList.isEmpty()) {
            return null
        }
        return retConvoList[0]
    }

    fun getConversationByConversationId(convoId: String): Conversation? {
        var retConvoList: List<Conversation> = this.conversations
            .filter { x -> x.convoId == convoId }
        if (retConvoList.isEmpty()) {
            return null
        }
        return retConvoList[0]
    }


    fun addContact(user: String): Boolean {
        if (user in this.contacts) {
            return false
        }
        this.contacts.add(user)
        return true
    }

    fun getContactById(userId: String): String? {
        var retUserList: List<String> = this.contacts.filter { x -> x == userId }
        if (retUserList.isEmpty()) {
            return null
        }
        return retUserList[0]
    }

    fun deleteContact(user: String): Boolean {
        var index = this.contacts.indexOf(user)
        if (index < 0) {
            return false
        }
        this.contacts.removeAt(index)
        return true
    }

    // Pairs in the mutable list follow the format: (userId, Firstname + " " + Lastname)
    // gets all users from the database that have userId, first name, or lastname equal to the searchTerm
    // Refactor in next iteration for readability.
    fun getAllUsersWithSearchTerm(searchTerm : String, callback: ((MutableList<String>) -> Unit)? = null)
    {
        val outputList : MutableList<String> = mutableListOf()
        val usersRef = db.collection("users")
        usersRef.whereEqualTo("canonicalId", searchTerm).get()
            .addOnSuccessListener { it ->
                val documents = it.documents
                for(doc in documents){
                    var userName = doc.data?.get("canonicalId")
                    var firstName = doc.data?.get("first_name")
                    var lastName = doc.data?.get("last_name")
                    if(userName != null && firstName != null && lastName  != null)
                        outputList.add((userName  as String) + ": " + (firstName as String) + " " + (lastName as String))
                }

                // Search for users based on first name
                usersRef.whereEqualTo("first_name", searchTerm).get()
                    .addOnSuccessListener { it2 ->
                        val documents2 = it2.documents
                        for(doc in documents2){
                            var userName = doc.data?.get("canonicalId")
                            var firstName = doc.data?.get("first_name")
                            var lastName = doc.data?.get("last_name")

                            val userInfoString : String
                            if(userName != null && firstName != null && lastName  != null){
                                userInfoString =
                                    (userName  as String) + ": " + (firstName as String) + " " + (lastName as String)
                                if(!outputList.contains(userInfoString))
                                    outputList.add(userInfoString)
                            }
                        }

                        // Search for users based on last name
                        usersRef.whereEqualTo("last_name", searchTerm).get()
                            .addOnSuccessListener { it3 ->
                                val documents3 = it3.documents
                                for (doc in documents3) {
                                    var userName = doc.data?.get("canonicalId")
                                    var firstName = doc.data?.get("first_name")
                                    var lastName = doc.data?.get("last_name")

                                    val userInfoString: String
                                    if (userName != null && firstName != null && lastName != null) {
                                        userInfoString =
                                            (userName as String) + ": " + (firstName as String) + " " + (lastName as String)
                                        if (!outputList.contains(userInfoString))
                                            outputList.add(userInfoString)
                                    }
                                }
                                callback?.invoke(outputList)
                            }.addOnFailureListener{ callback?.invoke(outputList) }
                    }
                    .addOnFailureListener{ callback?.invoke(outputList) }
            }
            .addOnFailureListener { callback?.invoke(outputList) }
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

    // This function is untestable since it adds the blocked contact to Db
    // It also uses the addBlockedContact (local addition) if it successfully adds to Db.
    // However note that addBlockedContact has been unit tested
    fun addBlockedContactToDb(userId : String, callback: (() -> Unit)?) {
        if (checkIfInBlockList(userId) || !isValidUserId(userId))
            return
        // Add to Db
        val newBlockedContacts = this.blockedContacts.toMutableList()
        newBlockedContacts.add(userId)

        val newBlockedContactsMap = mapOf("block_list" to newBlockedContacts)
        db.collection("users").document(this.userId)
            .update(newBlockedContactsMap)
            .addOnSuccessListener {
                // Add the contact locally. In addition, we need to delete every single conversation
                addBlockedContact(userId)
                callback?.invoke()
                for(c in conversations){
                    if(c.user1Id == userId || c.user2Id == userId){
                        deleteConversationFromDb(c.convoId)
                        break
                    }
                }
            }
            .addOnFailureListener {
                // Else do nothing : Remove the line below
                //blockedContacts.remove(userId)
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

    // Untestable - relies on database functionality
    fun getBlockList(callback: (() -> Unit)?) {
        var temp = mutableListOf<String>()
        val docref = db.collection("users").document(this.userId)
        docref.get()
            .addOnSuccessListener { document ->
                val blockedlist = document.data?.get("block_list")
                if(blockedlist != null) {
                    blockedContacts = blockedlist as MutableList<String>
                    callback?.invoke()
                }
            }
    }

    // This function is untestable since it deletes the blocked contact from Db
    // It also uses the deleteBlockedContact (local deletion) if it successfully deletes from Db.
    // However note that deleteBlockedContact has been unit tested
    fun deleteBlockedContactFromDb(userId: String, callback: () -> Unit) {
        // Check if userId exists in blocked contacts
        if (!checkIfInBlockList(userId)) {
            return
        }
        var index = blockedContacts.indexOf(userId)


        // Get a copy of the blocked contacts
        val tempBlockedContacts = blockedContacts.toMutableList()
        tempBlockedContacts.removeAt(index)

        //database deletion.
        val newBlockedContacts = mapOf("block_list" to tempBlockedContacts)
        db.collection("users").document(this.userId)
            .update(newBlockedContacts)
            .addOnSuccessListener {
                // If successful, we delete locally
                deleteBlockedContact(userId)
                callback()
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
    fun sendImageMsg(msg: ByteArray, convo: Conversation, isVisible: Boolean = true, deletionTimer: Long = -1.toLong()) {
        val recipient = if (convo.user1Id == this.userId) convo.user2Id else convo.user1Id
        val timestamp = Instant.now().toEpochMilli()
        val msg = ImageMessage(msg, convo.convoId, this.userId, recipient, timestamp)
        msg.isVisible = isVisible
        msg.deletionTimer = deletionTimer
        convo.addMessage(msg)

        sendEncryptedMsg(msg, convo)
    }

    // Sends text message to server
    fun sendTextMsg(msg: String, convo: Conversation, isVisible: Boolean = true, deletionTimer : Long = -1.toLong()) {
        val recipient = if (convo.user1Id == this.userId) convo.user2Id else convo.user1Id
        val timestamp = Instant.now().toEpochMilli()
        val txtMsg = TextMessage(msg, convo.convoId, this.userId, recipient, timestamp)
        txtMsg.isVisible = isVisible
        txtMsg.deletionTimer = deletionTimer
        convo.addMessage(txtMsg)

        sendEncryptedMsg(txtMsg, convo)
    }

    // Fully untestable because all this does is hit the server
    private fun sendEncryptedMsg(unencryptedMsg: UnencryptedMessage, convo: Conversation) {
        val encryptedMessage: EncryptedMessage =
            device.cipher.encryptUnencryptedMessage(unencryptedMsg)

        when (unencryptedMsg) {
            is TextMessage -> {
                val toSend = hashMapOf(
                    "bucket_url" to "",
                    "data" to Blob.fromBytes(encryptedMessage.message),
                    "message_type" to encryptedMessage.messageType,
                    "sender_id" to encryptedMessage.senderId,
                    "recipient_id" to encryptedMessage.recipientId,
                    "timestamp" to encryptedMessage.timestamp,
                    "is_visible" to unencryptedMsg.isVisible,
                    "sender_encrypted_aes_key" to Blob.fromBytes(encryptedMessage.encryptedSenderAESKey),
                    "recipient_encrypted_aes_key" to Blob.fromBytes(encryptedMessage.encryptedRecipientAESKey),
                    "deletion_timer" to unencryptedMsg.deletionTimer
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
                            "is_visible" to unencryptedMsg.isVisible,
                            "sender_encrypted_aes_key" to Blob.fromBytes(encryptedMessage.encryptedSenderAESKey),
                            "recipient_encrypted_aes_key" to Blob.fromBytes(encryptedMessage.encryptedRecipientAESKey),
                            "deletion_timer" to unencryptedMsg.deletionTimer
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
    fun getUserPublicKey(userId: String): Task<DocumentSnapshot> {
        val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
        val keyTask: Task<DocumentSnapshot> = db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val keyField = documentSnapshot.getBlob("publicKey")
                if(keyField != null){
                    Log.d("We got the key: ", keyField.toString())
                    val publicKey: PublicKey =
                        keyFactory.generatePublic(X509EncodedKeySpec(keyField.toBytes()))

                    val fileUserId = if (this.userId == userId) "myKey" else userId

                    device.addUserPublicKey(fileUserId, publicKey)
                }
            }.addOnFailureListener { e ->
                throw e
            }

        return keyTask
    }

    // gets the other user in a message given yourself.
    fun getOtherUser(msg: Message): String {
        if (msg.senderId == this.userId) {
            return msg.recipientId
        }
        return msg.senderId
    }

    // Handle incoming message from server
    fun receiveMsg(decryptedMsg: UnencryptedMessage): Boolean {
        // var decryptedMessage = device.cipher.decryptEncryptedMessage(encryptedMsg)

        var localConvoObject = getConversationByUserId(getOtherUser(decryptedMsg))
        localConvoObject ?: return false
        localConvoObject?.addMessage(decryptedMsg)

        // If there is a deletion timer, then set up for deletion if you are the message recipient
        if(decryptedMsg.deletionTimer > 0 && this.userId == decryptedMsg.recipientId){
            deleteMessageTimer(localConvoObject.convoId, decryptedMsg, decryptedMsg.deletionTimer)
        }
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
        for (c in conversations) {

            if (c.convoId == possibleConvoId1 || c.convoId == possibleConvoId2) {

                val messages = c.messages
                for (index in messages.indices) {

                    if (message.mEquals(messages[index])) {
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
        val convo = db.collection("conversations").document(convoId)
        //val query = db.collection("conversation").document(convoId).collection("messages")
        val query = convo.collection("messages")
            .whereEqualTo("timestamp", timestamp)
            .whereEqualTo("sender_id", senderId)
            .limit(1)
        query.get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    document.reference.delete()
                        .addOnFailureListener {
                            callback?.invoke()
                        }
                }


            }
            .addOnFailureListener() {

            }
    }

    fun deleteMessageTimer(convoId: String, message: Message, seconds: Long) {
        Timer("delete_timer", false).schedule(seconds*1000) {
            deleteSentMessageFromDb(convoId, message)
        }
    }

    // Add your own public key to server - Untestable because it deals with server
    fun addPublicKeyToServer(key: String, user: User): Boolean {
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
    fun addSelfToDatabase(): Boolean {
        var publicKey =
            Blob.fromBytes(device.cipher.publicKeyRing["myKey"]?.encoded!!)

        val toAdd = hashMapOf(
            "name" to this.userId,
            "canonicalId" to this.userId,
            "first_name" to this.firstName,
            "last_name" to this.lastName,
            "password_hash" to "",
            "phone" to phoneNumber,
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

    fun addListener(listener: ListenerRegistration){
        listeners.add(listener)
    }

    fun removeListeners(){
        for(l in listeners){
            l.remove()
        }
        listeners = mutableListOf()
    }
}
