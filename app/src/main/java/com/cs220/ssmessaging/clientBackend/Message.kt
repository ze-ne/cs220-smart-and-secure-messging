package com.cs220.ssmessaging.clientBackend

import android.graphics.Bitmap
import java.lang.IllegalArgumentException

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
    fun mEquals(m: Message) : Boolean // decides whether the current message equals another message
    companion object{
        // Need to unit test
        fun isValidTimestamp(timestamp: Int) : Boolean{
            return timestamp >= 0
        }
    }
}

class EncryptedMessage(_message : ByteArray, _conversationId : String,
                       _messageType : String, _sender : User, _recipient : User, _timestamp : Int) : Message {

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
    override val sender : User
    override val recipient : User
    override val timestamp : Int

    // Need to write unit test
    override fun mEquals(m : Message) : Boolean{
        return if(m is UnencryptedMessage)
            false
        else {
            ((m as EncryptedMessage).conversationId == conversationId) &&
            (m.sender.userId == sender.userId) &&
            (m.recipient.userId == recipient.userId) &&
            (m.timestamp == timestamp) &&
            (m.message.contentEquals(message)) &&
            (m.messageType == messageType)
        }
    }
    val message : ByteArray
    val messageType : String
    init {
        if( isValidMessageBody(_message) &&
            User.isValidUser(_sender) &&
            User.isValidUser(_recipient) &&
            Message.isValidTimestamp(_timestamp) &&
            Conversation.isValidConversationId(_conversationId) &&
            isValidMessageType(_messageType)){

            conversationId = _conversationId
            sender = _sender
            recipient = _recipient
            timestamp = _timestamp
            message = _message
            messageType = _messageType
        }else{
            conversationId = ""
            sender = User("","","")
            recipient = User("","","")
            timestamp = 0
            message = ByteArray(0)
            messageType = ""
        }
    }
}

interface UnencryptedMessage : Message {
}

class TextMessage(_message : String, _conversationId : String,
                  _sender : User, _recipient : User, _timestamp : Int) : UnencryptedMessage {

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
    override val sender : User
    override val recipient : User
    override val timestamp : Int
    val message : String

    // Need to write unit test
    override fun mEquals(m : Message) : Boolean{
        return if(m is ImageMessage|| m is EncryptedMessage)
            false
        else {
            ((m as TextMessage).conversationId == conversationId) &&
            (m.sender.userId == sender.userId) &&
            (m.recipient.userId == recipient.userId) &&
            (m.timestamp == timestamp) &&
            (m.message == message)
        }
    }

    init {
        if( isValidMessageBody(_message) &&
            User.isValidUser(_sender) &&
            User.isValidUser(_recipient) &&
            Message.isValidTimestamp(_timestamp) &&
            Conversation.isValidConversationId(_conversationId)){
            conversationId = _conversationId
            sender = _sender
            recipient = _recipient
            timestamp = _timestamp
            message = _message
        }else{
            conversationId = ""
            sender = User("","","")
            recipient = User("","","")
            timestamp = 0
            message = ""
        }
    }

}

class ImageMessage(_message : ByteArray, _conversationId : String,
                   _sender : User, _recipient: User, _timestamp : Int) : UnencryptedMessage {

    companion object{
        // Need to unit test
        fun isValidMessage(imageMessage: ImageMessage) : Boolean =
            isValidMessageBody(imageMessage.message) &&
            User.isValidUser(imageMessage.sender) &&
            User.isValidUser(imageMessage.recipient) &&
            Message.isValidTimestamp(imageMessage.timestamp) &&
            Conversation.isValidConversationId(imageMessage.conversationId)

        // Need to unit test
        fun isValidMessageBody(byteArray: ByteArray) : Boolean = byteArray.isNotEmpty()

        // Need to unit test
        fun isValidPathToImage(path : String) : Boolean = path.matches(Regex("^[a-zA-Z0-9./:_-]*$")) && path.isNotEmpty()
    }

    override val conversationId : String
    override val sender : User
    override val recipient : User
    override val timestamp : Int
    val message : ByteArray
    var pathToImage : String = ""
        set(path : String){
            if(isValidPathToImage(path)){
                field = path
            }
        }

    // Need to write unit test
    override fun mEquals(m : Message) : Boolean{
        return if(m is EncryptedMessage || m is TextMessage)
            false
        else {
            ((m as ImageMessage).conversationId == conversationId) &&
            (m.sender.userId == sender.userId) &&
            (m.recipient.userId == recipient.userId) &&
            (m.timestamp == timestamp) &&
            (m.message.contentEquals(message))
        }
    }

    init {
        if( isValidMessageBody(_message) &&
            User.isValidUser(_sender) &&
            User.isValidUser(_recipient) &&
            Message.isValidTimestamp(_timestamp) &&
            Conversation.isValidConversationId(_conversationId)){
            conversationId = _conversationId
            sender = _sender
            recipient = _recipient
            timestamp = _timestamp
            message = _message
        }else{
            conversationId = ""
            sender = User("","","")
            recipient = User("","","")
            timestamp = 0
            message = ByteArray(0)
        }
    }
}