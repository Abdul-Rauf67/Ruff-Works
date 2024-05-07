package com.example.agrilinkup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.agrilinkup.ChatFragment

class ViewPagerAdapter(fragmentManager:FragmentManager,lifeCycle:Lifecycle):
    FragmentStateAdapter(fragmentManager,lifeCycle) {

        private var fragments = arrayListOf(
            HomeFragment(),
            ChatFragment(),
            CartFragment(),
            ProfileFragment()
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