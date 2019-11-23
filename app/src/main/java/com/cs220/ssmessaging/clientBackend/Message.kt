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
    val senderId : String
    val recipientId : String
    val timestamp : Long // This should be epoch number
    fun mEquals(m: Message) : Boolean // decides whether the current message equals another message
    companion object{
        // Need to unit test
        fun isValidTimestamp(timestamp: Long) : Boolean{
            return timestamp >= 0
        }
    }
}

// Note: Add one more parameter and field: aesKey for symmetric encryption.
// We do not check its validity (unless it is empty).
class EncryptedMessage(_message : ByteArray, _conversationId : String,
                       _messageType : String, _senderId : String, _recipientId : String,
                       _timestamp : Long, _encryptedAESKey : ByteArray) : Message {

    companion object{
        // Need to unit test
        fun isValidMessage(encryptedMessage: EncryptedMessage) : Boolean =
            isValidMessageBody(encryptedMessage.message) &&
            isValidMessageType(encryptedMessage.messageType) &&
            User.isValidUserId(encryptedMessage.senderId) &&
            User.isValidUserId(encryptedMessage.recipientId) &&
            Message.isValidTimestamp(encryptedMessage.timestamp) &&
            Conversation.isValidConversationId(encryptedMessage.conversationId) &&
            (encryptedMessage.encryptedAESKey.isNotEmpty())

        // Need to unit test
        fun isValidMessageBody(byteArray: ByteArray) : Boolean = byteArray.isNotEmpty()

        // Need to unit test
        fun isValidMessageType(type : String) : Boolean = (type == "text") || (type == "image")
    }

    override val conversationId : String
    override val senderId : String
    override val recipientId : String
    override val timestamp : Long

    // Need to write unit test
    override fun mEquals(m : Message) : Boolean{
        return if(m is UnencryptedMessage)
            false
        else {
            ((m as EncryptedMessage).conversationId == conversationId) &&
            (m.senderId == senderId) &&
            (m.recipientId == recipientId) &&
            (m.timestamp == timestamp) &&
            (m.message.contentEquals(message)) &&
            (m.messageType == messageType) &&
            (m.encryptedAESKey.equals(encryptedAESKey))
        }
    }
    val message : ByteArray
    val messageType : String
    val encryptedAESKey : ByteArray
    init {
        if( isValidMessageBody(_message) &&
            User.isValidUserId(_senderId) &&
            User.isValidUserId(_recipientId) &&
            Message.isValidTimestamp(_timestamp) &&
            Conversation.isValidConversationId(_conversationId) &&
            isValidMessageType(_messageType) &&
            _encryptedAESKey.isNotEmpty()){

            conversationId = _conversationId
            senderId = _senderId
            recipientId = _recipientId
            timestamp = _timestamp
            message = _message
            messageType = _messageType
            encryptedAESKey = _encryptedAESKey
        }else{
            conversationId = ""
            senderId = ""
            recipientId = ""
            timestamp = 0
            message = ByteArray(0)
            messageType = ""
            encryptedAESKey = ByteArray(0)
        }
    }
}

interface UnencryptedMessage : Message {
    var isVisible : Boolean
}

class TextMessage(_message : String, _conversationId : String,
                  _senderId : String, _recipientId : String, _timestamp : Long) : UnencryptedMessage {

    companion object {
        fun isValidMessage(textMessage : TextMessage) : Boolean =
            isValidMessageBody(textMessage.message) &&
            User.isValidUserId(textMessage.senderId) &&
            User.isValidUserId(textMessage.recipientId) &&
            Message.isValidTimestamp(textMessage.timestamp) &&
            Conversation.isValidConversationId(textMessage.conversationId)

        fun isValidMessageBody(msg : String) : Boolean = msg.isNotEmpty()
    }

    override val conversationId : String
    override val senderId : String
    override val recipientId : String
    override val timestamp : Long
    override var isVisible : Boolean = true

    val message : String

    override fun mEquals(m : Message) : Boolean{
        return if(m is ImageMessage|| m is EncryptedMessage)
            false
        else {
            ((m as TextMessage).conversationId == conversationId) &&
            (m.senderId == senderId) &&
            (m.recipientId == recipientId) &&
            (m.timestamp == timestamp) &&
            (m.message == message)
        }
    }

    init {
        if( isValidMessageBody(_message) &&
            User.isValidUserId(_senderId) &&
            User.isValidUserId(_recipientId) &&
            Message.isValidTimestamp(_timestamp) &&
            Conversation.isValidConversationId(_conversationId)){
            conversationId = _conversationId
            senderId = _senderId
            recipientId = _recipientId
            timestamp = _timestamp
            message = _message
        }else{
            conversationId = ""
            senderId = ""
            recipientId = ""
            timestamp = 0
            message = ""
        }
    }

}

class ImageMessage(_message : ByteArray, _conversationId : String,
                   _senderId : String, _recipientId: String, _timestamp : Long) : UnencryptedMessage {

    companion object{
        fun isValidMessage(imageMessage: ImageMessage) : Boolean =
            isValidMessageBody(imageMessage.message) &&
            User.isValidUserId(imageMessage.senderId) &&
            User.isValidUserId(imageMessage.recipientId) &&
            Message.isValidTimestamp(imageMessage.timestamp) &&
            Conversation.isValidConversationId(imageMessage.conversationId)

        fun isValidMessageBody(byteArray: ByteArray) : Boolean = byteArray.isNotEmpty()

        fun isValidPathToImage(path : String) : Boolean = path.matches(Regex("^[a-zA-Z0-9./:_-]*$")) && path.isNotEmpty()
    }

    override val conversationId : String
    override val senderId : String
    override val recipientId : String
    override val timestamp : Long
    override var isVisible : Boolean = true

    val message : ByteArray
    var pathToImage : String = ""
        set(path : String){
            if(isValidPathToImage(path)){
                field = path
            }
        }

    override fun mEquals(m : Message) : Boolean{
        return if(m is EncryptedMessage || m is TextMessage)
            false
        else {
            ((m as ImageMessage).conversationId == conversationId) &&
            (m.senderId == senderId) &&
            (m.recipientId == recipientId) &&
            (m.timestamp == timestamp) &&
            (m.message.contentEquals(message))
        }
    }

    init {
        if( isValidMessageBody(_message) &&
            User.isValidUserId(_senderId) &&
            User.isValidUserId(_recipientId) &&
            Message.isValidTimestamp(_timestamp) &&
            Conversation.isValidConversationId(_conversationId)){
            conversationId = _conversationId
            senderId = _senderId
            recipientId = _recipientId
            timestamp = _timestamp
            message = _message
        }else{
            conversationId = ""
            senderId = ""
            recipientId = ""
            timestamp = 0
            message = ByteArray(0)
        }
    }
}