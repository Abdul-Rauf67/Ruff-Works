package com.example.agrilinkup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.agrilinkup.ChatFragment
import com.example.agrilinkup.View.Fragments.UserAccount.UserAccountFragment

class ViewPagerAdapter(fragmentManager:FragmentManager,lifeCycle:Lifecycle):
    FragmentStateAdapter(fragmentManager,lifeCycle) {

        private var fragments = arrayListOf(
            HomeFragment(),
            ChatFragment(),
            CartFragment(),
            UserAccountFragment()
        )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun replaceFragment(position: Int,newFragment: Fragment){
        fragments[position]=newFragment
        notifyDataSetChanged()
    }
}