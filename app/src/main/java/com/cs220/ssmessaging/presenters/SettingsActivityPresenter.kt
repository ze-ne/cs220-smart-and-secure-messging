package com.cs220.ssmessaging.presenters

class SettingsActivityPresenter : BasePresenter<SettingsActivityPresenter.View>() {

    fun changeFirstName (text: String) {
        TODO("not implemented")
    }

    fun changeLastName (text: String){
        TODO("not implemented")
    }

    fun logout (){
        TODO("not implemented")
    }

    interface View {
        fun changeFirstNameSuccessful()
        fun changeFirstNameFail ()
        fun changeLastNameSuccessful()
        fun changeLastNameFail ()
        fun logoutSuccessful ()
        fun logoutFail()
    }
}