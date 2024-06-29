package com.example.agrilinkup.view.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.agrilinkup.R
import com.example.agrilinkup.view.fragments.ChatMessagesFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChattingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chating)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val messageFragment = ChatMessagesFragment()
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .add(R.id.fragment_container, messageFragment)
            .addToBackStack(null)
            .commit()
    }
}