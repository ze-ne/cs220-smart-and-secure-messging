package com.cs220.ssmessaging.clientBackend

import android.graphics.Bitmap
import android.widget.ImageView
import com.cs220.ssmessaging.clientBackend.Conversation
import com.cs220.ssmessaging.clientBackend.Device
import com.cs220.ssmessaging.clientBackend.Message

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

    constructor(userId : String, firstName: String, lastName: String) : this(){
        // TODO
    }

    val userId : String
        get(){
            // TODO
            return "TODO"
        }

    var firstName : String
        get(){
            // TODO
            return "TODO"
        }
        set(firstName : String){
            // TODO
        }

    var lastName : String
        get(){
            // TODO
            return "TODO"
        }
        set(lastName : String){
            // TODO
        }

    // We set the next two variables' setters to private because we don't want to expose the
    // default setter functions to the world
    var contacts : MutableList<User>
        // get returns copy of list
        get(){
            // TODO
            return mutableListOf()
        }
        private set(contacts : MutableList<User>){
            // TODO
        }

    var blockedContacts : MutableList<User>
        // get returns copy of list
        get() {
            // TODO
            return mutableListOf()
        }
        private set(contacts : MutableList<User>){
            // TODO
        }

    var conversations : MutableList<Conversation>
        // returns copy of conversations list
        get() {
            // TODO
            return mutableListOf()
        }
        private set(convos : MutableList<Conversation>){
            // TODO
        }

    var device : Device
        get(){
            // TODO
            return Device()
        }
        set(dvice : Device){
            // TODO
        }

    fun addContact(user : User) : Boolean {
        // TODO
        return false
    }

    fun findContactById(userId : String) : User? {
        // TODO
        return User()
    }

    fun deleteContact(user : User) : Boolean {
        // TODO
        return false
    }

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
    }

    fun sendTextMsg(msg : String, convo : Conversation) : Boolean {
        // TODO
        return false
    }

    fun sendImageMsg(bitmap: Bitmap, convo : Conversation) : Boolean {
        // TODO
        return false
    }

    fun receiveMsg(encryptedMsg: EncryptedMessage) : Message {
        // TODO
        return ImageMessage()
    }

    fun addPublicKeyToServer(key : String, user : User) : Boolean {
        // TODO
        return false
    }
}