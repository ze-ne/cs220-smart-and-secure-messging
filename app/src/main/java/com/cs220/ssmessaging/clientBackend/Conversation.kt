package com.cs220.ssmessaging.clientBackend
import android.media.Image
import com.cs220.ssmessaging.clientBackend.User
import com.cs220.ssmessaging.clientBackend.Message
import com.ibm.cloud.sdk.core.security.IamAuthenticator
import com.ibm.watson.tone_analyzer.v3.ToneAnalyzer
import com.ibm.watson.tone_analyzer.v3.model.ToneChatOptions
import com.ibm.watson.tone_analyzer.v3.model.Utterance
import com.ibm.watson.tone_analyzer.v3.model.UtteranceAnalyses
import org.json.JSONObject

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

    private val authenticator = IamAuthenticator("niLJk0JS8fRbqrqFmPoRgqQMt9PnWMC7EKOfCZHty9no")
    private val toneAnalyzer = ToneAnalyzer("8.0.1", authenticator)
    private val WATSON_URL = "https://gateway.watsonplatform.net/tone-analyzer/api"

    constructor(firstUser : String, secondUser : String, msgs : MutableList<Message>) : this(){

        // Check if all messages are valid is msgs
        var validMessages : Boolean = true
        for(m in msgs){
            // If setMessage ever becomes false, we break and don't set the messages
            if(!validMessages)
                break

            when(m){
                is EncryptedMessage -> validMessages = EncryptedMessage.isValidMessage(m)
                is TextMessage -> validMessages = TextMessage.isValidMessage(m)
                is ImageMessage -> validMessages = ImageMessage.isValidMessage(m)
            }
        }

        // The messages in the message list are valid and also the users
        if(validMessages && User.isValidUserId(firstUser) && User.isValidUserId(secondUser)){
            user1Id = firstUser
            user2Id = secondUser
            _messages = msgs
            convoId = user1Id + "-" +  user2Id
        }
    }

    constructor(firstUser : String, secondUser : String) : this(){
        // Users are valid, so set the vals
        if(User.isValidUserId(firstUser) && User.isValidUserId(secondUser)){
            user1Id = firstUser
            user2Id = secondUser
            convoId = user1Id + "-" +  user2Id
        }
    }

    companion object{
        fun isValidConversation(conversation: Conversation) =
            User.isValidUserId(conversation.user1Id) &&
            User.isValidUserId(conversation.user2Id) &&
            isValidConversationId(conversation.convoId) &&
            isValidLastTimeSynched(conversation.lastTimeSynced)

        fun isValidConversationId(conversationId : String) : Boolean =
            conversationId.matches(Regex("^[a-zA-Z0-9_.,/-]*$")) && conversationId.isNotEmpty()

        fun isValidLastTimeSynched(timeSynced : Long) : Boolean = (timeSynced >= 0)
    }

    // The following three properties use backing properties since we want to be able to change user directly if need be
    private var _userId : String = ""
    var user1Id : String
        get(){
            return _userId
        }
        set(user : String) {
            if(User.isValidUserId(user)){
                _userId = user
            }
        }


    private var _user2Id : String = ""
    var user2Id : String
        get(){
            return _user2Id
        }
        set(user : String) {
            if(User.isValidUserId(user)){
                _user2Id = user
            }
        }

    private var _messages : MutableList<Message> = mutableListOf()
    var messages : MutableList<Message>
        get() {
            return _messages
        }
        set(messageList){
            // Only set messages  only if every message is valid
            var setMessages : Boolean = true
            for(m in messageList){
                // If setMessage ever becomes false, we break and don't set the messages
                if(!setMessages)
                    break

                when(m){
                    is EncryptedMessage -> setMessages = EncryptedMessage.isValidMessage(m)
                    is TextMessage -> setMessages = TextMessage.isValidMessage(m)
                    is ImageMessage -> setMessages = ImageMessage.isValidMessage(m)
                }
            }
            if(setMessages)
                _messages = messageList
        }

    var convoId : String = ""
        get() {
            return field
        }
        private set

    var lastTimeSynced : Long = 0
        get() {
            return field
        }
        set(timeSynced : Long){
            if(isValidLastTimeSynched(timeSynced))
                field = timeSynced
        }

    fun addMessage(msg : Message) : Boolean{
        var addMessage : Boolean = false
        when(msg){
            is EncryptedMessage -> addMessage = EncryptedMessage.isValidMessage(msg)
            is TextMessage -> addMessage = TextMessage.isValidMessage(msg)
            is ImageMessage -> addMessage = ImageMessage.isValidMessage(msg)
        }

        for(m in messages){
            if(m.mEquals(msg)){
                addMessage = false
                break
            }
        }

        if(addMessage) {
            _messages.add(msg)
            // Last time synched must also be updated
            lastTimeSynced = msg.timestamp
        }

        return addMessage
    }


    // Iteration 2
    fun removeMessage(msg: Message): Boolean {
        return messages.remove(msg)
    }

    fun getSubConversation(startTimestamp : Long, endTimestamp : Long) : Conversation {
        var subMessages = messages.filter {it.timestamp in startTimestamp..endTimestamp} as MutableList<Message>
        return Conversation(user1Id, user2Id, subMessages)
    }

    fun getAnalytics() : UtteranceAnalyses {
        //TODO
        /* might exclude from unit testing on account of having a
           relatively low monthly limit on api calls before we have to pay */

        val utterances = _messages
            .filterIsInstance<TextMessage>()
            .map {
                Utterance.Builder()
                    .user(it.senderId)
                    .text(it.message)
                    .build()
            }

        toneAnalyzer.serviceUrl = WATSON_URL

        val toneChatOptions = ToneChatOptions.Builder()
            .utterances(utterances)
            .build()
        val utteranceAnalyses = toneAnalyzer.toneChat(toneChatOptions).execute().result

        return utteranceAnalyses
    }
}