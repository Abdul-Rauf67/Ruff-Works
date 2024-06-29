package com.example.agrilinkup.view.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.agrilinkup.view.fragments.CommunityFragment
import com.example.agrilinkup.view.fragments.GroupsFragment
import com.example.agrilinkup.view.fragments.InboxFragment


class ViewPagerAdapterChats(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return 3 // Number of fragments you want to slide between
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> InboxFragment()
            1 -> GroupsFragment()
            else -> CommunityFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Inbox"
            1 -> "Groups"
            2 -> "Community"
            else -> null
        }
    }
}