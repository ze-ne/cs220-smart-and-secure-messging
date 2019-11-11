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
    val recipient : User
    val timestamp : Int // This should be epoch number
    companion object{
        // Need to unit test
        fun isValidTimestamp(timestamp: Int) : Boolean{
            return timestamp >= 0
        }
    }
}

class EncryptedMessage(message : ByteArray, conversationId : String,
                       messageType : String, sender : User, recipient : User, timestamp : Int) : Message {

    companion object{
        // Need to unit test
        fun isValidMessage(encryptedMessage: EncryptedMessage) : Boolean =
            isValidMessageBody(encryptedMessage.message) &&
            isValidMessageType(encryptedMessage.messageType) &&
            User.isValidUser(encryptedMessage.sender) &&
            User.isValidUser(encryptedMessage.recipient) &&
            Message.isValidTimestamp(encryptedMessage.timestamp) &&
            Conversation.isValidConversationId(encryptedMessage.conversationId)

        // Need to unit test
        fun isValidMessageBody(byteArray: ByteArray) : Boolean = byteArray.isNotEmpty()

        // Need to unit test
        fun isValidMessageType(type : String) : Boolean = (type == "text") || (type == "image")
    }

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

    override val recipient : User
        get(){
            // TODO
            return User()
        }

    override val timestamp : Int
        get(){
            // TODO
            return -1
        }

    // Message needs to be not empty
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
                  sender : User, recipient : User, timestamp : Int) : UnencryptedMessage {

    companion object {
        // Need to unit test
        fun isValidMessage(textMessage : TextMessage) : Boolean =
            isValidMessageBody(textMessage.message) &&
            User.isValidUser(textMessage.sender) &&
            User.isValidUser(textMessage.recipient) &&
            Message.isValidTimestamp(textMessage.timestamp) &&
            Conversation.isValidConversationId(textMessage.conversationId)

        // Need to unit test
        fun isValidMessageBody(msg : String) : Boolean = msg.isNotEmpty()
    }

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

    override val recipient : User
        get(){
            // TODO
            return User()
        }

    override val timestamp : Int
        get(){
            // TODO
            return -1
        }

    // Message needs to be not empty
    val message : String
        get(){
            // TODO
            return "TODO"
        }
}

class ImageMessage(message : ByteArray, conversationId : String,
                   sender : User, recipient: User, timestamp : Int) : UnencryptedMessage {

    companion object{
        // Need to unit test
        fun isValidMessage(imageMessage: ImageMessage) : Boolean =
            isValidMessageBody(imageMessage.message) &&
            isValidPathToImage(imageMessage.pathToImage) &&
            User.isValidUser(imageMessage.sender) &&
            User.isValidUser(imageMessage.recipient) &&
            Message.isValidTimestamp(imageMessage.timestamp) &&
            Conversation.isValidConversationId(imageMessage.conversationId)

        // Need to unit test
        fun isValidMessageBody(byteArray: ByteArray) : Boolean = byteArray.isNotEmpty()

        // Need to unit test
        fun isValidPathToImage(path : String) : Boolean = path.matches(Regex("^[a-zA-Z0-9./:_-]*$"))
    }

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

    override val recipient : User
        get(){
            // TODO
            return User()
        }

    override val timestamp : Int
        get(){
            // TODO
            return -1
        }

    // Message needs to be not empty
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