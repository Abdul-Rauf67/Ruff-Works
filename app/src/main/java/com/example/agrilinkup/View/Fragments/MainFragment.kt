package com.example.agrilinkup.View.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.agrilinkup.R
import com.example.agrilinkup.ViewPagerAdapter
import com.example.agrilinkup.databinding.FragmentChatBinding
import com.example.agrilinkup.databinding.FragmentMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding:FragmentMainBinding

            lateinit var toolbar: Toolbar
    lateinit var viewPager2: ViewPager2
    lateinit var adapter: ViewPagerAdapter
    lateinit var layoutTab:TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        binding = FragmentMainBinding.inflate(inflater, container, false)

        viewPager2 = binding.ViewpagerTwo
        layoutTab = binding.tabLayout

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val TabTitles= arrayOf("Home","Chats","Cart","Account")
        val TabIcons=arrayOf(R.drawable.home_icon,R.drawable.chats_icon,R.drawable.shipping_cart_icon,R.drawable.mange_account_icon)

//
//        val navHostFragment=supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController=navHostFragment.navController


        adapter=ViewPagerAdapter(childFragmentManager,lifecycle)

        viewPager2.adapter=adapter

        TabLayoutMediator(layoutTab,viewPager2){tab,position ->
            tab.text=TabTitles[position]
            tab.setIcon(TabIcons[position])
            /* when(position){
             1->R.drawable.chats_icon
             else -> R.drawable.home_icon
             }
             */
        }.attach()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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