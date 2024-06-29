package com.example.agrilinkup.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.agrilinkup.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment=supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController=navHostFragment.navController

        //setupActionBarWithNavController(navController)

    }
    override fun onSupportNavigateUp(): Boolean {
        val navController=findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}