package com.example.agrilinkup.View.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.agrilinkup.MainActivity
import com.example.agrilinkup.R
import com.example.agrilinkup.SignUpActivity
import com.example.agrilinkup.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=DataBindingUtil.setContentView(this,R.layout.activity_login)

        binding.registerBtn.setOnClickListener {
           startActivity(Intent(this,MainActivity::class.java))
        }

        binding.register.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}