package com.cs220.ssmessaging.clientBackend
import com.cs220.ssmessaging.clientBackend.User
import com.cs220.ssmessaging.clientBackend.Message

class Conversation() {
    /* For TAs: all accesses (except for the constructor) in Kotlin must go through the getter and setter.
     * This is because variables are properties and all properties have a private field.
     * Therefore we achieve encapsulation Kotlin by defining custom getters and setters.
     * This style is standard Kotlin convention called "Backing Fields".
     * That is why in some cases we don't need to declare variables with private modifiers.
     * In the cases that we do need private modifiers, it is when we need "Backing Properties" OR
     * when we want the variable getter and setter to be private.
     * Please go here for more information: https://kotlinlang.org/docs/reference/properties.html
     */

    constructor(firstUser : User, secondUser : User, msgs : MutableList<Message>) : this(){
        // TODO
    }

    constructor(firstUser : User, secondUser : User) : this(){
        // TODO
    }

    var user1 : User
        get(){
            // TODO
            return User()
        }
        set(user : User) {
        }


    var user2 : User
        get() {
            // TODO
            return User()
        }
        set(user : User){
            // TODO
        }

    var messages : MutableList<Message>
        get() {
            // TODO
            return mutableListOf()
        }
        set(messageList){
            // TODO
        }

    val convoId : String
        get() {
            // TODO
            return ""
        }

    var lastTimeSynced : Int
        get() {
            // TODO
            return -1
        }
        set(timeSynced : Int){
            // TODO
        }

    fun addMessage(msg : Message) : Boolean{
        // TODO
        return false
    }

    fun sync() : Boolean{
        // TODO
        return false
    }
}