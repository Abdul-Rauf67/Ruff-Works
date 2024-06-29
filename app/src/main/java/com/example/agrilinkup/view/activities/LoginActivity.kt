package com.example.agrilinkup.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.agrilinkup.R
import com.example.agrilinkup.ui.VmAuth
import com.example.agrilinkup.databinding.ActivityLoginBinding
import com.example.agrilinkup.utils.DataState
import com.example.agrilinkup.utils.ProgressDialogUtil.dismissProgressDialog
import com.example.agrilinkup.utils.ProgressDialogUtil.showProgressDialog
import com.example.agrilinkup.utils.setPasswordVisibilityToggle
import com.example.agrilinkup.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    //Note
    //Viewmodel - this code is enough to get viewmodel *insure to add @AndroidEntryPoint on class before using this
    // or when injecting any hilt dependency
    private val vm: VmAuth by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        inIt()
    }


    private fun inIt() {
        setUpUI()
        setOnClickListeners()
        setUpObserver()
    }

    private fun setOnClickListeners() {
        binding.loginBtn.setOnClickListener {
            if (isValid()) {
                login()
            }
        }
        binding.register.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun login() {
        val email = binding.EmailLogin.text.toString().trim()
        val password = binding.passLogin.text.toString().trim()
        lifecycleScope.launch {
            vm.loginWithEmailPass(email, password)
        }
    }

    private fun setUpObserver() {
        vm.loginStatus.observe(this) { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    toast("Loged In successfully")
                    dismissProgressDialog()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                is DataState.Error -> {
                    toast(dataState.errorMessage)
                    dismissProgressDialog()
                }

                is DataState.Loading -> {
                    showProgressDialog(supportFragmentManager)
                }
            }
        }
    }

    private fun setUpUI() {
        setPasswordVisibilityToggle(binding.passLogin)
    }

    private fun isValid(): Boolean {
        val email = binding.EmailLogin
        val password = binding.passLogin

        email.error = null
        password.error = null
        return when {
            email.text.isNullOrEmpty() -> {
                email.error = "Please fill!"
                email.requestFocus()
                false
            }

            password.text.isNullOrEmpty() -> {
                password.error = "Please fill!"
                password.requestFocus()
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches() -> {
                email.error = "Invalid email pattern"
                email.requestFocus()
                false
            }

            else -> {
                true
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }
}

