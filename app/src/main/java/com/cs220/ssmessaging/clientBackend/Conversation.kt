package com.cs220.ssmessaging.clientBackend
import com.cs220.ssmessaging.clientBackend.User
import com.cs220.ssmessaging.clientBackend.Message

class Conversation() {
    /* Note, all accesses (except for the constructor) in Kotlin must go through the getter and setter.
     * This is because variables are properties and all properties have a private field.
     * Therefore we achieve encapsulation Kotlin by defining custom getters and setters.
     * That is why in most cases we don't need to declare private variables.
     */
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
        private set(messageList){
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