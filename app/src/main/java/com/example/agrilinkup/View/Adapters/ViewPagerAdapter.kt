package com.example.agrilinkup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentManager:FragmentManager,lifeCycle:Lifecycle):
    FragmentStateAdapter(fragmentManager,lifeCycle) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            1 -> ChatFragment()
            2 ->  CartFragment()
            3 -> ProfileFragment()
            else -> HomeFragment()
        }
    }
}