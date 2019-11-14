package com.cs220.ssmessaging.clientBackend

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import com.cs220.ssmessaging.clientBackend.Conversation
import com.cs220.ssmessaging.clientBackend.Message
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.nio.charset.Charset
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

    constructor(userId : String, firstName: String, lastName: String) : this(){
        if(!isValidName(firstName) || !isValidName(lastName) || !isValidName(userId)){
            this.userId = ""
            this.firstName = ""
            this.lastName = ""
        }
        else{
            this.userId = userId
            this.firstName = firstName
            this.lastName = lastName
        }
    }

    // Additional constructor allows for setting conversations and contacts
    constructor(userId : String, firstName: String, lastName: String, contacts : MutableList<String>, conversations : MutableList<Conversation>) : this(){
        var invalidContacts : Boolean = false
        var invalidConversation : Boolean = false

        for(cont in contacts){
            if(!isValidUserId(cont)) {
                invalidContacts = true
                break
            }
        }

        for(convo in conversations){
            if(!Conversation.isValidConversation(convo)) {
                invalidConversation = true
                break
            }
        }

        if(!isValidName(firstName) || !isValidName(lastName) || !isValidName(userId) || invalidContacts || invalidConversation){
            this.userId = ""
            this.firstName = ""
            this.lastName = ""
        }
        else{
            this.userId = userId
            this.firstName = firstName
            this.lastName = lastName
            this.contacts = contacts
            this.conversations = conversations
        }

    }

    companion object{
        fun isValidUser(user : User) : Boolean =
            isValidUserId(user.userId) && isValidName(user.firstName) && isValidName(user.lastName)

        fun isValidUserId(usrId : String) : Boolean = usrId.matches(Regex("^[a-zA-Z0-9_+@.-]*$")) && usrId.isNotEmpty()

        fun isValidName(name : String) : Boolean = name.matches(Regex("^[a-zA-Z0-9]*$")) && name.isNotEmpty()
    }

    var userId : String = ""
        get(){
            return field
        }
        private set(userId : String){
            if(isValidUserId(userId))
                field = userId
        }

    var firstName : String = ""
        get(){
            return field
        }

        set(firstName : String){
            if(isValidName(firstName))
                field = firstName
        }

    var lastName : String = ""
        get(){
            return field
        }
        set(lastName : String){
            if(isValidName(lastName))
                field = lastName
        }

    // We set the next two variables' setters to private because we don't want to expose the
    // default setter functions to the world
    var contacts : MutableList<String> = mutableListOf()
        private set

    /* We are moving the "Manage Block List" that this variable and its getter/setter
       methods implement to iteration 2.
    var blockedContacts : MutableList<User>
        // get returns copy of list
        get() {
            // TODO
            return mutableListOf()
        }
        private set(contacts : MutableList<User>){
            // TODO
        } */

    var conversations : MutableList<Conversation> = mutableListOf()
        private set

    var device : Device = Device()
        get(){
            return field
        }
        set(dvice : Device){
            field = dvice
        }

    fun addConversation(convo : Conversation) : Boolean {
        /*val conversation = hashMapOf(
            "created" to Timestamp(Date()),
            "users" to arrayListOf<String>(convo.user1Id, convo.user2.userId)
        )

        db.collection("/collections")
        .add(conversation)
        .addOnSuccessListener { documentReference ->
            Log.d(TAG, "Conversation DocumentSnapshot written with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Error adding conversation document", e)
        }
        return true*/
        if(convo in this.conversations) {
            return false
        }
        this.conversations.add(convo)
        // TODO: write convo validity checks
        return true
    }

    // FIX: Write unit tests for this
    fun startConversation(convo : Conversation) : Boolean {
        val toAdd = hashMapOf(
            "canonicalId" to convo.convoId,
            "created" to Timestamp.now(),
            "users" to listOf<String>(convo.user1Id, convo.user2Id)
        )
        addConversation(convo)
        db.collection("conversations").document(convo.convoId)
            .set(toAdd)
            .addOnSuccessListener {
                Log.d("startConversation", "success")
                getUserPublicKey(convo.user1Id)
                getUserPublicKey(convo.user2Id)
            }
            .addOnFailureListener {
                Log.d("startConversation", "failure")
            }

        return true
    }

    // FIX: Write unit tests for this
    fun receiveConversation(convo : Conversation) : Boolean {
        // TODO
        getUserPublicKey(convo.user1Id)
        getUserPublicKey(convo.user2Id)
        addConversation(convo)
        return false
    }

    fun getConversationByUserId(recipientId : String) : Conversation? {
        // TODO: validity checks
        var retConvoList: List<Conversation> = this.conversations
            .filter { x -> (x.user1Id == recipientId || x.user2Id == recipientId)}
        if (retConvoList.isEmpty()) {
            return null
        }
        return retConvoList.single()
    }

    fun getConversationByConversationId(convoId: String) : Conversation? {
        // TODO: validity checks
        var retConvoList: List<Conversation> = this.conversations
            .filter { x -> x.convoId == convoId}
        if (retConvoList.isEmpty()) {
            return null
        }
        return retConvoList.single()
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
        return retUserList.single()
    }

    fun deleteContact(user : String) : Boolean {
        var index = this.contacts.indexOf(user)
        if (index < 0) {
            return false
        }
        this.contacts.removeAt(index)
        return true
    }

    /* We are moving the "Manage Block List" use case that these methods implement to iteration 2
    fun findBlockedContactById(userId : String) : User? {
        // TODO
        return User()
    }

    fun addBlockedContact(user: User) : Boolean {
        // TODO
        return false
    }

    fun deleteBlockedContact(user: User) : Boolean {
        // TODO
        return false
    }*/

    // Sends text message to server
    fun sendTextMsg(msg : String, convo : Conversation){
        val recipient = if (convo.user1Id == this.userId) convo.user2Id else convo.user1Id
        val timestamp = Instant.now().toEpochMilli()
        val txtMsg = TextMessage(msg, convo.convoId,this.userId, recipient,timestamp)
        sendEncryptedMsg(txtMsg, convo)
    }

    // Sends image message to server
    /*fun sendImageMsg(byteArray: ByteArray, convo : Conversation){
        val recipient = if (convo.user1Id == this.userId) convo.user2Id else convo.user1Id
        val timestamp = Instant.now().toEpochMilli()
        val txtMsg = ImageMessage(byteArray, convo.convoId,this.userId, recipient,timestamp)
        sendEncryptedMsg(txtMsg, convo)
    }*/

    private fun sendEncryptedMsg(unencryptedMsg: UnencryptedMessage, convo: Conversation) {
        val msg = device.cipher.encryptUnencryptedMessage(unencryptedMsg)
        val toSend = hashMapOf(
            "bucket_url" to "",
            "data" to msg.message.toString(Charsets.UTF_8),
            "message_type" to msg.messageType,
            "sender_id" to msg.senderId,
            "recipient_id" to msg.recipientId,
            "timestamp" to msg.timestamp
        )

        db.collection("conversations")
            .document(convo.convoId)
            .collection("messages")
            .document()
            .set(toSend)
            .addOnSuccessListener {
                convo.addMessage(msg)
                Log.d("sendTextMsg", "success")
            }
            .addOnFailureListener {
                Log.d("sendTextMsg", "failure")
            }
    }

    // Gets and stores public key of user from server
    fun getUserPublicKey(userId : String) : Boolean{
        val keyFactory : KeyFactory = KeyFactory.getInstance("RSA")
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val keyField = documentSnapshot.getString("publicKey")!!
                Log.d("hello",keyField)
                val publicKey: PublicKey = keyFactory.generatePublic(X509EncodedKeySpec(Base64.getDecoder().decode(keyField)))

                val fileUserId = if (this.userId == userId) "myKey" else userId

                device.addUserPublicKey(fileUserId, publicKey)
            }
        return false
    }

    // Handle incoming message from server
    fun receiveMsg(encryptedMsg: EncryptedMessage) : Message {
        return device.cipher.decryptEncryptedMessage(encryptedMsg)
    }

    // Add your own public key to server
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

    // FIX: Write unit tests for this
    fun addSelfToDatabase() : Boolean {
        var base64EncodedPublicKey =
            Base64.getEncoder().encodeToString(device.cipher.publicKeyRing["myKey"]?.encoded)

        val toAdd = hashMapOf(
            "name" to this.userId,
            "canonicalId" to this.userId,
            "first_name" to this.firstName,
            "last_name" to this.lastName,
            "password_hash" to "",
            "phone" to "123",
            "publicKey" to base64EncodedPublicKey
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