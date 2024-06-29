package com.example.agrilinkup.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.agrilinkup.R
import com.example.agrilinkup.ViewPagerAdapter
import com.example.agrilinkup.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var binding:FragmentMainBinding
    lateinit var toolbar: Toolbar
    private lateinit var viewPager2: ViewPager2
    lateinit var adapter: ViewPagerAdapter
    private lateinit var layoutTab:TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        viewPager2 = binding.ViewpagerTwo
        layoutTab = binding.tabLayout

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabTitles= arrayOf("Home","Chats","Cart","Account")
        val tabIcons=arrayOf(R.drawable.home_icon,R.drawable.chats_icon,R.drawable.shipping_cart_icon,R.drawable.mange_account_icon)

//
//        val navHostFragment=supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController=navHostFragment.navController


        adapter=ViewPagerAdapter(childFragmentManager,lifecycle)

        viewPager2.adapter=adapter

        TabLayoutMediator(layoutTab,viewPager2){tab,position ->
            tab.text=tabTitles[position]
            tab.setIcon(tabIcons[position])
            /* when(position){
             1->R.drawable.chats_icon
             else -> R.drawable.home_icon
             }
             */
        }.attach()

    }

    fun switchToFragment(fragmentIndex:Int) : Boolean {
        viewPager2.currentItem=fragmentIndex
        return true
    }

    fun replaceFragmentsInViewpager(fragment: Fragment,index:Int):Boolean{
        adapter.replaceFragment(index,fragment)
        viewPager2.adapter=adapter
        switchToFragment(index)
        return true
    }
}