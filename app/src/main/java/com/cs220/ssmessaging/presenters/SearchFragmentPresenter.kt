package com.cs220.ssmessaging.presenters

class SearchFragmentPresenter {
    private var view: View? = null

    fun attachView(view: View) {
        this.view = view
    }

    fun detachView() {
        this.view = null
    }

    fun search(query: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    interface View {
        fun showQueryRequiredMessage()
        fun showSearchResults(query: String)
    }
}