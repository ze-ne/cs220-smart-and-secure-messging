package com.cs220.ssmessaging.presenters

class ConversationsListActivityPresenter : BasePresenter<ConversationsListActivityPresenter.View>() {

    fun startNewConversation(userID: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    interface View {
        fun gotoConversation(conversationID: String)
        fun showIDNotFoundMessage(userID: String)
        fun updateConversationsList(conversationID: String)
    }
}