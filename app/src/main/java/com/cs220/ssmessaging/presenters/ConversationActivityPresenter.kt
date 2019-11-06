package com.cs220.ssmessaging.presenters

import com.cs220.ssmessaging.clientBackend.Message

class ConversationActivityPresenter : BasePresenter<ConversationActivityPresenter.View>() {

    fun sendTextMessage(text: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun sendImageMessage(image: Array<Byte>){
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    interface View {
        fun updateDisplayedMessages(newMessage: Message)
    }
}