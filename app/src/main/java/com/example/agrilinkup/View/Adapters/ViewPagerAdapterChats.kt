package com.example.agrilinkup.View.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.agrilinkup.CartFragment
import com.example.agrilinkup.ChatFragment
import com.example.agrilinkup.HomeFragment
import com.example.agrilinkup.ProfileFragment
import com.example.agrilinkup.View.Fragments.CommunityFragment
import com.example.agrilinkup.View.Fragments.GroupsFragment
import com.example.agrilinkup.View.Fragments.InboxFragment


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