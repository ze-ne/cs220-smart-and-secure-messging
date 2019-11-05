package com.cs220.ssmessaging.presenters

class SearchFragmentPresenter {
    private val view: View? = null

    fun search(query: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    interface View {
        fun showQueryRequiredMessage()
        fun showSearchResults(query: String)
    }
}