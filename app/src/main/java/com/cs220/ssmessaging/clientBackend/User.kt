package com.cs220.ssmessaging.clientBackend

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import com.cs220.ssmessaging.clientBackend.Conversation
import com.cs220.ssmessaging.clientBackend.Device
import com.cs220.ssmessaging.clientBackend.Message
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
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

    //val db = FirebaseFirestore.getInstance()

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
    var contacts : MutableList<User> = mutableListOf()
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
            "users" to arrayListOf<String>(convo.user1.userId, convo.user2.userId)
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

    fun getConversationByUserId(recipientId : String) : Conversation? {
        // TODO: validity checks
        var retConvoList: List<Conversation> = this.conversations
            .filter { x -> (x.user1.userId == recipientId || x.user2.userId == recipientId)}
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

    fun addContact(user : User) : Boolean {
        if(user in this.contacts) {
            return false
        }
        this.contacts.add(user)
        return true
    }

    fun getContactById(userId : String) : User? {
        var retUserList: List<User> = this.contacts.filter {x -> x.userId == userId}
        if (retUserList.isEmpty()) {
            return null
        }
        return retUserList.single()
    }

    fun deleteContact(user : User) : Boolean {
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
    fun sendTextMsg(msg : String, convo : Conversation) : Boolean {
        // TODO
        return false
    }

    // Sends image message to server
    fun sendImageMsg(byteArray: ByteArray, convo : Conversation) : Boolean {
        // TODO
        return false
    }

    // Gets and stores public key of user from server
    fun getUserPublicKey(userId : String) : Boolean{
        // TODO
        return false
    }

    // Handle incoming message from server
    fun receiveMsg(encryptedMsg: EncryptedMessage) : Message {
        // TODO
        return ImageMessage(ByteArray(0),"", User(), User(), -1)
    }

    // Add your own public key to server
    fun addPublicKeyToServer(key : String, user : User) : Boolean {
        // TODO
        return false
    }
}