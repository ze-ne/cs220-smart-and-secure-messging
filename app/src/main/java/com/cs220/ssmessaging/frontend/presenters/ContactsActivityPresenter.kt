package com.cs220.ssmessaging.frontend.presenters

class ContactsActivityPresenter : BasePresenter<ContactsActivityPresenter.View>() {

    fun addContact(userID: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    interface View {
        fun showConfirmationMessage()
        fun showErrorMessage()
        fun updateContactsList(userID: String)
    }
}