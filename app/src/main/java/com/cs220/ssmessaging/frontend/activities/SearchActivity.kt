package com.cs220.ssmessaging.frontend.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.frontend.presenters.SearchActivityPresenter

// TODO: Implementation moved to iteration 2
class SearchActivity : AppCompatActivity(), SearchActivityPresenter.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_list)
    }

    override fun showQueryRequiredMessage() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showSearchResults(query: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}