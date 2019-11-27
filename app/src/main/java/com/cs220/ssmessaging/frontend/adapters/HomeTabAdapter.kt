package com.cs220.ssmessaging.frontend.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.cs220.ssmessaging.frontend.fragments.BlockedListFragment
import com.cs220.ssmessaging.frontend.fragments.ConversationsListFragment
import com.cs220.ssmessaging.frontend.fragments.SearchFragment

class HomeTabAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ConversationsListFragment()
            1 -> BlockedListFragment()
            2 -> SearchFragment()
            else -> ConversationsListFragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Conversations"
            1 -> "Blocked Users"
            2 -> "Search for Users"
            else -> ""
        }
    }
}