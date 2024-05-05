package com.example.agrilinkup.View.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import com.example.agrilinkup.R
import com.example.agrilinkup.View.Fragments.Chat_Messages_Fragment

class Chating_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chating)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val messageFragment=Chat_Messages_Fragment()
        val fragmentManager=supportFragmentManager
        fragmentManager.beginTransaction()
            .add(R.id.fragment_container,messageFragment)
            .addToBackStack(null)
            .commit()

    }
}