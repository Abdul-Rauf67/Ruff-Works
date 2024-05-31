package com.example.agrilinkup.View.Activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.agrilinkup.FirebaseModule_ProvideFirebaseAuthFactory.provideFirebaseAuth
import com.example.agrilinkup.MainActivity
import com.example.agrilinkup.databinding.ActivitySplashBinding
import com.example.agrilinkup.utils.dataBinding
import com.example.agrilinkup.utils.startActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

//@AndroidEntryPoint
//@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    @Inject
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth=provideFirebaseAuth()
        inIt()
    }

    private fun inIt() {
        setUpSplash()
    }


    private fun setUpSplash() {
        lifecycleScope.launch {
            delay(500)
            if (auth.currentUser != null) {
                startActivity(MainActivity::class.java)
                finish()
            } else {
                startActivity(LoginActivity::class.java)
                finish()
            }
        }
    }
}




