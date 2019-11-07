package com.cs220.ssmessaging.frontend.presenters

class SettingsActivityPresenter : BasePresenter<ConversationActivityPresenter.View>() {

    fun sendTextMessage(text: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun sendImageMessage(image: Array<Byte>){
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun deleteSentTextMessage(text: String){
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun deleteSentImageMessage(image: Array<Byte>){
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    interface View {
        fun updateDisplayedMessages()
    }
}