package com.cs220.ssmessaging.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.presenters.SearchFragmentPresenter

class SearchActivity : AppCompatActivity(), SearchFragmentPresenter.View {
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