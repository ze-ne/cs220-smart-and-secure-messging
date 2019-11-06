package com.cs220.ssmessaging.clientBackend

import android.graphics.Bitmap

/* For TAs: all accesses (except for the constructor) in Kotlin must go through the getter and setter.
 * This is because variables are properties and all properties have a private field.
 * Therefore we achieve encapsulation Kotlin by defining custom getters and setters.
 * This style is standard Kotlin convention called "Backing Fields".
 * That is why in some cases we don't need to declare variables with private modifiers.
 * In the cases that we do need private modifiers, it is when we need "Backing Properties".
 * Please go here for more information: https://kotlinlang.org/docs/reference/properties.html
 */

interface Message {
    val conversationId : String
    val sender : User
    val timestamp : Int // This should be epoch number
}

class EncryptedMessage(message : ByteArray, conversationId : String,
                       messageType : String, sender : User, timestamp : Int) : Message {

    override val conversationId : String
        get(){
            // TODO
            return "TODO"
        }

    override val sender : User
        get(){
            // TODO
            return User()
        }

    override val timestamp : Int
        get(){
            // TODO
            return -1
        }

    val message : ByteArray
        get(){
            // TODO
            return ByteArray(0)
        }

    // Message type can only be "image" or "text"
    val messageType : String
        get(){
            // TODO
            return "TODO"
        }
}

interface UnencryptedMessage : Message {
}

class TextMessage(message : String, conversationId : String,
                  sender : User, timestamp : Int) : UnencryptedMessage {

    override val conversationId : String
        get(){
            // TODO
            return "TODO"
        }

    override val sender : User
        get(){
            // TODO
            return User()
        }

    override val timestamp : Int
        get(){
            // TODO
            return -1
        }

    val message : String
        get(){
            // TODO
            return "TODO"
        }
}

class ImageMessage(message : ByteArray, conversationId : String,
                   sender : User, timestamp : Int) : UnencryptedMessage {
    override val conversationId : String
        get(){
            // TODO
            return "TODO"
        }

    override val sender : User
        get(){
            // TODO
            return User()
        }

    override val timestamp : Int
        get(){
            // TODO
            return -1
        }

    val message : ByteArray
        get(){
            // TODO
            return ByteArray(0)
        }

    var pathToImage : String
        get(){
            // TODO
            return "TODO"
        }
        set(imagePath : String){
        }
}