package com.example.agrilinkup

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(){


    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
//
//        drawerLayout = findViewById(R.id.drawer_layout)
//        navView = findViewById(R.id.nav_view)
//
//        val toggle = ActionBarDrawerToggle(
//            this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close)
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
//        navView.setNavigationItemSelectedListener(this)

        val TabTitles= arrayOf("Home","Chats","Cart","Profile")
        val TabIcons=arrayOf(R.drawable.home_icon,R.drawable.chats_icon,R.drawable.shipping_cart_icon,R.drawable.mange_account_icon)

        viewPager2=findViewById<ViewPager2>(R.id.ViewpagerTwo)
        val layoutTab=findViewById<TabLayout>(R.id.tabLayout)

        val adapter=ViewPagerAdapter(supportFragmentManager,lifecycle)
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


    fun switchToFragment(fragmentIndex:Int) : Boolean {
        viewPager2.currentItem=fragmentIndex
        return true
    }




//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//
//            R.id.nav_update -> {
//                Toast.makeText(this, "Update clicked", Toast.LENGTH_SHORT).show()
//            }
//            R.id.nav_logout -> {
//                Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show()
//            }
//        }
//        drawerLayout.closeDrawer(GravityCompat.START)
//        return true
//    }


}