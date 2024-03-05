package com.example.agrilinkup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var bottomNavigationBar=findViewById<BottomNavigationView>(R.id.bottomNavigationBar)

        bottomNavigationBar.setOnItemReselectedListener {
            when(it.itemId){
                R.id.HomeFragment -> homeFragment()
                R.id.chatsFragment -> chatsFragment()
                R.id.CartFragment -> cartFragment()
                R.id.profileFragment -> profileFragment()
            }
        }



    }
    fun homeFragment(){
            val fragmentManager: FragmentManager = supportFragmentManager

            val fragments = fragmentManager?.fragments
            if (fragments != null) {
                for (fragment in fragments) {
                    if (fragment.tag == "HomeFragment"){

                        //we don't want to create a new Fragment each time the user presses the button
                        // return
                    }
                }
            }

            val fragment: Fragment = HomeFragment()
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

            fragmentTransaction.add(R.id.host, fragment, "HomeFragment")

            fragmentTransaction.addToBackStack("HomeFragment")

            fragmentTransaction.commit()
    }
    fun chatsFragment(){
        val fragmentManager: FragmentManager = supportFragmentManager

        val fragments = fragmentManager?.fragments
        if (fragments != null) {
            for (fragment in fragments) {
                if (fragment.tag == "ChatFragment"){

                    //we don't want to create a new Fragment each time the user presses the button
                    // return
                }
            }
        }

        val fragment: Fragment = ChatFragment()
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.host, fragment, "ChatFragment")

        fragmentTransaction.addToBackStack("ChatFragment")

        fragmentTransaction.commit()
    }
    fun cartFragment(){
        val fragmentManager: FragmentManager = supportFragmentManager

        val fragments = fragmentManager?.fragments
        if (fragments != null) {
            for (fragment in fragments) {
                if (fragment.tag == "CartFragment"){

                    //we don't want to create a new Fragment each time the user presses the button
                    // return
                }
            }
        }

        val fragment: Fragment = CartFragment()
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.host, fragment, "CartFragment")

        fragmentTransaction.addToBackStack("CartFragment")

        fragmentTransaction.commit()

    }
    fun profileFragment(){
        val fragmentManager: FragmentManager = supportFragmentManager

        val fragments = fragmentManager?.fragments
        if (fragments != null) {
            for (fragment in fragments) {
                if (fragment.tag == "ProfileFragment"){

                    //we don't want to create a new Fragment each time the user presses the button
                    // return
                }
            }
        }

        val fragment: Fragment = ProfileFragment()
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.host, fragment, "ProfileFragment")

        fragmentTransaction.addToBackStack("ProfileFragment")

        fragmentTransaction.commit()

    }
}