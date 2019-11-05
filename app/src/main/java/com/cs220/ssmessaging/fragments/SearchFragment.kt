package com.cs220.ssmessaging.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.presenters.SearchFragmentPresenter

class SearchFragment : Fragment(), SearchFragmentPresenter.View {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun showQueryRequiredMessage() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showSearchResults(query: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}