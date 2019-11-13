package com.cs220.ssmessaging.frontend.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.cs220.ssmessaging.frontend.fragments.ContactsFragment
import com.cs220.ssmessaging.frontend.fragments.ConversationsListFragment

class HomeTabAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ConversationsListFragment()
            1 -> ContactsFragment()
            else -> ConversationsListFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Conversations"
            1 -> "Contacts"
            else -> ""
        }
    }
}